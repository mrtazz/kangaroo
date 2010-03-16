package com.mobiletsm.osm;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.NodeHelper;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osm.data.coordinates.Bounds;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osm.data.searching.NearestStreetSelector;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.travelingsalesman.routing.IRouter;
import org.openstreetmap.travelingsalesman.routing.IVehicle;
import org.openstreetmap.travelingsalesman.routing.Route;
import org.openstreetmap.travelingsalesman.routing.Route.RoutingStep;
import com.mobiletsm.osm.data.MobileDataSet;
import com.mobiletsm.osm.data.MobileMemoryDataSet;
import com.mobiletsm.osm.data.searching.POICode;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileNode;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWayNode;
import com.mobiletsm.routing.metrics.MobileRoutingMetric;
import com.mobiletsm.routing.routers.MobileMultiTargetDijkstraRouter;



/**
 * @author andreaswalz
 *
 */
public class OsmHelper {
	
	
	public static int TAGFLAG_ONEWAY = 1;
	
	
	public static int TAGFLAG_REVERSE_ONEWAY = 2;
	
	
	public static int TAGFLAG_ROUNDABOUT = 4;
	
	
	public static IDataSet applyFilter(IDataSet map, Selector selector) {
		System.out.println("applyFilter: input: # nodes = " + getNumberOfNodes(map));
		System.out.println("applyFilter: input: # ways = " + getNumberOfWays(map));
		
		IDataSet newMap = new MobileMemoryDataSet();
		
		Iterator<Node> nodes = map.getNodes(Bounds.WORLD);
		while (nodes.hasNext()) {
			Node node = nodes.next();			
			boolean keep = false;
			if (selector.isAllowed(map, node)) {
				/* keep the node if it is allowed */
				keep = true;
			} else {
				/* keep the node anyhow if there is an allowed way connected to it */
				Iterator<Way> waysForNode = map.getWaysForNode(node.getId());
				while (waysForNode.hasNext()) {
					if (selector.isAllowed(map, waysForNode.next())) {
						keep = true;
					}
				}				
			}
			if (keep)
				newMap.addNode(node);
		}
		
		
		Iterator<Way> ways = map.getWays(Bounds.WORLD);
		while (ways.hasNext()) {
			Way way = ways.next();
			if (selector.isAllowed(map, way)) {
				newMap.addWay(way);
			}
		}
		
		System.out.println("applyFilter: output: # nodes = " + getNumberOfNodes(newMap));
		System.out.println("applyFilter: output: # ways = " + getNumberOfWays(newMap));
		
		return newMap;
	}
	
	
	public static void addSpecificTags(Way way, String name, String highway) {
		way.getTags().add(new Tag("name", name));
		way.getTags().add(new Tag("highway", highway));
	}
	
	
	public static boolean checkPathLength(IDataSet map) {
		if (!(map instanceof MobileDataSet)) {
			map = toMobileDataSet(map);
		}
		
		System.out.println("checkPathLength: input: # ways = " + getNumberOfWays(map));
		
		boolean equal = true;
		
		/* iterate over every way */
		Iterator<Way> way_itr = map.getWays(Bounds.WORLD);
		while (way_itr.hasNext()) {
			MobileWay way = (MobileWay)way_itr.next();			
			
			/* choose two different nodes on the way randomly */
			List<WayNode> wayNodes = way.getWayNodes();
			if (wayNodes.size() < 2) {
				System.out.println("checkPathLength: way has less than 2 waynodes");
				continue;
			}
			long nodeId1 = wayNodes.get(random(0, wayNodes.size() - 1)).getNodeId();
			long nodeId2 = nodeId1;
			while (nodeId1 == nodeId2)
				nodeId2 = wayNodes.get(random(0, wayNodes.size() - 1)).getNodeId();			
			
			Node startNode = map.getNodeByID(nodeId1);
			Node endNode = map.getNodeByID(nodeId2);
			
			/* calculate distance with 'RoutingStep.distanceInMeters()' and 'MobileWay.getPathLength()' */
			RoutingStep step = new RoutingStep(map, startNode, endNode, way);
			double length1 = step.distanceInMeters();
			double length2 = way.getPathLength(nodeId1, nodeId2);	
			if (getRatio(length1, length2) > 0.01 /*length1 != length2*/) {
				equal = false;
				System.out.println(String.format(Locale.US, "checkPathLength: difference in " +
						"(isOneway = %s) %d ---wayid:%d---> %d, %.2f <> %.2f", 
						Boolean.toString(WayHelper.isOneway(way) || WayHelper.isReverseOneway(way)), 
						startNode.getId(), way.getId(), endNode.getId(), length1, length2));
				System.out.println("   waynodeinfo: " + way.getWayNodeInfo() + "\n");
			}			
		}		
		return equal;
	}
	

