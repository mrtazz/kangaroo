package com.mobiletsm.routing;

import java.util.Iterator;

import org.openstreetmap.osm.ConfigurationSection;
import org.openstreetmap.osm.Tags;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

/**
 * @author Andreas Walz
 *
 */
public final class AllStreetVehicle extends Vehicle {

	
	public AllStreetVehicle() {
		this(MAXSPEED_DEFAULT);
	}
	
	
	public AllStreetVehicle(double maxSpeed) {
		super();
		super.setMaxSpeed(maxSpeed);
	}
	
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AllStreetVehicle)) {
			return false;
		}
		
		AllStreetVehicle vehicle = (AllStreetVehicle)object;		
		
		/* return true if both vehicles have the same maximum speed */
		return this.getMaxSpeed() == vehicle.getMaxSpeed(); 
	}
	
	
	@Override
	public boolean isAllowed(IDataSet map, Node node) {		
		Iterator<Way> wayitr = map.getWaysForNode(node.getId());
		while(wayitr.hasNext()) {
			Way way = wayitr.next();
			if (this.isAllowed(map, way)) {
				return true;
			}
		}		
		return false;
	}

	
	@Override
	public boolean isAllowed(IDataSet map, Way way) {
		boolean hasHighway = WayHelper.getTag(way, Tags.TAG_HIGHWAY) != null;
		boolean hasName = WayHelper.getTag(way, Tags.TAG_NAME) != null;
		boolean hasRef = WayHelper.getTag(way, Tags.TAG_REF) != null;
		boolean isRoundabout = false; 
		
		String junction = WayHelper.getTag(way, Tags.TAG_JUNCTION);
		if (junction != null && junction.equals("roundabout")) {
			isRoundabout = true;
		}
		
		return hasHighway && (hasName || hasRef || isRoundabout);		
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
