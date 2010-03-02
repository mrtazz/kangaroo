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
		updateRouteParameter(route, vehicle);
	}
	
	
	public RouteParameter(Route route) {
		this(route, null);
	}
	
	
	public RouteParameter(Route route, Vehicle vehicle) {
		super();
		update(route, vehicle);
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
	 * returns the Route object of Traveling Salesman
	 * @return
	 */
	public Route getRoute() {
		return route;
	}
	
	
	/**
	 * returns true if no route was found
	 * @return
	 */
	public boolean getNoRouteFound() {
		return noRouteFound;
	}


	/**
	 * 
	 * @param route
	 * @param vehicle
	 */
	public void updateRouteParameter(Route route, Vehicle vehicle) {
		/* duration of travel of the route in minutes */
		double duration = 0;
		/* length of the route in meters */
		double length = 0;
		/* maximum speed on a routing step in km/h */
		double maxSpeed;
		/* distance of a routing step in meters */
		double dist;
		
		if (route == null) {
			this.length = UNDEFINED;
			this.durationOfTravel = UNDEFINED;
			return;
		}
		
		List<RoutingStep> steps = route.getRoutingSteps();
		Iterator<RoutingStep> steps_itr = steps.iterator();
		
		String lastStreetName = null;
		
		while (steps_itr.hasNext()) {
			RoutingStep step = steps_itr.next();
			Way way = step.getWay();			
			
			if (way instanceof MobileWay) {
				dist = ((MobileWay)way).getPathLength(step.getStartNode().getId(), step.getEndNode().getId());
				length += dist;
			} else {
				dist = step.distanceInMeters();
				length += dist;
			}
			
			if (vehicle != null) {
				/* add duration of travel for this rouitng step */
				maxSpeed = getMaxSpeed(way, vehicle);
				duration += (dist / 1000) / (maxSpeed / 60);
				String wayName = WayHelper.getTag(way.getTags(), Tags.TAG_NAME);
				/* add 15 seconds for every corner */
				if (lastStreetName != null && wayName != null) {
					if (!lastStreetName.equals(wayName)) {
						duration += (15.0 / 60.0);
					}
				} else if (lastStreetName != null || wayName != null) {
					duration += (15.0 / 60.0);
				}
				lastStreetName = wayName;
			}
		}
		
		this.length = length;
		if (vehicle == null) {
			this.durationOfTravel = UNDEFINED;
		} else {
			this.durationOfTravel = duration;
		}
	}


	/**
	 * returns the maximum speed the given vehicle can move on
	 * the given way in km/h
	 * @param way
	 * @param vehicle
	 * @return maximum speed the given vehicle can move on the given way in km/h
	 */
	public static double getMaxSpeed(Way way, Vehicle vehicle) {
		double vehicleMaxSpeed = vehicle.getMaxSpeed();
		
		String maxSpeedStr = WayHelper.getTag(way.getTags(), "maxspeed");
		
		if (maxSpeedStr != null) {
			try {
				double wayMaxSpeed = Double.parseDouble(maxSpeedStr);
				if (wayMaxSpeed < vehicleMaxSpeed)
					return wayMaxSpeed;
				else
					return vehicleMaxSpeed;
			} catch (NumberFormatException e) {
				return vehicleMaxSpeed;
			}
		} else {
			String highway = WayHelper.getTag(way.getTags(), Tags.TAG_HIGHWAY);
			
			if (highway != null) {
				if (highway.equals("residential"))
					return 50;
				else
					return vehicleMaxSpeed;
			} else {
				return vehicleMaxSpeed;
			}
		}		
	}
	
}
