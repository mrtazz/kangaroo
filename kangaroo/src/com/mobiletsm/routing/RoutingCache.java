package com.mobiletsm.routing;

import java.util.HashMap;


public class RoutingCache {

	
	// TODO: handle route reversal
	
	
	HashMap<Place, HashMap<Place, RouteParameter>> cache = 
		new HashMap<Place, HashMap<Place, RouteParameter>>();

	
	public void clear() {
		cache.clear();
	}
	
	
	public boolean hasElement(Place from, Place to, Object vehicle) {
		return getElement(from, to, vehicle) != null;
	}
	
	
	public RouteParameter getElement(Place from, Place to, Object vehicle) {
		HashMap<Place, RouteParameter> map = cache.get(from);
		if (map != null) {
			RouteParameter route = map.get(to);
			if (route != null) {
				Object routeVehicle = route.getVehicle();
				if (routeVehicle.equals(vehicle)) {
					return route;
				} else {
					return null;
				}
			} else { 
				return null;
			}
		} else {
			return null;
		}
	}
	
	
	public void putElement(RouteParameter route) {		
		/* check if route parameters are given */
		if (route == null) {
			throw new RuntimeException("RoutingCache.putElement(): no route parameter given");
		}
		
		/* check if route parameters specify start and destination places */
		if (route.getStartPlace() == null || route.getDestinationPlace() == null) {
			throw new RuntimeException("RoutingCache.putElement(): no start and/or destination place given");
		}
		
		HashMap<Place, RouteParameter> map = cache.get(route.getStartPlace());
		
		if (map != null) {
			map.put(route.getDestinationPlace(), route);			
		} else {
			map = new HashMap<Place, RouteParameter>();
			map.put(route.getDestinationPlace(), route);
			cache.put(route.getStartPlace(), map);
		}
	}
	
	
}
