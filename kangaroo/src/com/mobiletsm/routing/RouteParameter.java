/**
 * 
 */
package com.mobiletsm.routing;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
	public static double PARAMETER_UNDEFINED = -1;
	
	
	/**
	 * 
	 */
	public static String STRING_PARAMETER_UNDEFINIED = "?";
	
	
	/** 
	 * length of route in meters 
	 */
	protected double length = PARAMETER_UNDEFINED;
	
	
	/** 
	 * time it takes to travel the route in minutes
	 */
	protected double durationOfTravel = PARAMETER_UNDEFINED;
	
	
	/**
	 * an object describing the route
	 */
	protected Object route = null;
	
	
	/**
	 * an object describing the vehicle used to travel this route
	 */
	protected Object vehicle = null;
	
	
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
	
	
	@Override
	public String toString() {
		if (getNoRouteFound()) {
			return "RouteParameter: {no route found}";
		} else {
			return "RouteParameter: {route found: length = " + lengthToString(getLength()) + ", " +
					"duration = " + durationToString(getDurationOfTravel()) + "}";
		}
	}
	
	

	/* methods to be implemented */
	
	protected abstract void updateRouteParameter(Object route, Object vehicle);
	
	
	/* static methods */
	
	/**
	 * convert the given length to a String
	 * @param length
	 * @return
	 */
	public static String lengthToString(double length) {
		if (length == PARAMETER_UNDEFINED) {
			return STRING_PARAMETER_UNDEFINIED; 
		} else {
			if (length < 1000) {
				return String.format(Locale.US, "%.0fm", length);
			} else {
				return String.format(Locale.US, "%.1fkm", length / 1000);
			}
		}		
	}
	
	
	/**
	 * convert the given duration to a String
	 * @param duration
	 * @return
	 */
	public static String durationToString(double duration) {
		if (duration == PARAMETER_UNDEFINED) {
			return STRING_PARAMETER_UNDEFINIED; 
		} else {
			if (duration < 60) {
				return String.format(Locale.US, "%.0fmin", duration);
			} else {
				int hours = (int)(duration / 60);
				int minutes = (int)(duration - 60 * hours);						
				return String.format(Locale.US, "%dh%02dmin", hours, minutes);
			}
		}		
	}
}
