package com.mobiletsm.osm.data.providers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.travelingsalesman.routing.IVehicle;

import com.mobiletsm.osm.OsmHelper;
import com.mobiletsm.osm.data.MobileInterfaceDataSet;
import com.mobiletsm.osm.data.MobileRoutingInterfaceDataSet;
import com.mobiletsm.osm.data.adapters.RoutingDBAdapter;
import com.mobiletsm.osm.data.searching.POINodeSelector;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileNode;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWayNode;
import com.mobiletsm.routing.Limits;
import com.mobiletsm.routing.Place;

public class DatabaseMDSProvider extends MobileDataSetProvider {

	/* database connection stuff */
	
	public DatabaseMDSProvider(RoutingDBAdapter adapter) {
		super(adapter);
	}

	
	@Override
	public boolean open(String source) {
		if (!isOpen()) {
			((RoutingDBAdapter)adapter).setMaps(poiNodes, streetNodes, 
					completeWays, reducedWays, waysForNodes);
			return adapter.open(source);
		} else {
			return false;
		}
	}
	
	
	@Override
	public boolean isOpen() {
		return (adapter != null && adapter.isOpen());
	}
	
	
	@Override
	public String getInfo() {
		return super.getInfo() + ":DatabaseMDSProvider";
	}
	
	
	@Override
	public void close() {
		if (isOpen()) {
			adapter.close();
		}
	}
	
	
	/*  */	
	
	private boolean routingMapPresent = false;
	

	private Map<Long, Node> poiNodes = Collections.synchronizedMap(new HashMap<Long, Node>());
	
	
	private Map<Long, Node> streetNodes = Collections.synchronizedMap(new HashMap<Long, Node>());
	
	
	private Map<Long, Way> reducedWays = Collections.synchronizedMap(new HashMap<Long, Way>());
	
	
	private Map<Long, Way> completeWays = Collections.synchronizedMap(new HashMap<Long, Way>());
	
	
	private Map<Long, Set<Long>> waysForNodes = Collections.synchronizedMap(new HashMap<Long, Set<Long>>());
	
	
	@Override
	public Place getNearestStreetNode(Place center) {
		return getNearestStreetNode(center, false);
	}
	
	
	/* node cache */
	private final double defaultRadiusToLoad = 300;
	private final double maxRadiusToLoad = 500;
	private Vector<Node> cache = null;
	private Place cacheCenter = null;
	private double cacheRadius = 0;		
	private Place lastQueryPos = null;	
	
	
	@Override
	public Place getNearestStreetNode(Place center, boolean updateCenter) {		
		/* do not search the database if given place is already a street node */
		if (center.isOsmStreetNode() || center.getNearestOsmStreetNodeId() != Place.ID_UNDEFINED) {
			/* get id of nearest street node given by center */
			long nodeId;
			if (center.isOsmStreetNode()) {
				nodeId = center.getOsmNodeId();
			} else {
				nodeId = center.getNearestOsmStreetNodeId();
			}		
			
			if (!streetNodes.containsKey(nodeId)) {
				adapter.loadStreetNodes(nodeId, -1, false);
			}			
			Node node = streetNodes.get(nodeId);
			Place place = new Place(node, true); 
			place.setNearestOsmStreetNodeId(nodeId);
			return place;
		}
				
		/* recalculate cache radius depending on distance moved since last query */
		double newRadius = cacheRadius;
		if (lastQueryPos != null) {
			newRadius = 3 * lastQueryPos.distanceTo(center);
			/* allow the radius to increase but not to decrease */
			if (cacheRadius > newRadius) {
				newRadius = cacheRadius;
			}
			/* don't let the radius exceed a specific value */
			if (newRadius > maxRadiusToLoad) {
				newRadius = maxRadiusToLoad;
			}
		}
		lastQueryPos = center;
		
		double minDist = Double.MAX_VALUE;
        Node minDistNode = null;   
       
        if (cache != null) {
        	
        	double distToCenter = cacheCenter.distanceTo(center);
        	
        	/* if position is still within circle */
        	if (distToCenter < cacheRadius) {
        		
        		Iterator<Node> nodes = cache.iterator();            	
            	while(nodes.hasNext()) {    	        	
    	        	Node node = nodes.next();    	        	
    	        	double dist = center.distanceTo(node.getLatitude(), node.getLongitude());
    	            if (dist < minDist) {
    	                minDist = dist;
    	                minDistNode = node;
    	            }	           
    	        }
            	
            	/* if there is definitely no node outside the circle
            	 * that is closer than the one we found inside */
            	if (minDist + distToCenter < cacheRadius) {
        			if (updateCenter) {
        				center.update(minDistNode, true);
        			}
        			Place place = new Place(minDistNode, true); 
        			place.setNearestOsmStreetNodeId(minDistNode.getId());
        			return place;
            	}
        	}        	
        }         		        
        
        /* initialize a new street node cache */
        if (newRadius > 0) {
        	cacheRadius = newRadius;
        	cacheCenter = center;
	        cache = new Vector<Node>();
        }        
		
		/* get the street node near the given center */
		double radiusToLoad = cacheRadius;
		if (radiusToLoad == 0) {
			radiusToLoad = defaultRadiusToLoad;
		}
        
			System.out.println("DatabaseMDSProvider.getNearestStreetNode(): radiusToLoad = " + radiusToLoad);
		
		if (adapter.loadAllStreetNodesAround(center, radiusToLoad) > 0) {				
			/* find the street node closest to the center */
			for (Node node : streetNodes.values()) {
				double dist = center.distanceTo(node.getLatitude(), node.getLongitude());
	            /* fill the street node cache */
				if (cacheRadius > 0 && dist < cacheRadius) {
	            	cache.add(node);
	            }
				if (dist < minDist) {
					minDist = dist;
					minDistNode = node;
				}
			}		
			if (minDistNode != null) {
				if (updateCenter) {				
					center.update(minDistNode, true);				
					/* calculate and set name of center place */
					//adapter.loadCompleteWaysForNodes(minDistNode.getId(), -1);				
				}
				Place place = new Place(minDistNode, true); 
    			place.setNearestOsmStreetNodeId(minDistNode.getId());
    			return place;
			} else {
				/* no street node could be found
				 * (TODO: this case should never occur, because not finding any 
				 * street node is handled in the if clause one level higher) */
				return null;
			}
		} else {
			/* there are no street nodes around the center */
			return null;
		}
	}
	

