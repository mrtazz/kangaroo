/**
 * 
 */
package com.mobiletsm.routing;

import java.util.Iterator;
import java.util.List;

import org.openstreetmap.osm.Tags;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.travelingsalesman.routing.Route;
import org.openstreetmap.travelingsalesman.routing.Route.RoutingStep;

import com.mobiletsm.osm.OsmHelper;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;

/**
 * @author andreaswalz
 *
 */
public abstract class RouteParameter {

	
	/**
	 * static value used to specify undefined parameters
	 */
	public static double UNDEFINED = -1;
	
	
	/** 
	 * length of route in meters 
	 */
	protected double length;
	
	
	/** 
	 * time it takes to travel the route in minutes
	 */
	protected double durationOfTravel;
	
	
	/**
	 * an object describing the route
	 */
	protected Object route;
	
	
	/**
	 * an object describing the vehicle used to travel this route
	 */
	protected Object vehicle;
	
	
	/**
	 * true if no route could be found, false if a route was found
	 */
	protected boolean noRouteFound;
	
	
	public RouteParameter(Object route) {
		this(route, null);
	}
	
	
	public RouteParameter(Object route, Object vehicle) {
		super();
		update(route, vehicle);
	}
	
	
	private void update(Object route, Object vehicle) {
		this.route = route;
		this.vehicle = vehicle;
		this.noRouteFound = (route == null);
		updateRouteParameter(route, vehicle);
	}
	
	
	/**
	 * returns the length of the route in meters
	 * @return
	 */
	public double getLength() {
		return length;
	}
	
	
	/**
	 * returns the time it will take to travel the route in minutes 
	 * @return
	 */
	public double getDurationOfTravel() {
		return durationOfTravel;
	}
	
	
	/**
	 * returns true if no route was found
	 * @return
	 */
	public boolean getNoRouteFound() {
		return noRouteFound;
	}


	/* methods to be implemented */
	
	protected abstract void updateRouteParameter(Object route, Object vehicle);
	
}
