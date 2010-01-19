/**
 * 
 */
package com.kangaroo.routing;

import java.net.URI;

import org.openstreetmap.osm.data.Selector;

import android.location.LocationListener;

/**
 * This defines the interface between the routing 
 * engine and the rest of the application that makes
 * use of it. 
 * @author Andreas Walz
 *
 */
public interface KangarooRoutingEngine {
	
	/**
	 * 
	 */
	public final static int INITIALIZING_ROUTING_ENGINE_ID = 1;
	public final static String INITIALIZING_ROUTING_ENGINE_MSG = 
		"initializing routing engine...";
	public final static String INITIALIZING_ROUTING_ENGINE_DONE =
		INITIALIZING_ROUTING_ENGINE_MSG + "done";
	public final static String INITIALIZING_ROUTING_ENGINE_FAILED =
		INITIALIZING_ROUTING_ENGINE_MSG + "failed";

	
	/**
	 * 
	 */
	public final static int LOOKING_FOR_NEAREST_NODE_ID = 2;
	public final static String LOOKING_FOR_NEAREST_NODE_MSG = 
		"looking for nearest node...";
	public final static String LOOKING_FOR_NEAREST_NODE_DONE =
		LOOKING_FOR_NEAREST_NODE_MSG + "done";
	public final static String LOOKING_FOR_NEAREST_NODE_FAILED =
		LOOKING_FOR_NEAREST_NODE_MSG + "failed";
	
	
	/**
	 * set the data source URI where data is initially read from
	 * @param aDataSource
	 */
	public void setDataSource(URI aDataSource);
		
	
	/**
	 * set the status listener that will be informed about changes in
	 * the routing engine's status
	 * @param listener
	 */
	public void setStatusListener(StatusListener listener);
	
	
	/**
	 * Initialize the routing engine
	 * @throws Exception 
	 * @throws Exception 
	 */
	public void init();
	
	
	/**
	 * stop the routing engine and release all used resources
	 * @throws Exception 
	 */
	public void shutdown();
	
	
	/**
	 * 
	 * @param start
	 * @param destination
	 * 			place to route to
	 * @param vehicle
	 * 			the vehicle that is to be used to follow the route
	 * @return a RouteParameter object containing the parameter of the fastest 
	 * route. null if there is no route compatible with the specified vehicle
	 */
	public RouteParameter routeFromTo(Place start, Place destination, Vehicle vehicle);
	

	/**
	 * 
	 * @param position
	 * @param selector
	 * @param limits
	 * @return
	 */
	public Place getNearestNode(Place position, Selector selector, Limits limits);
		
	
	/**
	 * return a string describing the routing engine and its data source
	 * @return
	 */
	public String getInfo();

}
