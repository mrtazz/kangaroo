/**
 * 
 */
package com.mobiletsm.routing;

import java.util.Locale;

/**
 * @author andreaswalz
 *
 */
public abstract class RouteParameter {
	
	
	public static int ROUTE_PARAMETER_NO_ROUTE_FOUND = 1;
	
	
	public static int ROUTE_PARAMETER_ONE_POINT_ROUTE = 2;
	
	
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
	
	
	/**
	 * start place of route
	 */
	protected Place startPlace = null;
		
	
	/**
	 * destination place of route
	 */
	protected Place destinationPlace = null;
		

	public RouteParameter(int type, Object vehicle) {
		
		if (type == ROUTE_PARAMETER_NO_ROUTE_FOUND) {
			this.route = null;
			this.vehicle = vehicle;
			this.noRouteFound = true;			
		} else if (type == ROUTE_PARAMETER_ONE_POINT_ROUTE) {
			this.route = null;
			this.vehicle = vehicle;
			this.noRouteFound = false;
			this.length = 0;
			this.durationOfTravel = 0;
		} else {
			throw new RuntimeException("RouteParameter(): type undefined");
		}
		
	}
	
	
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
	 * @return the startPlace
	 */
	public Place getStartPlace() {
		return startPlace;
	}


	/**
	 * @param startPlace the startPlace to set
	 */
	public void setStartPlace(Place startPlace) {
		this.startPlace = startPlace;
	}


	/**
	 * @return the destinationPlace
	 */
	public Place getDestinationPlace() {
		return destinationPlace;
	}


	/**
	 * @param destinationPlace the destinationPlace to set
	 */
	public void setDestinationPlace(Place destinationPlace) {
		this.destinationPlace = destinationPlace;
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
		
	
	/**
	 * @return the route
	 */
	public Object getRoute() {
		return route;
	}


	/**
	 * @return the vehicle
	 */
	public Object getVehicle() {
		return vehicle;
	}


	@Override
	public String toString() {
//		if (getNoRouteFound()) {
//			return "RouteParameter: {no route found}";
//		} else {
			
			String from = null;
			String to = null;
			if (startPlace != null) {
				from = startPlace.toString();
			}
			if (destinationPlace != null) {
				to = destinationPlace.toString();
			}
			
			//StringBuffer buf = new StringBuffer("RouteParameter: {route");
			StringBuffer buf = new StringBuffer("RouteParameter: {");
			if (getNoRouteFound()) {
				buf.append("no route found");
			} else {
				buf.append("route");
			}
			
			if (from != null) {
				buf.append(" from '" + from + "'");
			}

			if (to != null) {
				buf.append(" to '" + to + "'");
			}
			
			buf.append(": length = " + lengthToString(getLength()) + ", " +
				"duration = " + durationToString(getDurationOfTravel()) + "}");				
			
			return buf.toString();
//		}
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
		/* round up to next integer */
		duration = Math.ceil(duration);
		
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
