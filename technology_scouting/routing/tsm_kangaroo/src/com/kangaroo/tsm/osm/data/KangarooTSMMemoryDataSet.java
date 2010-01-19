/**
 * 
 */
package com.kangaroo.tsm.osm.data;

import java.util.Iterator;
import java.util.Vector;

import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import android.util.Log;

/**
 * @author andreaswalz
 *
 */
public class KangarooTSMMemoryDataSet extends MemoryDataSet {

	/* circle of pre-loaded nodes */
	private Vector<Node> nearestNodes = null;
	private LatLon center = null;
	private double radius = 0;	
	
	private LatLon lastQueryPos = null;

	
	@Override
	public Node getNearestNode(final LatLon aLastGPSPos, final Selector aSelector) {      
	
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
        
    	Iterator<Node> nodes = getNodes(null);	
        
        while(nodes.hasNext()) {        	
        	Node node = nodes.next();        	
        	if (aSelector != null && !aSelector.isAllowed(this, node))
                continue;
            LatLon pos = new LatLon(node.getLatitude(), node.getLongitude());
            double dist = pos.distance(aLastGPSPos);
            if (radius > 0 && dist < radius)
            	nearestNodes.add(node);            
            if (dist < minDist) {
                minDist = dist;
                minDistNode = node;
            }	           
        }
        
        return minDistNode;
    }


}
