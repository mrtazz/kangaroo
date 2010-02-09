package com.mobiletsm.osm.data;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osm.data.coordinates.Bounds;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;




public class MobileMemoryDataSet extends MobileDataSet {

	
	protected Map<Long, Node> nodesById = Collections.synchronizedMap(new HashMap<Long, Node>());
	
	
	protected Map<Long, Way> waysById = Collections.synchronizedMap(new HashMap<Long, Way>());
	
	
	protected Map<Long, List<Way>> waysByNodeId = Collections.synchronizedMap(new HashMap<Long, List<Way>>());
	
		
	@Override
	public void addNode(Node node) {
		if (node != null) {
			if (containsNode(node))
				removeNode(node);
			nodesById.put(node.getId(), node);
		}
	}
	

	@Override
	public void addWay(Way way) {
		if (way != null) {
			if (containsWay(way))
				removeWay(way);
			waysById.put(way.getId(), way);
			for (WayNode wayNode : way.getWayNodes()) {
				List<Way> ways = waysByNodeId.get(wayNode.getNodeId());
				if (ways == null) {
					ways = new LinkedList<Way>();
					waysByNodeId.put(wayNode.getNodeId(), ways);
				}
				ways.add(way);
			}
		}
	}
	

	@Override
	public boolean containsNode(Node node) {
		if (node == null)
			return false;
		return nodesById.containsKey(node.getId());
	}
	

	@Override
	public boolean containsWay(Way way) {
		if (way == null)
			return false;
		return waysById.containsKey(way.getId());
	}
	

	@Override
	public Node getNearestNode(LatLon pos, Selector selector) {
		double minDist = Double.MAX_VALUE;
		Node minDistNode = null;
		for (Node node : nodesById.values()) {
			if (selector != null && !selector.isAllowed(this, node))
				continue;
			LatLon nodePos = new LatLon(node.getLatitude(), node.getLongitude());
			double dist = pos.distance(nodePos);
			if (dist < minDist) {
				minDist = dist;
				minDistNode = node;
			}
		}
		return minDistNode;
	}

	
	@Override
	public Node getNodeByID(long nodeId) {		
		return nodesById.get(nodeId);
	}

	
	@Override
	public Iterator<Node> getNodes(Bounds bounds) {
		if (bounds == null || bounds == Bounds.WORLD) {
			return nodesById.values().iterator();
		} else {
			Iterator<Node> nodes = nodesById.values().iterator();
			Collection<Node> result = new LinkedList<Node>();
			while (nodes.hasNext()) {
				Node node = nodes.next();
				if (bounds.contains(node.getLatitude(), node.getLongitude()))
					result.add(node);
			}
			return result.iterator();
		}
	}

	
	@Override
	public Iterator<Node> getNodesByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	@Override
	public Iterator<Node> getNodesByTag(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public WayHelper getWayHelper() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Iterator<Way> getWays(Bounds bounds) {
		if (bounds == null || bounds == Bounds.WORLD) {
			return waysById.values().iterator();
		} else {
			Collection<Way> result = new LinkedList<Way>();
			for (Way way : waysById.values()) {
				for (WayNode wayNode : way.getWayNodes()) {
					Node node = getNodeByID(wayNode.getNodeId());
					if (node != null && bounds.contains(node.getLatitude(), node.getLongitude())) {
						result.add(way);
						break;
					}
				}
			}
			return result.iterator();
		}
	}

	
	@Override
	public Way getWaysByID(long wayId) {
		return waysById.get(wayId);
	}

	
	@Override
	public Iterator<Way> getWaysByName(String name, Bounds bounds) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Iterator<Way> getWaysByTag(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Iterator<Way> getWaysForNode(long nodeId) {
		List<Way> ways = waysByNodeId.get(nodeId);
		if (ways != null)
			return ways.iterator();
		else
			return (new Vector<Way>()).iterator();
	}

	
	@Override
	public void removeNode(Node node) {
		if (containsNode(node)) {
			nodesById.remove(node.getId());
		}
	}

	
	@Override
	public void removeWay(Way way) {
		if (containsWay(way)) {
			waysById.remove(way.getId());
			for (WayNode wayNode : way.getWayNodes()) {
				List<Way> ways = waysByNodeId.get(wayNode.getNodeId());
				if (ways != null) {
					ways.remove(way);
				}
			}
		}
	}

	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	
	@Override
	public int getNodesCount() {
		return nodesById.values().size();
	}

	
	@Override
	public int getWaysCount() {
		return waysById.values().size();
	}

}
