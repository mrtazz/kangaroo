package com.mobiletsm.routing;

import java.util.Iterator;

import org.openstreetmap.osm.ConfigurationSection;
import org.openstreetmap.osm.Tags;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.travelingsalesman.routing.IVehicle;

/**
 * @author Andreas Walz
 *
 */
public class AllStreetVehicle extends Vehicle {

	
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
		return hasHighway && (hasName || hasRef);		
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
