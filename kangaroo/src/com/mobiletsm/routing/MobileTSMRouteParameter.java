package com.mobiletsm.routing;

import java.util.Iterator;
import java.util.List;

import org.openstreetmap.osm.Tags;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.travelingsalesman.routing.Route;
import org.openstreetmap.travelingsalesman.routing.Route.RoutingStep;

import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;

public class MobileTSMRouteParameter extends RouteParameter {

	
	public MobileTSMRouteParameter(Object route, Object vehicle) {
		super(route, vehicle);
	}

	
	public MobileTSMRouteParameter(Object route) {
		super(route);
	}
	

	/**
	 * 
	 * @param route
	 * @param vehicle
	 */
	protected void updateRouteParameter(Object route, Object vehicle) {
		
		/* throw exception if route is not of type Route */
		if (route != null && !(route instanceof Route)) {
			throw new RuntimeException("MobileTSMRouteParameter.updateRouteParameter(): Not a Route");
		}

		/* throw exception if vehicle is not of type Vehicle */
		if (vehicle != null && !(vehicle instanceof Vehicle)) {
			throw new RuntimeException("MobileTSMRouteParameter.updateRouteParameter(): Not a Vehicle");
		}
		
		/* duration of travel of the route in minutes */
		double duration = 0;
		/* length of the route in meters */
		double length = 0;
		/* maximum speed on a routing step in km/h */
		double maxSpeed;
		/* distance of a routing step in meters */
		double dist;
		
		if (route == null) {
			/* no parameters to calculate if no route is given */
			this.length = PARAMETER_UNDEFINED;
			this.durationOfTravel = PARAMETER_UNDEFINED;
			return;
		}
		
		/* get the routing steps */
		List<RoutingStep> steps = ((Route)route).getRoutingSteps();
		Iterator<RoutingStep> steps_itr = steps.iterator();
		
		String lastStreetName = null;
		
		/* iterate over all routing steps */
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
				/* add duration of travel for this routing step */
				maxSpeed = ((Vehicle)vehicle).getMaxSpeedOnWay(way);
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
			this.durationOfTravel = PARAMETER_UNDEFINED;
		} else {
			this.durationOfTravel = duration;
		}
	}
	
}
