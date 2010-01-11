/**
 * 
 */
package com.kangaroo.routing;

import java.net.URI;

import android.location.LocationListener;

/**
 * This defines the interface between the routing 
 * engine and the rest of the application that makes
 * use of it. 
 * @author Andreas Walz
 *
 */
public interface IRoutingEngine {
	
	/**
	 * 
	 * @param aDataSource
	 */
	public void setDataSource(URI aDataSource);
	
	
	/**
	 * 
	 * @return
	 */
	public URI getDataSource();
	
	
	/**
	 * Initialize the routing engine
	 * @throws Exception 
	 */
	public void Init(IProgressListener progressListener) throws Exception;
	
	
	/**
	 * Calculate the fastest route starting from the current
	 * position to an arbitrary place. 
	 * @param destination	place to route to
	 * @param vehicle	the vehicle that is to be used to follow the route
	 * @return a RouteParameter object containing the parameter of the fastest 
	 * route. null if there is no route compatible with the specified vehicle
	 */
	public RouteParameter routeTo(Place destination, Vehicle vehicle);
	
	/**
	 * 
	 * @param start
	 * @param destination
	 * @param vehicle
	 * @return
	 */
	public RouteParameter routeFromTo(Place start, Place destination, Vehicle vehicle);
	

	/**
	 * 
	 * @param position
	 * @param selector
	 * @param limits
	 * @return
	 */
	public Place getNearestAmenity(Place position, AmenitySelector selector, Limits limits);
	
	
	/**
	 * Return the location listener that listens to
	 * changes of the location.
	 * @return
	 */
	public LocationListener getLocationListener();
	
	
	/**
	 * return a string describing the routing engine and its data source
	 * @return
	 */
	public String getInfo();

}