	public static boolean compareRoutes(IDataSet map1, Route route1, IDataSet map2, Route route2) {
		List<WayNode> wayNodes1 = getRouteNodes(map1, route1);
		List<WayNode> wayNodes2 = getRouteNodes(map2, route2);
		
		final int colWidth = 70;
		final int colWidth_ = 20;
		boolean identical = true;
		int i1 = 0;
		int i2 = 0;
		
		System.out.println(fitLength("compareRoutes:", colWidth_) + 
				fitLength(String.format("OsmHelper.getRouteLength(route1) = %.2fm", OsmHelper.getRouteLength(route1)), colWidth) + 
				fitLength(String.format("OsmHelper.getRouteLength(route2) = %.2fm", OsmHelper.getRouteLength(route2)), colWidth));
		
		System.out.println(fitLength("compareRoutes:", colWidth_) + 
				fitLength(String.format("OsmHelper.getRouteLengthOnMap(map1, route1) = %.2fm", getRouteLengthOnMap(map1, route1)), colWidth) + 
				fitLength(String.format("OsmHelper.getRouteLengthOnMap(map2, route2) = %.2fm", getRouteLengthOnMap(map2, route2)), colWidth));
		
		System.out.println(fitLength("compareRoutes:", colWidth_) + 
				fitLength(String.format("OsmHelper.getRouteLengthOnMap(map2, route1) = %.2fm", getRouteLengthOnMap(map2, route1)), colWidth) + 
				fitLength(String.format("OsmHelper.getRouteLengthOnMap(map1, route2) = %.2fm", getRouteLengthOnMap(map1, route2)), colWidth));
		
		System.out.println(fitLength("compareRoutes:", colWidth_) + 
				fitLength(String.format("route1.distanceInMeters() = %.2fm", route1.distanceInMeters()), colWidth) + 
				fitLength(String.format("route2.distanceInMeters() = %.2fm", route2.distanceInMeters()), colWidth));
		
		Node thisNode1 = null;
		Node thisNode2 = null;
		
		while (i1 < wayNodes1.size() || i2 < wayNodes2.size()) {
						
			StringBuffer line = new StringBuffer(fitLength("compareRoutes:", colWidth_));			
						
			long stepNodeId1 = wayNodes1.get(i1).getNodeId();
			long stepNodeId2 = wayNodes2.get(i2).getNodeId();
			
			String wayInfo1 = "";
			String wayInfo2 = "";			
			
			if (wayNodes1.get(i1) instanceof MobileWayNode) {
				MobileWayNode wayNode = (MobileWayNode)wayNodes1.get(i1);				
				thisNode1 = map1.getNodeByID(wayNode.getNodeId());				
				wayInfo1 = String.format(Locale.US, "--- wayid:%d,dist:%.2fm ---> ",  
						wayNode.getWay().getId(), wayNode.getDistanceToPredecessor());				
			}
			if (wayNodes2.get(i2) instanceof MobileWayNode) {
				MobileWayNode wayNode = (MobileWayNode)wayNodes2.get(i2);				
				thisNode2 = map2.getNodeByID(wayNode.getNodeId());				
				wayInfo2 = String.format(Locale.US, "--- wayid:%d,dist:%.2fm ---> ", 
						wayNode.getWay().getId(), wayNode.getDistanceToPredecessor());
			}
			
			
			if (stepNodeId1 == stepNodeId2) {
					
				/* if current nodes are the same in both routes */
				line.append(fitLength(wayInfo1 + Long.toString(stepNodeId1), colWidth));
				line.append(fitLength(wayInfo2 + Long.toString(stepNodeId2), colWidth));
				i1++;
				i2++;
					
			} else {
			
				/* if nodes differ */
				identical = false;
				boolean found = false;
				for (int i = i2; i < wayNodes2.size() && !found; i++) {
					if (stepNodeId1 == wayNodes2.get(i).getNodeId()) {							
						line.append(fitLength("|", colWidth));
						line.append(fitLength(wayInfo2 + Long.toString(stepNodeId2), colWidth));
						i2++;							
						found = true;
					}
				}
				for (int i = i1; i < wayNodes1.size() && !found; i++) {
					if (stepNodeId2 == wayNodes1.get(i).getNodeId()) {
						line.append(fitLength(wayInfo1 + Long.toString(stepNodeId1), colWidth));
						line.append(fitLength("|", colWidth));
						i1++;							
						found = true;
					}
				}
				if (!found) {
					line.append(fitLength(wayInfo1 + Long.toString(stepNodeId1), colWidth));
					line.append(fitLength("|", colWidth));
					i1++;
				}				
			}		
			
			System.out.println(line.toString());			
		}		
		
		return identical;
	}
	
	
	public static boolean compareRouting(IDataSet map1, IDataSet map2, IVehicle vehicle, PrintStream output) {
		
		if (output != null) {
			output.println("compareRouting: input: # map1.nodes = " + getNumberOfNodes(map1));
			output.println("compareRouting: input: # map1.ways = " + getNumberOfWays(map1));
			output.println("compareRouting: input: # map2.nodes = " + getNumberOfNodes(map2));
			output.println("compareRouting: input: # map2.ways = " + getNumberOfWays(map2));
		}
		
		/* find nodes of intersection of map1 and map2 */
		List<Long> intersectingNodes = getIntersectionNodeIds(map1, map2);
		
		if (intersectingNodes.size() < 2) {
			if (output != null)
				output.println("compareRouting: # intersecting nodes must be " +
					"at least 2, but is " + intersectingNodes.size());
			return false;
		} else {
			if (output != null)
				output.println("compareRouting: # intersecting nodes = " + intersectingNodes.size());			
		}
		
		IRouter router1 = new MobileMultiTargetDijkstraRouter();	//new MultiTargetDijkstraRouter();
		IRouter router2 = new MobileMultiTargetDijkstraRouter();	//new MultiTargetDijkstraRouter();
		router1.setMetric(new MobileRoutingMetric());
		router2.setMetric(new MobileRoutingMetric());
		
		/* find to nodes to route between in intersection of map1 and map2 */
		long fromNodeId, toNodeId;		
		Node fromNode, toNode;
		do {
			fromNodeId = intersectingNodes.get(random(0, intersectingNodes.size() - 1)); 
		} while (!vehicle.isAllowed(map1, map1.getNodeByID(fromNodeId)));
		do {
			toNodeId = intersectingNodes.get(random(0, intersectingNodes.size() - 1)); 
		} while (!vehicle.isAllowed(map1, map1.getNodeByID(toNodeId)));				
		
		if (!vehicle.isAllowed(map2, map2.getNodeByID(fromNodeId)) || !vehicle.isAllowed(map2, map2.getNodeByID(toNodeId)))
			System.out.println("compareRouting: WARNING: selected nodes of map1 are not allowed for map2.");
		
			//fromNodeId = 269582302;
			//toNodeId = 527825824;
		
		fromNode = map1.getNodeByID(fromNodeId);
		toNode = map1.getNodeByID(toNodeId);		
		System.out.println("compareRouting: router1: route from node " + fromNodeId + " to node " + toNodeId);
		Route route1 = router1.route(map1, toNode, fromNode, vehicle);
		
		fromNode = map2.getNodeByID(fromNodeId);
		toNode = map2.getNodeByID(toNodeId);	
		System.out.println("compareRouting: router2: route from node " + fromNodeId + " to node " + toNodeId);
		Route route2 = router2.route(map2, toNode, fromNode, vehicle);
		
		System.out.println("followRouteOnMap(map1, route2) = " + followRouteOnMap(map1, route2, vehicle, System.out));
		System.out.println("followRouteOnMap(map2, route2) = " + followRouteOnMap(map2, route2, vehicle, System.out));
		
		if (route1 != null && route2 != null)
			return compareRoutes(map1, route1, map2, route2);
		else
			return false;
		
	}
	
	
	private static String fitLength(String str, int length) {
		if (str.length() < length) {
			while (str.length() < length)
				str = str + " ";
			return str;
		} else if (str.length() > length) {
			return str.substring(0, length);
		}
		return str;
	}
	
	
	
