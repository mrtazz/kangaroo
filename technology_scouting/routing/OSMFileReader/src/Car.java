
import java.util.Iterator;

import org.openstreetmap.osm.ConfigurationSection;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.travelingsalesman.routing.IVehicle;

/**
 * @author Andreas Walz
 *
 */
public class Car implements IVehicle {

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
					(tag.getValue().compareTo("service") == 0) ||
					(tag.getValue().compareTo("residential") == 0)) {
					
					result = true;
				}
				
			}
		}
		
		return result;
	}

	public boolean isOneway(IDataSet arg0, Way arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isReverseOneway(IDataSet arg0, Way arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAllowed(IDataSet arg0, Relation arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public ConfigurationSection getSettings() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
