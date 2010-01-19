/**
 * 
 */
package com.kangaroo.routing;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

/**
 * @author Andreas Walz
 *
 */
public class AmenitySelector implements Selector {
	
	private String amenity;
	
	public static String POST_BOX = "post_box";
	public static String PARKING = "parking";
	public static String SCHOOL = "school";
	public static String CASH_POINT = "atm";
	public static String HOSPITAL = "hospital";
	

	public AmenitySelector(String aAmenity) {
		super();
		amenity = aAmenity;
	}
	
	
	@Override
	public boolean isAllowed(IDataSet map, Node node) {
		boolean result = false;
		
		for (Tag tag : node.getTags()) {
			if ((tag.getKey().compareTo("amenity") == 0) && (tag.getValue().compareTo(amenity) == 0)) 
				result = true;
		}
		
		return result;
	}

	
	@Override
	public boolean isAllowed(IDataSet map, Way way) {
		return false;
	}

	
	@Override
	public boolean isAllowed(IDataSet map, Relation relation) {
		return false;
	}
	
}
