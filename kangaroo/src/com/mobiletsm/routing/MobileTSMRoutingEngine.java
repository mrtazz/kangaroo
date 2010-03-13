package com.mobiletsm.routing;

import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.travelingsalesman.routing.IRouter;
import org.openstreetmap.travelingsalesman.routing.Route;
import org.openstreetmap.travelingsalesman.routing.routers.MultiTargetDijkstraRouter;

import com.mobiletsm.osm.data.MobileInterfaceDataSet;
import com.mobiletsm.osm.data.adapters.RoutingAndroidSQLiteAdapter;
import com.mobiletsm.osm.data.adapters.RoutingSQLiteAdapter;
import com.mobiletsm.osm.data.providers.DatabaseMDSProvider;
import com.mobiletsm.osm.data.providers.MobileDataSetProvider;
import com.mobiletsm.osm.data.searching.POINodeSelector;
import com.mobiletsm.routing.metrics.MobileRoutingMetric;
import com.mobiletsm.routing.routers.MobileMultiTargetDijkstraRouter;


public class MobileTSMRoutingEngine implements RoutingEngine {
	
	
	private boolean useRoutingCache = false;
	
	
	private RoutingCache routingCache = null;
	
	
	private MobileDataSetProvider provider = null;

	
	@Override
	/**
	 * initialize the routing engine and set its map data source.
	 * The parameter <code>source</code> has to specify a SQLite
	 * database file path, for example "file:/sdcard/map.db".
	 * @param source the SQLite database file path
	 * @return true if initialization was successful, false otherwise
	 */
	public boolean init(String source) {
		if (!initialized()) {
			if (provider != null) {
				provider.close();
			}			
			provider = new DatabaseMDSProvider(new RoutingAndroidSQLiteAdapter());
			return provider.open(source);
		} else {
			return false;
		}
	}


	@Override
	public boolean initialized() {
		return (provider != null && provider.isOpen());
	}

	
	@Override
	public String getInfo() {
		if (initialized()) {
			return "MobileTSMRoutingEngine(provider = " + provider.getInfo() + ")";
		} else {
			return "MobileTSMRoutingEngine";
		}
	}
	
	
	@Override
	public RouteParameter routeFromTo(Place from, Place to, Object vehicle) {
		return routeFromTo(from, to, vehicle, true);
	}
	
	
	@Override
	public RouteParameter routeFromTo(Place from, Place to, Object vehicle, boolean updatePlaces) {
		/* only accept MobileTSM Vehicle objects */
		if (!(vehicle instanceof Vehicle)) {
			throw new RuntimeException("MobileTSMRoutingEngine.routeFromTo(): Not a Vehicle");
		}
		
		/* look up this routing order in the routing cache if enabled */
		if (useRoutingCache && routingCache != null) {
			RouteParameter cacheRoute = routingCache.getElement(from, to, vehicle);
			if (cacheRoute != null) {
					System.out.println("MobileTSMRoutingEngine.routeFromTo(): using route from routing cache");
				return cacheRoute;
			}
		}		
		
		/* find the street nodes that are closest to start and 
		 * destination places */
		Place fromNode = provider.getNearestStreetNode(from, updatePlaces);
		Place toNode = provider.getNearestStreetNode(to, updatePlaces);
		
		if (fromNode == null || toNode == null) {
			/* return that no route could be found, because start and/or
			 * destination places could not be resolved to street nodes */
			return new MobileTSMRouteParameter(null);
		} else {
			long fromNodeId = fromNode.getOsmNodeId();
			long toNodeId = toNode.getOsmNodeId();
			
			/* build routing data set */
			MobileInterfaceDataSet routingDataSet = provider.getRoutingDataSet(fromNodeId, toNodeId, null);		
			
			/* set up the router */
			IRouter router = new MobileMultiTargetDijkstraRouter();
			router.setMetric(new MobileRoutingMetric());
			
			/* calculate the route */
			Route route = router.route(routingDataSet, routingDataSet.getNodeByID(toNodeId), 
					routingDataSet.getNodeByID(fromNodeId), (Vehicle)vehicle);
			
			/* return the route parameter */
			RouteParameter result = new MobileTSMRouteParameter(route, vehicle);
			result.setStartPlace(from);
			result.setDestinationPlace(to);
			
			/* store the route in the routing cache if enabled */
			if (useRoutingCache && routingCache != null) {
				routingCache.putElement(result);
				System.out.println("MobileTSMRoutingEngine.routeFromTo(): route put into routing cache " +
						"(# routes in cache = " + routingCache.size() + ")");
			}
			
			return result; 
		}
	}

	
	@Override
	public Place getNearestPOINode(Place center, Object selector, Limits limits) {
		if (!(selector instanceof POINodeSelector)) {
			throw new RuntimeException("MobileTSMRoutingEngine.getNearestPOINode(): Not a POINodeSelector");
		}
		
		POINodeSelector poiNodeSelector = (POINodeSelector)selector;
		
		return provider.getNearestPOINode(center, poiNodeSelector, limits);		
	}

	
	@Override
	public Place getNearestStreetNode(Place center) {
		throw new UnsupportedOperationException("getNearestStreetNode() not yet supported by MobileTSMRoutingEngine");
	}
	

	@Override
	public void shutdown() {
		if (initialized()) {
			provider.close();
		}
		
		/* clear routing cache */
		clearRoutingCache();
	}


	@Override
	public void disableRoutingCache() {
		useRoutingCache = false;		
	}


	@Override
	public void enableRoutingCache() {
		useRoutingCache = true;
		if (routingCache == null) {
			routingCache = new RoutingCache();
		}
	}


	@Override
	public void clearRoutingCache() {
		if (routingCache != null) {
			routingCache.clear();
		}
	}

}
