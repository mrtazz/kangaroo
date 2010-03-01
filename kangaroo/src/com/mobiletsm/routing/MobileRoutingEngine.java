package com.mobiletsm.routing;

import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.travelingsalesman.routing.IRouter;
import org.openstreetmap.travelingsalesman.routing.Route;
import org.openstreetmap.travelingsalesman.routing.routers.MultiTargetDijkstraRouter;

import com.mobiletsm.osm.data.MobileInterfaceDataSet;
import com.mobiletsm.osm.data.adapters.MDSAndroidDatabaseAdapter;
import com.mobiletsm.osm.data.providers.DatabaseMDSProvider;
import com.mobiletsm.osm.data.providers.MobileDataSetProvider;
import com.mobiletsm.osm.data.searching.POINodeSelector;
import com.mobiletsm.routing.metrics.MobileRoutingMetric;
import com.mobiletsm.routing.routers.MobileMultiTargetDijkstraRouter;
import com.mobiletsm.routing.statuschange.SubJobDoneStatusChange;
import com.mobiletsm.routing.statuschange.SubJobStartedStatusChange;

public class MobileRoutingEngine implements RoutingEngine {
	
	
	private MobileDataSetProvider provider = null;

	
	@Override
	public boolean init(String source) {
		if (!initialized()) {
			if (provider != null) {
				provider.close();
			}			
			provider = new DatabaseMDSProvider(new MDSAndroidDatabaseAdapter());
			return provider.open(source);
		} else {
			return false;
		}
	}


	@Override
	public boolean initialized() {
		return provider.isOpen();
	}

	
	@Override
	public RouteParameter routeFromTo(Place from, Place to, Vehicle vehicle) {
		return routeFromTo(from, to, vehicle, false);
	}
	
	
	@Override
	public RouteParameter routeFromTo(Place from, Place to, Vehicle vehicle, boolean updatePlaces) {
		long fromNodeId = provider.getNearestStreetNode(from, updatePlaces).getId();
		long toNodeId = provider.getNearestStreetNode(to, updatePlaces).getId();
		
		MobileInterfaceDataSet routingDataSet = provider.getRoutingDataSet(fromNodeId, toNodeId, null);		
		
		IRouter router = new MobileMultiTargetDijkstraRouter();
		router.setMetric(new MobileRoutingMetric());
		Route route = router.route(routingDataSet, routingDataSet.getNodeByID(toNodeId), 
				routingDataSet.getNodeByID(fromNodeId), vehicle);
		
		RouteParameter result = new RouteParameter(route);
		return result;
	}

	
	@Override
	public Place getNearestPOINode(Place center, POINodeSelector selector, Limits limits) {
		throw new UnsupportedOperationException("getNearestPOINode() not yet supported by MobileRoutingEngine");	
	}

	
	@Override
	public Place getNearestStreetNode(Place center) {
		throw new UnsupportedOperationException("getNearestStreetNode() not yet supported by MobileRoutingEngine");
	}
	

	@Override
	public void shutdown() {
		if (initialized()) {
			provider.close();
		}
	}

}
