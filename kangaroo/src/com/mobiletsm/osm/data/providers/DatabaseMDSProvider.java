package com.mobiletsm.osm.data.providers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.travelingsalesman.routing.IVehicle;

import com.mobiletsm.osm.OsmHelper;
import com.mobiletsm.osm.data.MobileInterfaceDataSet;
import com.mobiletsm.osm.data.MobileRoutingInterfaceDataSet;
import com.mobiletsm.osm.data.adapters.MDSDatabaseAdapter;
import com.mobiletsm.osm.data.searching.POINodeSelector;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileNode;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;

public class DatabaseMDSProvider extends MobileDataSetProvider {

	/* database connection stuff */
	
	public DatabaseMDSProvider(MDSDatabaseAdapter adapter) {
		super(adapter);
	}

	
	@Override
	public boolean open(String source) {
		if (!isOpen()) {
			adapter.setMaps(streetNodes, completeWays, reducedWays,	waysForNodes);
			return adapter.open(source);
		} else {
			return false;
		}
	}
	
	
	@Override
	public boolean isOpen() {
		return (adapter != null && adapter.isOpen());
	}
	
	
	@Override
	public void close() {
		if (isOpen()) {
			adapter.close();
		}
	}
	
	
	/*  */	
	
	private boolean routingMapPresent = false;
	

	private Map<Long, Node> streetNodes = Collections.synchronizedMap(new HashMap<Long, Node>());
	
	
	private Map<Long, Way> reducedWays = Collections.synchronizedMap(new HashMap<Long, Way>());
	
	
	private Map<Long, Way> completeWays = Collections.synchronizedMap(new HashMap<Long, Way>());
	
	
	private Map<Long, Set<Long>> waysForNodes = Collections.synchronizedMap(new HashMap<Long, Set<Long>>());

	
	
	public boolean isStreetNode(long nodeId) {
		throw new RuntimeException("");
	}
	
	
	public Node getNearestStreetNode(LatLon center) {		
		adapter.loadAllStreetNodesAround(center);
				
		double minDist = Double.MAX_VALUE;
		Node minDistNode = null;
		for (Node node : streetNodes.values()) {
			LatLon nodePos = new LatLon(node.getLatitude(), node.getLongitude());
			double dist = center.distance(nodePos);
			if (dist < minDist) {
				minDist = dist;
				minDistNode = node;
			}
		}
		
		if (minDistNode != null)
			return minDistNode;
		else
			return null;
	}
	

	public MobileInterfaceDataSet getRoutingDataSet(long fromNodeId, long toNodeId, IVehicle vehicle) {
		
		if (vehicle != null)
			throw new RuntimeException("getRoutingDataSet: vehicle not yet supported");		
		
		if (!routingMapPresent) {
			adapter.loadRoutingStreetNodesIncluding(fromNodeId, toNodeId);
			adapter.loadReducedWays();
		} else {
			adapter.loadNodes(fromNodeId, toNodeId, false);
		}			
			
		routingMapPresent = true;
		
		adapter.loadCompleteWaysForNodes(fromNodeId, toNodeId);
		//TODO: set distToPre in new complete ways
		
		long fromWayId = getWayForNode(fromNodeId);		
		long toWayId = getWayForNode(toNodeId);		
		
		adapter.loadAllStreetNodesForWays(fromWayId, toWayId);
		
		MobileRoutingInterfaceDataSet dataSet = new MobileRoutingInterfaceDataSet(fromWayId, toWayId, vehicle);
		dataSet.setMaps(streetNodes, completeWays, reducedWays, waysForNodes);		
		
		return dataSet;
	}

	
	
	private long getWayForNode(long nodeId) {
		Set<Long> wayIds = waysForNodes.get(nodeId);
		if (wayIds != null && wayIds.size() == 1)
			return wayIds.iterator().next();
		else
			return -1;
	}



	/* methods not yet supported */
	
	public MobileInterfaceDataSet getCompleteDataSet() {
		throw new UnsupportedOperationException("getFullDataSet() not supported by DatabaseMDSProvider");		
	}
	

	public MobileInterfaceDataSet getPOINodeDataSet(LatLon center, POINodeSelector selector) {
		throw new UnsupportedOperationException("getPOINodeDataSet() not supported by DatabaseMDSProvider");
	}


	public MobileInterfaceDataSet updatePOINodeDataSet(LatLon center, POINodeSelector selector) {
		throw new UnsupportedOperationException("updatePOINodeDataSet() not supported by DatabaseMDSProvider");
	}


	public MobileInterfaceDataSet updateRoutingDataSet(long fromNodeId,	long toNodeId, IVehicle vehicle) {
		throw new UnsupportedOperationException("updateRoutingDataSet() not supported by DatabaseMDSProvider");
	}

		
}
