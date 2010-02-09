package com.mobiletsm.osm.data;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osm.data.coordinates.Bounds;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.travelingsalesman.routing.IVehicle;

public class MobileRoutingInterfaceDataSet extends MobileInterfaceDataSet {

	private WayHelper wayHelper = new WayHelper(this); 
	
	
	/*  */
	
	private long fromWayId;	
	
	private long toWayId;	
	
	private IVehicle vehicle;
		
	
	/*  */
	
	private Map<Long, Node> nodes = null;	
	
	private Map<Long, Way> completeWays = null;	
	
	private Map<Long, Way> reducedWays = null;	
	
	private Map<Long, Set<Long>> waysForNodes = null;
	
	
	/*  */
	
	public void setNodesMap(Map<Long, Node> map) {
		nodes = map;
	}
	
	
	public void setCompleteWaysMap(Map<Long, Way> map) {
		completeWays = map;
	}
	
	
	public void setReducedWaysMap(Map<Long, Way> map) {
		reducedWays = map;
	}
	
	
	public void setWaysForNodesMap(Map<Long, Set<Long>> map) {
		waysForNodes = map;
	}
	
	
	public void setMaps(Map<Long, Node> nodes, Map<Long, Way> completeWays, Map<Long, Way> reducedWays, Map<Long, Set<Long>> waysForNodes) {
		this.nodes = nodes;
		this.completeWays = completeWays;
		this.reducedWays = reducedWays;
		this.waysForNodes = waysForNodes;
	}
	
	
	public MobileRoutingInterfaceDataSet(long fromWayId, long toWayId, IVehicle vehicle) {
		super();
		this.fromWayId = fromWayId;
		this.toWayId = toWayId;
		this.vehicle = vehicle;
	}	
	
	
	@Override
	public boolean containsNode(Node node) {
		return nodes.containsKey(node.getId());
	}

	
	@Override
	public boolean containsWay(Way way) {
		return reducedWays.containsKey(way.getId()) || completeWays.containsKey(way.getId());
	}	

	
	@Override
	public Node getNodeByID(long nodeId) {
		return nodes.get(nodeId);
	}

	
	@Override
	public int getNodesCount() {
		return nodes.size();
	}

	
	@Override
	public WayHelper getWayHelper() {
		return wayHelper;
	}
	
	
	@Override
	public Way getWaysByID(long wayId) {
		if (wayId == fromWayId || wayId == toWayId)
			return completeWays.get(wayId);
		else
			return reducedWays.get(wayId);
	}

	
	@Override
	public int getWaysCount() {
		return reducedWays.size();
	}
	

	@Override
	public Iterator<Way> getWaysForNode(long nodeId) {
		List<Way> ways = new Vector<Way>();
		Set<Long> wayIds = waysForNodes.get(nodeId);
		for (Long id : wayIds) {
			ways.add(this.getWaysByID(id));
		}
		return ways.iterator();
	}
	

	@Override
	public void onParameterUpdate() {
	}
	

	@Override
	public void shutdown() {
	}

	
	/* methods that are not yet supported by MobileRoutingInterfaceDataSet */
	
	@Override
	public Node getNearestNode(LatLon pos, Selector selector) {
		throw new UnsupportedOperationException("getNearestNode() not supported by MobileRoutingInterfaceDataSet");
	}

	@Override
	public Iterator<Node> getNodes(Bounds bounds) {
		throw new UnsupportedOperationException("getNodes() not supported by MobileRoutingInterfaceDataSet");
	}

	@Override
	public Iterator<Node> getNodesByName(String name) {
		throw new UnsupportedOperationException("getNodesByName() not supported by MobileRoutingInterfaceDataSet");
	}

	@Override
	public Iterator<Node> getNodesByTag(String key, String value) {
		throw new UnsupportedOperationException("getNodesByTag() not supported by MobileRoutingInterfaceDataSet");
	}
	
	@Override
	public Iterator<Way> getWays(Bounds bounds) {
		throw new UnsupportedOperationException("getWays() not supported by MobileRoutingInterfaceDataSet");
	}

	@Override
	public Iterator<Way> getWaysByName(String name, Bounds bounds) {
		throw new UnsupportedOperationException("getWaysByName() not supported by MobileRoutingInterfaceDataSet");
	}

	@Override
	public Iterator<Way> getWaysByTag(String key, String value) {
		throw new UnsupportedOperationException("getWaysByTag() not supported by MobileRoutingInterfaceDataSet");
	}
}
