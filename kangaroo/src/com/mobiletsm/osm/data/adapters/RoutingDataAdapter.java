package com.mobiletsm.osm.data.adapters;

import java.util.List;

import com.mobiletsm.osm.data.searching.POICode;
import com.mobiletsm.routing.Place;

public interface RoutingDataAdapter {

	/* methods to be implemented by RoutingDataAdapters */
	
	public boolean open(String source);
	
	
	public boolean isOpen();
	
	
	public void close();
	
	
	public int loadAllStreetNodesAround(Place center, double radius);
	
	
	public void loadAllStreetNodesForWays(long fromWayId, long toWayId);
	
	
	public void loadReducedWays();
	
	
	public void loadReducedWays(List<Long> ways);
	
	
	public List<Long> loadCompleteWaysForNodes(long fromNodeId, long toNodeId);
	
	
	public void loadCompleteWay(long wayId);
	
	
	public void loadAllEssentialStreetNodes();
	
	
	public int loadPOINodes(POICode poiCode);
	
	
	public void loadStreetNodes(long nodeId1, long nodeId2, boolean loadTags);
	
}
