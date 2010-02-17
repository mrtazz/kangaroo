/**
 * 
 */
package com.mobiletsm.routing;

import org.openstreetmap.travelingsalesman.routing.Route;

import com.mobiletsm.osm.OsmHelper;

/**
 * @author andreaswalz
 *
 */
public class RouteParameter {

	
	public static double UNDEFINED = -1;
	
	
	private double length;
	
	
	private double durationOfTravel;
	
	
	private Route route;
	
	
	private boolean noRouteFound;
	
	
	public RouteParameter(double length, double durationOfTravel, Route route) {
		this.length = length;
		this.durationOfTravel = durationOfTravel;
		this.route = route;
		this.noRouteFound = (route == null);
	}
	
	
	public RouteParameter(double length) {
		this(length, UNDEFINED, null);
	}
	
	
	public RouteParameter(Route route) {
		this(OsmHelper.getRouteLength(route), UNDEFINED, route);
	}
	
	
	public double getLength() {
		return length;
	}
	
	
	public double getDurationOfTravel() {
		return durationOfTravel;
	}
	
	
	public Route getRoute() {
		return route;
	}
	
	
	public boolean getNoRouteFound() {
		return noRouteFound;
	}
	
}
