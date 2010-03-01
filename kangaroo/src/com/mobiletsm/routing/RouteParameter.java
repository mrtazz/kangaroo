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
	
	
	/** 
	 * length of route in meters 
	 */
	private double length;
	
	
	/** 
	 * time it takes to travel the route in minutes
	 */
	private double durationOfTravel;
	
	
	private Route route;
	
	
	private Vehicle vehicle;
	
	
	private boolean noRouteFound;
	
	
	private void update(Route route, Vehicle vehicle) {
		this.route = route;
		this.vehicle = vehicle;
		this.noRouteFound = (route == null);

		if (route != null) {
			this.length = OsmHelper.getRouteLength(route);
			if (vehicle != null) {
				this.durationOfTravel = OsmHelper.getDurationOfTravel(route, vehicle);
			} else {
				this.durationOfTravel = UNDEFINED;
			}
		} else {
			this.length = UNDEFINED;
			this.durationOfTravel = UNDEFINED;
		}
	}
	
	
	public RouteParameter(Route route) {
		super();
		update(route, null);
	}
	
	
	public RouteParameter(Route route, Vehicle vehicle) {
		super();
		update(route, vehicle);
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
