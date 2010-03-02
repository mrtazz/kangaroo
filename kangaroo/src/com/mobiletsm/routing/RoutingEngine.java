package com.mobiletsm.routing;

import com.mobiletsm.osm.data.searching.POINodeSelector;


/**
 * This interface defines the routing api used to perform routing and
 * navigation operations.
 * 
 * @author Andreas Walz
 *
 */
public interface RoutingEngine {
	
	
	/**
	 * initialize the routing engine and connect to the routing data source
	 * given by the parameter source. 
	 * 
	 * @param source a string describing the routing and navigation data source
	 * @return true if initialization was successful, false otherwise 
	 */
	public boolean init(String source);
	
	
	/**
	 * check if the routing engine has already been initialized. 
	 * @return true if the routing engine has already been initialized, false otherwiese
	 */
	public boolean initialized();
	
	
	/**
	 * shut down the routing engine, disconnect from data source and release all resources
	 */
	public void shutdown();	
		
	
	/**
	 * find a route between the two given places from and to using the specified vehicle.
	 * To find a route, the map is searched for the nearest street nodes from places
	 * from and to and a route is calculated between them. If updatePlaces is true, places
	 * from and to a updated to this street nodes.
	 * 	
	 * @param from
	 * @param to
	 * @param vehicle
	 * @param updatePlaces
	 * @return
	 */
	public RouteParameter routeFromTo(Place from, Place to, Object vehicle, boolean updatePlaces);
	
	
	/**
	 * find a route between the two given places from and to using the specified vehicle.
	 * @param from
	 * @param to
	 * @param vehicle
	 * @return
	 */
	public RouteParameter routeFromTo(Place from, Place to, Object vehicle);
		
	
	public Place getNearestPOINode(Place center, Object selector, Limits limits);
		
	
	public Place getNearestStreetNode(Place center);
	
	
}
