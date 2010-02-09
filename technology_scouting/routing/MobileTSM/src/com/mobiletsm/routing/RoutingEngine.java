package com.mobiletsm.routing;

import com.mobiletsm.osm.data.searching.POINodeSelector;



public interface RoutingEngine {
	
	
	public boolean init();
	
	
	public boolean initialized();
	
	
	public void shutdown();	
		
	
	public RouteParameter routeFromTo(Place from, Place to, Vehicle vehicle);
	
	
	public Place getNearestPOINode(Place center, POINodeSelector selector, Limits limits);
	
	
	public Place getNearestStreetNode(Place center);
	
	
}
