package com.mobiletsm.routing;

import java.util.Iterator;

import org.openstreetmap.osm.ConfigurationSection;
import org.openstreetmap.osm.Tags;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;


public class AllWayVehicle extends Vehicle {

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AllWayVehicle)) {
			return false;
		}
		
		AllWayVehicle vehicle = (AllWayVehicle)object;
		
		return this.getMaxSpeed() == vehicle.getMaxSpeed(); 
	}
	
	
	@Override
	public boolean isAllowed(IDataSet map, Node node) {		
		Iterator<Way> wayitr = map.getWaysForNode(node.getId());
		return wayitr.hasNext();
	}

	
	@Override
	public boolean isAllowed(IDataSet map, Way way) {
		return true;
	}

	
	@Override
	public boolean isOneway(IDataSet map, Way way) {
		return WayHelper.isOneway(way);
	}

	
	@Override
	public boolean isReverseOneway(IDataSet map, Way way) {
		return WayHelper.isReverseOneway(way);
	}

	
	@Override
	public boolean isAllowed(IDataSet arg0, Relation arg1) {
		return false;
	}

	
	@Override
	public ConfigurationSection getSettings() {
		return null;
	}

}