	public MobileInterfaceDataSet getRoutingDataSet(long fromNodeId, long toNodeId, IVehicle vehicle) {
		
		/* TODO: add support of vehicles */
		if (vehicle != null) {
			throw new UnsupportedOperationException("getRoutingDataSet(): vehicle not yet supported by DatabaseMDSProvider");
		}
		
		/* load start and destination nodes (with tags) */
		adapter.loadStreetNodes(fromNodeId, toNodeId, true);

		/* load routing graph unless already present */
		if (!routingMapPresent) {
			adapter.loadAllEssentialStreetNodes();
			adapter.loadReducedWays();
			routingMapPresent = true;
		}			
		
		/*  */
		adapter.loadCompleteWaysForNodes(fromNodeId, toNodeId);
		
		long fromWayId = getWayForNode(fromNodeId);		
		long toWayId = getWayForNode(toNodeId);		
		
		adapter.loadAllStreetNodesForWays(fromWayId, toWayId);
		
		setWayNodeDistances(fromWayId);
		setWayNodeDistances(toWayId);

		MobileRoutingInterfaceDataSet dataSet = new MobileRoutingInterfaceDataSet(fromWayId, toWayId, vehicle);
		dataSet.setMaps(streetNodes, completeWays, reducedWays, waysForNodes);		
		
		return dataSet;
	}

	
	
	private void setWayNodeDistances(long wayId) {
		if (wayId == -1) 
			return;		
		Way way = completeWays.get(wayId);
		List<WayNode> wayNodes = way.getWayNodes();		
		Node last = null;
		for (int i = 0; i < wayNodes.size(); i++) {
			MobileWayNode wayNode = (MobileWayNode)wayNodes.get(i);
			Node current = streetNodes.get(wayNode.getNodeId());
			if (last != null) {
				double dist = Place.distance(current.getLatitude(), current.getLongitude(), 
						last.getLatitude(), last.getLongitude());
				wayNode.setDistanceToPredecessor(dist);
			}
			last = current;
		}		
	}
	
	
	
	private long getWayForNode(long nodeId) {
		Set<Long> wayIds = waysForNodes.get(nodeId);
		if (wayIds != null && wayIds.size() == 1)
			return wayIds.iterator().next();
		else
			return -1;
	}


	
	public Place getNearestPOINode(Place center, POINodeSelector selector, Limits limits) {
		
		/* TODO: add support of limits */
		if (limits != null) {
			throw new UnsupportedOperationException("getNearestPOINode(): limits not yet supported by DatabaseMDSProvider");
		}
		
		double minDist = Double.MAX_VALUE;
        Node minDistNode = null; 
		
		if (adapter.loadPOINodes(selector.getPOICode()) > 0) {				
			/* find the street node closest to the center */
			for (Node node : poiNodes.values()) {
				/* skip this node if not allowed by selector */
				if (selector.getPOICode() != null && !selector.isAllowed(null, node)) {
					continue;
				}
				
				double dist = center.distanceTo(node.getLatitude(), node.getLongitude());
	            if (dist < minDist) {
					minDist = dist;
					minDistNode = node;
				}
			}		
			if (minDistNode != null) {
				Place place = new Place(minDistNode, true);
				if (minDistNode instanceof MobileNode) {
					place.setNearestOsmStreetNodeId(((MobileNode)minDistNode).getNearestStreetNodeId());
				}				
				return place;
			} else {
				/* no POI node could be found
				 * (TODO: this case should never occur, because not finding any 
				 * POI node is handled in the if clause one level higher) */
				return null;
			}
		} else {
			/* there are no POI nodes around the center */
			return null;
		}
	}
	

	/* methods not yet supported */
	
	public MobileInterfaceDataSet getCompleteDataSet() {
		throw new UnsupportedOperationException("getFullDataSet() not supported by DatabaseMDSProvider");		
	}
	

	public MobileInterfaceDataSet getPOINodeDataSet(LatLon center, POINodeSelector selector) {
		throw new UnsupportedOperationException("getPOINodeDataSet() not supported by DatabaseMDSProvider");
	}


	public MobileInterfaceDataSet updatePOINodeDataSet(LatLon center, POINodeSelector selector) {
		throw new UnsupportedOperationException("updatePOINodeDataSet() not supported by DatabaseMDSProvider");
	}


	public MobileInterfaceDataSet updateRoutingDataSet(long fromNodeId,	long toNodeId, IVehicle vehicle) {
		throw new UnsupportedOperationException("updateRoutingDataSet() not supported by DatabaseMDSProvider");
	}

		
}
