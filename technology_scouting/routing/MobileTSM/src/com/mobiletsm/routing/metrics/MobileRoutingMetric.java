package com.mobiletsm.routing.metrics;
import org.openstreetmap.osm.ConfigurationSection;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.travelingsalesman.routing.Route.RoutingStep;
import org.openstreetmap.travelingsalesman.routing.metrics.IRoutingMetric;
import org.openstreetmap.travelingsalesman.routing.metrics.ShortestRouteMetric;

import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;


public class MobileRoutingMetric extends ShortestRouteMetric {

	@Override
	public double getCost(RoutingStep step) {		
		Way way = step.getWay();		
		if (way instanceof MobileWay) {			
			MobileWay myWay = (MobileWay)way;
			return myWay.getPathLength(step.getStartNode().getId(), step.getEndNode().getId());
		} else {
			return super.getCost(step);
		}		
	}
		
}