	/**
	 * try to follow the given route on the given map. The route may be produced by a router working
	 * on another map. This method can thus be used to check if the map contains this route.
	 * @param map
	 * @param route
	 * @param vehicle
	 * @param output
	 * @return
	 */
	public static boolean followRouteOnMap(IDataSet map, Route route, IVehicle vehicle, PrintStream output) {
		List<RoutingStep> steps = route.getRoutingSteps();
		Iterator<RoutingStep> steps_itr = steps.iterator();
		
		if (output != null)
			output.println("followRouteOnMap: START");
		
		while (steps_itr.hasNext()) {
			RoutingStep step = steps_itr.next();
			
			/* load elements of this routing step from the map */
			Node startNode = map.getNodeByID(step.getStartNode().getId());
			Node endNode = map.getNodeByID(step.getEndNode().getId());
			Way way = map.getWaysByID(step.getWay().getId());
			
			/* check if the map contains these elements */
			if (startNode == null || way == null || endNode == null) {
				/* following the route on the map failed because of a missing element */
				if (output != null)
					output.println("followRouteOnMap: ERROR: missing element on map");
				return false;
			}

			/* check if the way contains given start and end node */
			boolean containsStartNode = false;
			boolean containsEndNode = false;
			List<WayNode> wayNodes = way.getWayNodes();
			for (WayNode wayNode : wayNodes) {
				if (wayNode.getNodeId() == startNode.getId())
					containsStartNode = true;
				if (wayNode.getNodeId() == endNode.getId())
					containsEndNode = true;
			}

			if (!containsEndNode || !containsStartNode) {
				/* following the route on the map failed because of a missing connection */
				if (output != null)
					output.println("followRouteOnMap: ERROR: way does not connect start and end node");
				return false;					
			}

			/* check if the given elements are allowed by the given vehicle */
			if (!vehicle.isAllowed(map, startNode) ||
					!vehicle.isAllowed(map, endNode) ||
					!vehicle.isAllowed(map, way)) {
				if (output != null) {
					output.println("followRouteOnMap: ERROR: element not allowed by vehicle");
					output.println("followRouteOnMap: " + routingStepToString(way, startNode, endNode));
					output.println("followRouteOnMap: isAllowed(nodeid:" + startNode.getId() + ") = " + 
							vehicle.isAllowed(map, startNode));
					output.println("followRouteOnMap: isAllowed(wayid:" + way.getId() + ") = " + 
							vehicle.isAllowed(map, way));
					output.println("followRouteOnMap: isAllowed(nodeid:" + endNode.getId() + ") = " + 
							vehicle.isAllowed(map, endNode));
				}
				return false;
			}				

			if (output != null) {
				output.println("followRouteOnMap: " + routingStepToString(way, startNode, endNode));
			}

		}
		
		/* following the route on the map was successful */
		if (output != null) {
			output.println("followRouteOnMap: END");
			output.println("followRouteOnMap: getRouteLengthOnMap() = " + OsmHelper.getRouteLengthOnMap(map, route));
		}
		return true;
	}
	

