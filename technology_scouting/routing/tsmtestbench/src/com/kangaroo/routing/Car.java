/**
 * 
 */
package com.kangaroo.routing;

import java.util.Iterator;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

/**
 * @author Andreas Walz
 *
 */
public class Car extends Vehicle {

	public boolean isAllowed(IDataSet dataSet, Node node) {
		// TODO Auto-generated method stub
		
		//System.out.println("isAllowed(.., Node): " + MyLogger.toString(node));
		
		Iterator<Way> wayitr = dataSet.getWaysForNode(node.getId());
		while(wayitr.hasNext()) {
			Way way = wayitr.next();
			if (this.isAllowed(dataSet, way)) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isAllowed(IDataSet dataSet, Way way) {
		// TODO Auto-generated method stub
		
		boolean result = false;
		
		//System.out.println("isAllowed(.., Way): " + MyLogger.toString(way));
		
		for (Tag tag : way.getTags()) {
			if (tag.getKey().compareTo("highway") == 0) {
				if ((tag.getValue().compareTo("motorway") == 0) ||
					(tag.getValue().compareTo("primary") == 0) ||
					(tag.getValue().compareTo("secondary") == 0) ||
					(tag.getValue().compareTo("tertiary") == 0) ||					
					(tag.getValue().compareTo("living_street") == 0) ||
					(tag.getValue().compareTo("residential") == 0)) {
					
					result = true;
				}
				
			}
		}
		
		return result;
	}
	
}
