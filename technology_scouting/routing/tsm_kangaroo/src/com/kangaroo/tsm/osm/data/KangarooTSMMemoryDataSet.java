/**
 * 
 */
package com.kangaroo.tsm.osm.data;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osm.data.searching.NearestStreetSelector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import com.kangaroo.osmosis.core.domain.v0_6.KangarooOSMNode;
import com.kangaroo.routing.KangarooProjection;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author andreaswalz
 *
 */
public class KangarooTSMMemoryDataSet extends MemoryDataSet {
	
	private SQLiteDatabase database = null;
	
	
	public void openDatabase(String filename) {
		database = SQLiteDatabase.openOrCreateDatabase(filename, null);
	}
	
	
	public void closeDatabase() {
		database.close();
	}
	
	
	public Node getNearestStreetNode(final LatLon aLastGPSPos) {      
		
		double newRadius = radius;
		if (lastQueryPos != null) {
			newRadius = 5 * lastQueryPos.distance(aLastGPSPos);
			if (radius > newRadius)
				newRadius = radius;
		}
		lastQueryPos = aLastGPSPos;
		
		double minDist = Double.MAX_VALUE;
        Node minDistNode = null;   
       
        if (nearestNodes != null) {
        	
        	double distToCenter = center.distance(aLastGPSPos);
        	
        	/* if position is still within circle */
        	if (distToCenter < radius) {
        		
        		Iterator<Node> nodes = nearestNodes.iterator();            	
            	while(nodes.hasNext()) {    	        	
    	        	Node node = nodes.next();    	        	
    	        	LatLon pos = new LatLon(node.getLatitude(), node.getLongitude());
    	            double dist = pos.distance(aLastGPSPos);    	            
    	            if (dist < minDist) {
    	                minDist = dist;
    	                minDistNode = node;
    	            }	           
    	        }
            	
            	/* if there is definitely no node outside the circle
            	 * that is closer than the one we found inside */
            	if (minDist + distToCenter < radius)
            		return minDistNode;            	
        	}        	
        }         		        
        
        if (newRadius > 0) {
        	radius = newRadius;
        	center = aLastGPSPos;
	        nearestNodes = new Vector<Node>();
        }         
               
        Cursor nodeCursor = database.query("nodes", new String[] {"node_id", "lat", "lon"}, 
        		"isstreetnode = 1", null, null, null, null);
    	int minDistNodeId = -1;
    	if (nodeCursor.getCount() > 0) {
    		int col_id = nodeCursor.getColumnIndex("node_id");
    		int col_lat = nodeCursor.getColumnIndex("lat");
    		int col_lon = nodeCursor.getColumnIndex("lon");        	
        	for(nodeCursor.moveToFirst(); !nodeCursor.isAfterLast(); nodeCursor.moveToNext()) {
        		LatLon pos = new LatLon(nodeCursor.getDouble(col_lat), nodeCursor.getDouble(col_lon));
                double dist = pos.distance(aLastGPSPos);
                if (radius > 0 && dist < radius)
                	nearestNodes.add(this.getNodeByID(nodeCursor.getInt(col_id)));   
                if (dist <= minDist) {
                	minDist = dist;
                	minDistNodeId = nodeCursor.getInt(col_id);
                }        		
			}
        }
        
        if (minDistNodeId != -1)
        	return this.getNodeByID(minDistNodeId);
        else 
        	return null;
    }

	
	
	@Override
	public Node getNearestNode(final LatLon aLastGPSPos, final Selector aSelector) {
		return getNearestStreetNode(aLastGPSPos);
	}
	
	/* circle of pre-loaded nodes */
	private Vector<Node> nearestNodes = null;
	private LatLon center = null;
	private double radius = 0;	
	
	private LatLon lastQueryPos = null;

//	
//	@Override
//	public Node getNearestNode(final LatLon aLastGPSPos, final Selector aSelector) {      
//	
//		double newRadius = radius;
//		if (lastQueryPos != null) {
//			newRadius = 5 * lastQueryPos.distance(aLastGPSPos);
//			if (radius > newRadius)
//				newRadius = radius;
//		}
//		lastQueryPos = aLastGPSPos;
//		
//		double minDist = Double.MAX_VALUE;
//        Node minDistNode = null;   
//       
//        if (nearestNodes != null) {
//        	
//        	double distToCenter = center.distance(aLastGPSPos);
//        	
//        	/* if position is still within circle */
//        	if (distToCenter < radius) {
//        		
//        		Iterator<Node> nodes = nearestNodes.iterator();            	
//            	while(nodes.hasNext()) {    	        	
//    	        	Node node = nodes.next();    	        	
//    	        	LatLon pos = new LatLon(node.getLatitude(), node.getLongitude());
//    	            double dist = pos.distance(aLastGPSPos);    	            
//    	            if (dist < minDist) {
//    	                minDist = dist;
//    	                minDistNode = node;
//    	            }	           
//    	        }
//            	
//            	/* if there is definitely no node outside the circle
//            	 * that is closer than the one we found inside */
//            	if (minDist + distToCenter < radius)
//            		return minDistNode;            	
//        	}        	
//        }         		        
//        
//        if (newRadius > 0) {
//        	radius = newRadius;
//        	center = aLastGPSPos;
//	        nearestNodes = new Vector<Node>();
//        }        
//        
//    	Iterator<Node> nodes = getNodes(null);	
//    	
//    	while(nodes.hasNext()) {        	
//        	Node node = nodes.next(); 
//        	if (aSelector != null && !aSelector.isAllowed(this, node))
//                continue;        	
//        	LatLon pos = new LatLon(node.getLatitude(), node.getLongitude());
//            double dist = pos.distance(aLastGPSPos);
//        	if (radius > 0 && dist < radius)
//            	nearestNodes.add(node);            
//            if (dist < minDist) {
//                minDist = dist;
//                minDistNode = node;
//            }	           
//        }    	
//    	
//        return minDistNode;
//    }

	/*
	@Override
	public Iterator<Way> getWaysForNode(long node_de) {
		
			//Log.v("MyTag", "getWaysForNode()");
		
		Collection<Way> ways = new LinkedList<Way>();		
		Cursor nodeCursor = database.query("nodes", new String[] {"node_ways"}, 
				"node_id=" + node_de, null, null, null, null);		
    	if (nodeCursor.getCount() > 0) {
    		int col_id = nodeCursor.getColumnIndex("node_ways");
    		nodeCursor.moveToFirst();
    		String str = nodeCursor.getString(col_id);
    		for (int i = 0; i < str.length(); i+=8) {
    			String long_str = str.substring(i, i+8);
		    	ways.add(this.getWaysByID(Long.decode("0x" + long_str)));  
		    }    		
        }    	
    	nodeCursor.close();
    	return ways.iterator();
	}
	*/
	
}