	private static String routingStepToString(Way way, Node startNode, Node endNode) {
		String wayName = WayHelper.getTag(way, "name");
		String wayRef = WayHelper.getTag(way, "ref");
		if (wayName == null && wayRef == null) 
			wayName = "";
		else if (wayName == null && wayRef != null)
			wayName = "(" + wayRef + ")";
		else
			wayName = "(" + wayName + ")";
		return String.format("%d ---wayid:%d---> %d %s", 
				startNode.getId(), way.getId(), endNode.getId(), wayName);
	}
	
	
	public static String getAndRemoveTag(Collection<Tag> tags, String key) {
		for (Tag tag : tags) {
			if (tag.getKey().equals(key)) {
				String value = tag.getValue();
				tags.remove(tag);
				return value;
			}
		}
		return null; 
	}
	
		
	public static int getAndRemoveTagFlags(Collection<Tag> tags) {
		int result = 0;
		
		return result;
	}
	
	
	public static Set<Long> getIntermediateWayNodes(IDataSet map) {
		System.out.println("getIntermediateWayNodes: input: # nodes = " + getNumberOfNodes(map));
		System.out.println("getIntermediateWayNodes: input: # ways = " + getNumberOfWays(map));
		
		Set<Long> result = new HashSet<Long>();				
		Iterator<Node> nodes = map.getNodes(Bounds.WORLD);
		while (nodes.hasNext()) {
			Node node = nodes.next();
			Iterator<Way> waysForNode = map.getWaysForNode(node.getId());
			int nWays = 0;
			Way way = null;
			while (waysForNode.hasNext()) {
				way = waysForNode.next();	
				nWays++;				
			}
			if (nWays == 1) {
				int index = getNodeIndex(node, way);
				if (index > 0 && index < way.getWayNodes().size() - 1)
					result.add(node.getId());
			}			
		}		
		
		System.out.println("getIntermediateWayNodes: output: # intermediate way nodes = " + result.size());
		
		return result;
	}
	
		
	public static List<Long> getIntersectionNodeIds(IDataSet map1, IDataSet map2) {
		Vector<Long> result = new Vector<Long>();		
		Iterator<Node> node_itr = map1.getNodes(null);
		while (node_itr.hasNext()) {
			Node node = node_itr.next();
			if (map2.getNodeByID(node.getId()) != null)
				result.add(node.getId());
		}
		return result;
	}
	
	
	/**
	 *  
	 * @param node
	 * @param way
	 * @return
	 */
	public static int getNodeIndex(Node node, Way way) {
		List<WayNode> wayNodes = way.getWayNodes();
        for (int i = 0; i < wayNodes.size(); i++) {
            if (wayNodes.get(i).getNodeId() == node.getId())
                return i;
        }
        return -1;
	}
		
	
	public static MobileWay getReducedWay(IDataSet map, Collection<Long> intermediateWayNodes, Way way) {
		MobileWay newWay = new MobileWay(way.getId());
		double length = 0;
		WayNode lastWayNode = null;
		for (WayNode wayNode : way.getWayNodes()) {
			if (lastWayNode != null) {
				length += LatLon.distanceInMeters(map.getNodeByID(lastWayNode.getNodeId()), 
						map.getNodeByID(wayNode.getNodeId()));
				if (!intermediateWayNodes.contains(wayNode.getNodeId())) {
					newWay.addWayNode(wayNode.getNodeId(), length);
					length = 0;
				}
			} else {
				newWay.addWayNode(wayNode.getNodeId(), 0);
			}
			lastWayNode = wayNode;
		}		
		return newWay;
	}
	
	
	public static int getNumberOfNodes(IDataSet map) {
		int result = 0;
		Iterator<Node> nodes = map.getNodes(Bounds.WORLD);
		while (nodes.hasNext()) {
			nodes.next();
			result++;
		}
		return result;
	}
	
	
	public static int getNumberOfWays(IDataSet map) {
		int result = 0;
		Iterator<Way> ways = map.getWays(Bounds.WORLD);
		while (ways.hasNext()) {
			ways.next();
			result++;
		}
		return result;
	}
	
	
	public static double getRatio(double a, double b) {
		if (a == 0 && b != 0 || a != 0 && b == 0)
			return 1;
		if (a == 0 && b == 0)
			return 0;
		double diff = Math.abs(a - b);
		return diff / a;		
	}
	
	
	public static double getRouteLengthOnMap(IDataSet map, Route route) {
		double length = 0;
		List<RoutingStep> steps = route.getRoutingSteps();
		Iterator<RoutingStep> steps_itr = steps.iterator();
		while (steps_itr.hasNext()) {
			RoutingStep step = steps_itr.next();

			/* load elements of this routing step from the map */
			Node startNode = map.getNodeByID(step.getStartNode().getId());
			Node endNode = map.getNodeByID(step.getEndNode().getId());
			Way way = map.getWaysByID(step.getWay().getId());
			
			if (way instanceof MobileWay) {
				length += ((MobileWay)way).getPathLength(step.getStartNode().getId(), step.getEndNode().getId());
			} else {
				//way = new MobileWay(way, map);
				//length += ((MobileWay)way).getPathLength(step.getStartNode().getId(), step.getEndNode().getId());				
				RoutingStep myStep = new RoutingStep(map, startNode, endNode, way);
				length += myStep.distanceInMeters();				
			}			
		}
		return length;
	}
	
	
	
