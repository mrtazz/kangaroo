package com.mobiletsm.osm.data;

import java.util.Iterator;

import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osm.data.coordinates.Bounds;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

public abstract class MobileInterfaceDataSet extends MobileDataSet {

	
	/* methods to be implemented by MobileInterfaceDataSets */
	
	public abstract boolean containsNode(Node node);
	
	
	public abstract boolean containsWay(Way way);
	
	
	public abstract Node getNearestNode(LatLon pos, Selector selector);
	
	
	public abstract Node getNodeByID(long nodeId);
	

	public abstract Iterator<Node> getNodes(Bounds bounds);


	public abstract Iterator<Node> getNodesByName(String name); 


	public abstract Iterator<Node> getNodesByTag(String key, String value);
	
	
	public abstract int getNodesCount(); 
	
	
	public abstract WayHelper getWayHelper();


	public abstract Way getWaysByID(long wayId); 


	public abstract Iterator<Way> getWays(Bounds bounds);


	public abstract Iterator<Way> getWaysByName(String name, Bounds bounds);


	public abstract Iterator<Way> getWaysByTag(String key, String value);
	

	public abstract int getWaysCount();


	public abstract Iterator<Way> getWaysForNode(long nodeId);
	
	
	public abstract void shutdown();	
	
	
	public abstract void onParameterUpdate();
	
	
	/* methods that are not yet supported by MobileInterfaceDataSet */	
	
	@Override
	public void addNode(Node node) {
		throw new UnsupportedOperationException("addNode() not supported by MobileInterfaceDataSet");
	}

	
	@Override
	public void addWay(Way way) {
		throw new UnsupportedOperationException("addWay() not supported by MobileInterfaceDataSet");
	}


	@Override
	public void removeNode(Node node) {
		throw new UnsupportedOperationException("removeNode() not supported by MobileInterfaceDataSet");
	}
	
	
	@Override
	public void removeWay(Way way) {
		throw new UnsupportedOperationException("removeWay() not supported by MobileInterfaceDataSet");
	}

}
