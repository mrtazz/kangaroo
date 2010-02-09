package com.mobiletsm.osm.data.searching;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.NodeHelper;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;

public class AmenityPOINodeSelector extends POINodeSelector {

	@Override
	public boolean isAllowed(IDataSet map, Node node) {
		boolean isAmenity = NodeHelper.getTag(node, "amenity") != null;
		return isAmenity;
	}

	
	@Override
	public boolean equals(Object object) {
		return (object instanceof AmenityPOINodeSelector);
	}

}