	public static List<WayNode> getRouteNodes(IDataSet map, Route route) {		
		Way way = null;
		WayNode lastWayNode = null;
		Iterator<WayNode> wayNodes = null;		
		List<RoutingStep> steps = route.getRoutingSteps();
		List<WayNode> result = new LinkedList<WayNode>();
		
		for (int i = 0; i < steps.size();) {
			if (wayNodes == null) {
				RoutingStep step = steps.get(i);
				way = step.getWay();
				wayNodes = step.getNodes().iterator();
				
				/* skip first way node if not the very first routing step */
				if (i != 0) {
					wayNodes.next();
				}
			}
			
			if (wayNodes.hasNext()) {				
				WayNode wayNode = wayNodes.next();
				MobileWayNode mobileWayNode = new MobileWayNode(wayNode.getNodeId());
				mobileWayNode.setWay(way);
				if (result.size() > 0) {
					if (way instanceof MobileWay) {
						MobileWay mobileWay = (MobileWay)way;
						double dist = mobileWay.getPathLength(lastWayNode.getNodeId(), mobileWayNode.getNodeId());
						mobileWayNode.setDistanceToPredecessor(dist);
					} else {
						Node lastNode = map.getNodeByID(lastWayNode.getNodeId());
						Node thisNode = map.getNodeByID(mobileWayNode.getNodeId());
						mobileWayNode.setDistanceToPredecessor(LatLon.distanceInMeters(lastNode, thisNode));
					}
				}
				result.add(mobileWayNode);
				lastWayNode = mobileWayNode;
			}			
			if (!wayNodes.hasNext()) {
				i++;
				wayNodes = null;
			}
		}		
		return result;
	}
	
	
	static List<Long> getWayIdsForNode(IDataSet map, Node node) {
		List<Long> longs = new LinkedList<Long>();
		Iterator<Way> ways = map.getWaysForNode(node.getId());		
		while(ways.hasNext()) {
			longs.add(ways.next().getId());
		}
		return longs;
	}
	
	
	public static List<Long> getWayNodeIds(Way way) {
		List<Long> longs = new LinkedList<Long>();
		Iterator<WayNode> waynode_itr = way.getWayNodes().iterator();		
		while(waynode_itr.hasNext()) {
			longs.add(waynode_itr.next().getNodeId());
		}
		return longs;
	}


	public static String packLongsToString(List<Long> longs) {
		StringBuffer buf = new StringBuffer();
		Iterator<Long> long_itr = longs.iterator();
		while (long_itr.hasNext()) {
			String longString = Long.toHexString(long_itr.next().longValue());
			for (int i = 8; i > longString.length(); i--)
				buf.append("0");
			buf.append(longString);
		}
		return buf.toString();
	}
	
	
	
	public static String serializeTags(Collection<Tag> tags) {
		StringBuffer buf = new StringBuffer();		
		Iterator<Tag> tag_itr = tags.iterator();
		while(tag_itr.hasNext()) {
			Tag tag = tag_itr.next();
			buf.append("|" + tag.getKey() + "==" + tag.getValue());
		}		
		return buf.toString();
	}
	
	
	public static String serializeMobileWayNodes(Way way) {
		List<WayNode> wayNodes = way.getWayNodes();
		List<MobileWayNode> mobileWayNodes = new LinkedList<MobileWayNode>();
		
		boolean isMobile = true;
		for (WayNode wayNode : wayNodes) {
			if (wayNode instanceof MobileWayNode) {
				mobileWayNodes.add((MobileWayNode)wayNode);
			} else {			
				isMobile = false;
			}
		}
		
		if (isMobile) {
			StringBuffer buf = new StringBuffer();
			for (MobileWayNode mobileWayNode : mobileWayNodes) {				
				if (mobileWayNode.getDistanceToPredecessor() > 0)
					buf.append(String.format(Locale.US, "(%.2f)", mobileWayNode.getDistanceToPredecessor()));				
				String longString = Long.toHexString(mobileWayNode.getNodeId());
				for (int i = 8; i > longString.length(); i--)
					buf.append("0");
				buf.append(longString);				
			}			
			return buf.toString();
		} else {
			return packLongsToString(getWayNodeIds(way));
		}
		
	}
	
	private static int random(int min, int max) {
		int result;		
		do {
			result = (int)(Math.random() * (max - min + 1) + min);
		} while (result < min || result > max);
		return result;
	}
	
