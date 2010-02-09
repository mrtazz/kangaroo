package com.mobiletsm.osm.data;
import java.util.Iterator;

import org.openstreetmap.osm.ConfigurationSection;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osm.data.coordinates.Bounds;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

/**
 * 
 */

/**
 * @author Andreas Walz
 *
 */
public abstract class MobileDataSet implements IDataSet {

	
	/* methods to be implemented by MobileDataSets */
	
	public abstract void addNode(Node node);

	
	public abstract boolean containsNode(Node node);
	
	
	public abstract int getNodesCount();
	
	
	public abstract void addWay(Way way);
	

	public abstract boolean containsWay(Way way);
	

	public abstract int getWaysCount();
	
	
	public abstract Node getNearestNode(LatLon pos, Selector selector);
	
	
	public abstract Node getNodeByID(long nodeId);
	
	
	public abstract Iterator<Node> getNodes(Bounds bounds);
	
	
	public abstract Iterator<Node> getNodesByName(String name);
	
	
	public abstract Iterator<Node> getNodesByTag(String key, String value);	
	
	
	public abstract WayHelper getWayHelper();
	
	
	public abstract Iterator<Way> getWays(Bounds bounds);
	
	
	public abstract Way getWaysByID(long wayId);
	
	
	public abstract Iterator<Way> getWaysByName(String name, Bounds bounds);
	
	
	public abstract Iterator<Way> getWaysByTag(String key, String value);
	
	
	public abstract Iterator<Way> getWaysForNode(long nodeId);
	
	
	public abstract void removeNode(Node node);
	
	
	public abstract void removeWay(Way way);
	
	
	public abstract void shutdown();
	
	
	/* methods that are not yet supported by MobileDataSet */
	
	public void addRelation(Relation arg0) {
		throw new UnsupportedOperationException("addRelation() not supported by MobileDataSet");
	}

	
	public boolean containsRelation(Relation arg0) {
		throw new UnsupportedOperationException("containsRelation() not supported by MobileDataSet");
	}

	
	public Relation getRelationByID(long arg0) {
		throw new UnsupportedOperationException("getRelationByID() not supported by MobileDataSet");
	}

	
	public Iterator<Relation> getRelations(Bounds arg0) {
		throw new UnsupportedOperationException("getRelations() not supported by MobileDataSet");
	}

	
	public void removeRelation(Relation arg0) {
		throw new UnsupportedOperationException("removeRelation() not supported by MobileDataSet");
	}

	
	/* methods of unknown use :-) */
	
	public ConfigurationSection getSettings() {
		return null;
	}

}
