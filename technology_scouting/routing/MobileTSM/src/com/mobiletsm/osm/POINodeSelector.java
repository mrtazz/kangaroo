package com.mobiletsm.osm;

import org.openstreetmap.osm.Tags;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.NodeHelper;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

public abstract class POINodeSelector implements Selector {

	/* method to be implemented by POINodeSelectors */
	
	public abstract boolean isAllowed(IDataSet map, Node node);
	
	
	public abstract boolean equals(Object object);
	
	
	/*  */
	
	public boolean isAllowed(IDataSet arg0, Way arg1) {
		return false;
	}

	
	public boolean isAllowed(IDataSet arg0, Relation arg1) {
		return false;
	}

	
}
