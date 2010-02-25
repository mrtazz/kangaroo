package com.mobiletsm.osm.data.searching;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.NodeHelper;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;

public class AmenityPOINodeSelector extends POINodeSelector {

	
	private Amenity myAmenity = null;
	
	
	public AmenityPOINodeSelector(String type) {
		this(new Amenity(type));
	}
	
	
	public AmenityPOINodeSelector() {
		super();
	}
	
	
	public AmenityPOINodeSelector(Amenity amenity) {
		super();
		this.myAmenity = amenity;
	}
	
	
	public Amenity getAmenity() {
		return myAmenity;
	}
	
	
	@Override
	public boolean isAllowed(IDataSet map, Node node) {
		String nodeAmenityType = NodeHelper.getTag(node, "amenity");
		if (myAmenity == null) {
			return nodeAmenityType != null;
		} else {
			return nodeAmenityType.equals(myAmenity.getType());
		}
	}

	
	@Override
	public boolean equals(Object object) {
		boolean isAmenitySelector = (object instanceof AmenityPOINodeSelector);
		if (isAmenitySelector) {
			AmenityPOINodeSelector selector = (AmenityPOINodeSelector)object;
			return myAmenity.equals(selector.getAmenity());
		} else {
			return false;
		}
	}

}