	/**
	 * reduce the graph (nodes and ways) in map to the minimum
	 * graph needed for routing
	 * 
	 * @param map
	 * @param nodeSelector
	 * @param selector
	 * @return
	 */
	public static IDataSet simplifyDataSet(IDataSet map, Selector selector) {
		System.out.println("simplifyDataSet: input: # nodes = " + getNumberOfNodes(map));
		System.out.println("simplifyDataSet: input: # ways = " + getNumberOfWays(map));
		
		int zeroWayNodes = 0;
		int oneWayNodes = 0;
		int intermediateWayNodes = 0;
		
		Set<Long> unusedNodes = new HashSet<Long>();		
		MobileDataSet routingMap = new MobileMemoryDataSet();
				
		/* find nodes that are irrelevant for routing by iterating over 
		 * all nodes and add the ones needed for routing to the routing map */
		Iterator<Node> nodes = map.getNodes(null);
		while(nodes.hasNext()) {
			Node node = nodes.next();
			int nNodeWays = 0;
			Way way = null;
			boolean unused = false;
			
			/* determine the number of ways connected to this node */ 
			Iterator<Way> nodeWays = map.getWaysForNode(node.getId());			
			while(nodeWays.hasNext()) {
				Way nextWay = nodeWays.next();
				/* consider only ways allowed by the selector */
				if (selector.isAllowed(map, nextWay)) {
					nNodeWays++;
					way = nextWay;
				}
			}
			
			if (nNodeWays == 0) {
				/* no way is connected to this node,
				 * this node will be useless for routing */
				zeroWayNodes++;
				unusedNodes.add(node.getId());
				unused = true;
			} else if (nNodeWays == 1) {
				/* exactly one way is connected to this node,
				 * check if this node is at the beginning or the end of this way */	
				oneWayNodes++;
				int index = getNodeIndex(node, way);
				if (index > 0 && index < way.getWayNodes().size() - 1) {
					/* node is neither beginning nor end of the way
					 * and thus useless for routing */
					intermediateWayNodes++;
					unusedNodes.add(node.getId());
					unused = true;
				}
			}
			
			/* add this node to the routing map if and only if it
			 * is not useless for routing */
			if (!unused) {
				routingMap.addNode(node);
			}
		}
			
		/*  */
		Iterator<Way> ways = map.getWays(Bounds.WORLD);
		while (ways.hasNext()) {
			Way way = ways.next();
			if (selector.isAllowed(map, way)) {				
				/* this way is allowed by the selector and thus
				 * needed for routing */
				
				/*
				Way newWay = new Way(way.getId(), way.getVersion(), way
						.getTimestamp(), way.getUser(), way.getChangesetId());
				newWay.getTags().addAll(way.getTags());
				for (WayNode wayNode : way.getWayNodes()) {
					if (!unusedNodes.contains(wayNode.getNodeId())) {
						newWay.getWayNodes().add(
								new WayNode(wayNode.getNodeId()));
					}
				}
				*/
				
				MobileWay newWay = new MobileWay(way.getId(), way.getVersion(), 
						way.getTimestamp(), way.getUser(), way.getChangesetId());
				newWay.getTags().addAll(way.getTags());
				
				double length = 0;
				WayNode lastWayNode = null;
				for (WayNode wayNode : way.getWayNodes()) {
					if (lastWayNode != null) {
						length += LatLon.distanceInMeters(map.getNodeByID(lastWayNode.getNodeId()), 
								map.getNodeByID(wayNode.getNodeId()));
						if (!unusedNodes.contains(wayNode.getNodeId())) {
							newWay.addWayNode(wayNode.getNodeId(), length);
							length = 0;
						}	
					} else {
						newWay.addWayNode(wayNode.getNodeId(), 0);
					}
					lastWayNode = wayNode;
				}
				
				
				routingMap.addWay(newWay);
			}
		}
		
		System.out.println("simplifyDataSet: # 0-way-nodes = " + zeroWayNodes);
		System.out.println("simplifyDataSet: # 1-way-nodes = " + oneWayNodes);
		System.out.println("simplifyDataSet: # intermediate way nodes = " + intermediateWayNodes);
		System.out.println("simplifyDataSet: output: # nodes = " + routingMap.getNodesCount() + 
				String.format(Locale.US, " (ratio = %.2f percent)", 
						(double)routingMap.getNodesCount()*100 / getNumberOfNodes(map)));
		System.out.println("simplifyDataSet: output: # ways = " + routingMap.getWaysCount() +
				String.format(Locale.US, " (ratio = %.2f percent)", 
						(double)routingMap.getWaysCount()*100 / getNumberOfWays(map)));
		
		return routingMap;
	}
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	public static MobileDataSet toMobileDataSet(IDataSet map) {
		MobileDataSet newMap = new MobileMemoryDataSet();
		
		/* add nodes */
		Iterator<Node> nodes = map.getNodes(Bounds.WORLD);
		while (nodes.hasNext()) {
			newMap.addNode(nodes.next());
		}
		
		/* add ways */
		Iterator<Way> ways = map.getWays(Bounds.WORLD);
		while (ways.hasNext()) {
			Way way = ways.next();
			
			/*
			MobileWay mobileWay = new MobileWay(way.getId(), way.getVersion(), 
					way.getTimestampContainer(), way.getUser(), way.getChangesetId());
			mobileWay.getTags().addAll(way.getTags());
			
			WayNode lastWayNode = null;
			for (WayNode wayNode : way.getWayNodes()) {
				if (wayNode instanceof MobileWayNode) {
					mobileWay.getWayNodes().add(wayNode);
				} else {
					if (lastWayNode == null) {
						mobileWay.addWayNode(wayNode.getNodeId(), 0);
					} else {
						double length = LatLon.distanceInMeters(map.getNodeByID(lastWayNode.getNodeId()), 
								map.getNodeByID(wayNode.getNodeId()));
						mobileWay.addWayNode(wayNode.getNodeId(), length);
					}
				}
				lastWayNode = wayNode;
			}
			*/
			
			MobileWay mobileWay = new MobileWay(way, map);
			
			newMap.addWay(mobileWay);
		}	
		
		return newMap;
	}
	
	
	public static List<Long> unpackStringToLongs(String longs) {
		List<Long> result = new LinkedList<Long>();
		while (longs.length() >= 8) {
			String str = longs.substring(0, 8);
			result.add(Long.decode("0x" + str));
			longs = longs.substring(8);
		}
		return result;
	}
	
	
	/**
	 * unpack a string containing the entity tags to a collection of tags
	 * @param tags
	 * @return
	 */
	public static Collection<Tag> unpackStringToTags(String tags) {
		Collection<Tag> result = new LinkedList<Tag>();		
		String tag;		
		while(tags.startsWith("|")) {
			int index = tags.indexOf("|", 1);			
			if (index > 1) {
				tag = tags.substring(0, index);
				tags = tags.substring(index);
			} else {
				tag = tags;
				tags = "";
			}			
			index = tag.indexOf("==");
			result.add(new Tag(tag.substring(1, index), tag.substring(index + 2)));
		}		
		return result;
	}
	
	
	public static List<WayNode> unpackStringToWayNodes(String wayNodes) {
		List<WayNode> result = new LinkedList<WayNode>();		
		double dist;
		while (wayNodes.length() > 0) {
			if (wayNodes.startsWith("(")) {
				int index = wayNodes.indexOf(")");
				dist = Double.valueOf(wayNodes.substring(1, index));
				wayNodes = wayNodes.substring(index + 1);
			} else {
				dist = 0;
			}			
			String long_str = wayNodes.substring(0, 8);
			wayNodes = wayNodes.substring(8);
    		result.add(new MobileWayNode(Long.decode("0x" + long_str), dist)); 
		}		
		return result;
	}

	
	
	
	
