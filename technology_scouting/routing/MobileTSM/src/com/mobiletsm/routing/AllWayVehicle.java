package com.mobiletsm.routing;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;


public class AllWayVehicle extends AllStreetVehicle {

	@Override
	public boolean isAllowed(IDataSet map, Node node) {
		return true;
	}

	@Override
	public boolean isAllowed(IDataSet map, Way way) {
		return true;
	}

}