	public static void writeToDatabase(IDataSet map, String database) {
		
		final String createTable_Nodes = 
			"create table if not exists nodes (" +
				"node_id integer primary key," +
				"lat real not null, " +
				"lon real not null, " +
				"tags text, " +
				"tags_ text," +
				"node_ways text," +
				"isstreetnode integer not null," +
				"amenity integer not null" +
			");";
		
		final String createTable_Ways =
			"create table if not exists ways (" +
				"way_id integer primary key," +
				"tags text," +
				"tags_ text," +
				"way_nodes text" +
			");";
		
		
		System.out.println("writeToDatabase: input: # nodes = " + getNumberOfNodes(map));
		System.out.println("writeToDatabase: input: # ways = " + getNumberOfWays(map));
		
	
		try {
			
			Class.forName("org.sqlite.JDBC");			
			Connection connection =	DriverManager.getConnection(database);	
						
			Statement statement = connection.createStatement();
			PreparedStatement ps;
		
			statement.executeUpdate("drop table if exists nodes;");
			statement.executeUpdate("drop table if exists ways;");
			statement.executeUpdate(createTable_Nodes);
			statement.executeUpdate(createTable_Ways);
				
			connection.setAutoCommit(false);
		
			/* write nodes to database */
			ps = connection.prepareStatement(
					"INSERT INTO nodes (node_id, lat, lon, tags, node_ways, isstreetnode, amenity) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?);");
			
			Selector selector = new NearestStreetSelector();			
			Iterator<Node> node_itr = map.getNodes(null);
			while(node_itr.hasNext()) {
				Node node = node_itr.next();				

				String tags = serializeTags(node.getTags());	//tagPacker(node.getTags());
				String ways = packLongsToString(getWayIdsForNode(map, node));
				
				int isstreetnode = 0;
				if (selector.isAllowed(map, node))
					isstreetnode = 1;				
				
				ps.setLong(1, node.getId());
				ps.setDouble(2, node.getLatitude());
				ps.setDouble(3, node.getLongitude());
				ps.setString(4, tags);
				ps.setString(5, ways);
				ps.setInt(6, isstreetnode);
				ps.setInt(7, 0);
				ps.execute();
			}			
			
			/* write ways to database */
			ps = connection.prepareStatement(
				"INSERT INTO ways (way_id, tags, way_nodes) VALUES (?, ?, ?);");	
			
			Iterator<Way> way_itr = map.getWays(Bounds.WORLD);
			while(way_itr.hasNext()) {
				Way way = way_itr.next();
				
				String tags = serializeTags(way.getTags());
				String wayNodes = serializeMobileWayNodes(way);				
				
				ps.setLong(1, way.getId());
				ps.setString(2, tags);
				ps.setString(3, wayNodes);
				ps.execute();
				
				System.out.println(wayNodes);
			}
			
		    connection.setAutoCommit(true);
		    			
			/*
		    ResultSet rs = statement.executeQuery("select * from ways where way_id = " + wayid + ";");
		    while (rs.next()) {
		    	String str = rs.getString("way_nodes");
		    			    	
		    	for (int i = 0; i < str.length(); i+=8) {
		    		String long_str = str.substring(i, i+8);
		    		System.out.print(Long.decode("0x" + long_str) + ", ");
		    	}
		    	
		    	String str_tags = rs.getString("tags");
		    	System.out.println("\n" + str_tags);
		    	
		    	Collection<Tag> tags = tagUnpacker(str_tags);
		    	Iterator<Tag> tag_itr = tags.iterator();
		    	while(tag_itr.hasNext()) {
		    		Tag tag = tag_itr.next();
		    		System.out.println(tag.getKey() + " = " + tag.getValue());
		    	}
		    }
		    rs.close();
		    		    
		    rs = statement.executeQuery("SELECT * FROM nodes;");
		    int i = 0;
		    while(rs.next())
		    	i++;
		    System.out.println("writeToDatabase: # nodes written = " + i);
		    rs = statement.executeQuery("SELECT * FROM ways;");
		    i = 0;
		    while(rs.next())
		    	i++;
		    System.out.println("writeToDatabase: # ways written = " + i);
		    */
		     
		    
		    connection.close();		    
	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static long NODES_STND_IS_STREET_NODE = -1;
	
	public static long NODES_STND_IS_INTERMEDIATE_STREET_NODE = -2;
	
	public IDataSet remapIds(IDataSet map, Map<Long, Integer> nodeMap, Map<Long, Integer> wayMap) {
		
		/* mapping of ids: osm-id --> new-id */
		if (nodeMap == null)
			nodeMap = new HashMap<Long, Integer>();
		if (wayMap == null)
			wayMap = new HashMap<Long, Integer>();
		
		
		/* create mapping for node ids */
		int nodeId = 0;
		Iterator<Node> nodes = map.getNodes(Bounds.WORLD);
		while (nodes.hasNext()) {
			Node node = nodes.next();
			if (!nodeMap.containsKey(node.getId()))
				nodeMap.put(node.getId(), nodeId++);
		}
		
		/* create mapping for way ids */
		int wayId = 0;
		Iterator<Way> ways = map.getWays(Bounds.WORLD);
		while (ways.hasNext()) {
			Way way = ways.next();
			if (!wayMap.containsKey(way.getId())) 
				wayMap.put(way.getId(), wayId++);
		}
		
		/* map nodes and ways */
		List<Node> nodeCache = new LinkedList<Node>();
		List<Way> wayCache = new LinkedList<Way>();
			
		//TODO: all the work
		
		/* check if there are relations in the dataset */
		Iterator<Relation> relations = map.getRelations(Bounds.WORLD);
		if (relations.hasNext()) {
			System.out.println("remapIds: WARNING: map contains relations which will be ignored");
		}			
		
		return map;
	}


	public static double getRouteLength(Route route) {
		double length = 0;
		List<RoutingStep> steps = route.getRoutingSteps();
		Iterator<RoutingStep> steps_itr = steps.iterator();
		while (steps_itr.hasNext()) {
			RoutingStep step = steps_itr.next();
			Way way = step.getWay();
			if (way instanceof MobileWay)
				length += ((MobileWay)way).getPathLength(step.getStartNode().getId(), step.getEndNode().getId());
			else {
				length += step.distanceInMeters();
			}
		}
		return length;
	}

	
	
	public static void printTagHighscore(IDataSet map) {
		Map<String, Integer> highscore = new HashMap<String, Integer>();
		Iterator<Node> nodes = map.getNodes(Bounds.WORLD);
		while (nodes.hasNext()) {
			updateHighscore(highscore, nodes.next().getTags());
		}				
		
		for (Map.Entry<String, Integer> entry : highscore.entrySet()) {
			System.out.println("printTagHighscore(): " + entry.getKey() + " = " + entry.getValue());
		}
				
	}
	
	
	public static void updateHighscore(Map<String, Integer> highscore, Collection<Tag> tags) {
		for (Tag tag : tags) {
			String key = tag.getKey();
			Integer score = highscore.get(key);
			if (score != null) {
				score++;
				highscore.put(key, score);
			} else {
				highscore.put(key, 1);
			}
		}		
	}
	
	
	public static String getWayName(Way way) {
		String name = WayHelper.getTag(way.getTags(), "name"); 
		if (name != null) {
			return name;
		} else {
			String ref = WayHelper.getTag(way.getTags(), "ref");
			if (ref != null) {
				return ref;
			}
		}
		return null;
	}
	
	
	public static String getWayNameDescription(List<Way> ways) {
		if (ways != null && ways.size() > 0) {
			List<String> names = new ArrayList<String>();
			for (Way way : ways) {
				String name = getWayName(way);
				boolean inList = false;
				for (String listName : names) {
					if (listName.equalsIgnoreCase(name)) {
						inList = true;
					}
				}
				if (!inList) {
					names.add(name);
				}
			}
			if (names.size() == 1) {
				return names.get(0);
			} else if (names.size() >= 2) {
				return names.get(0) + " Ecke " + names.get(1);
			}
		}
		return null;
	}
	
	
	public static String getPOINodeName(Node node) {
		String name = NodeHelper.getTag(node, "name");
		String operator = NodeHelper.getTag(node, "operator");
		
		if (name != null || operator != null) {
			StringBuffer buf = new StringBuffer();
			if (name != null) {
				buf.append(name);
				if (operator != null) {
					buf.append(", ");
				}
			}
			if (operator != null) {
				buf.append(operator);
			}
			return buf.toString();
		} else {			
			if (node instanceof MobileNode) {
				MobileNode mobileNode = (MobileNode)node;
				POICode poiCode = mobileNode.getPOICode();
				if (poiCode != null) {
					String type = poiCode.getTypeAsDescription(); 
					if (type != null) {
						return type;
					}
				}
			}			
			return null;
		}
	}
	
}
