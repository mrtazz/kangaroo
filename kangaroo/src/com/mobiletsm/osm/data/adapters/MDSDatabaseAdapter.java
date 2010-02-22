package com.mobiletsm.osm.data.adapters;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import com.mobiletsm.osm.OsmHelper;
import com.mobiletsm.osm.data.MapTile;
import com.mobiletsm.routing.Place;

public abstract class MDSDatabaseAdapter {

	
	protected void sqlLog(String sql) {
		//System.out.println("SQL query: " + sql);
	}
	
	
	protected String SQL_loadAllStreetNodesAround(Place center, double radius) {
		// earth radius
		final double earthRadius = 6378140;
		
		// scale factor between root-squared-lat-lon distance and distance in meters
		double scaleFactor = 2 * Math.PI * (earthRadius * Math.cos(Math.toRadians(center.getLatitude()))) / 360;
		
		String sql = String.format(Locale.US, "SELECT id,lat,lon FROM nodes WHERE stnd<=-1 AND " +
				"((lat-%f)*(lat-%f) + (lon-%f)*(lon-%f)) < %f;",
				center.getLatitude(), center.getLatitude(), 
				center.getLongitude(), center.getLongitude(),
				Math.pow(radius / scaleFactor, 2));
		sqlLog(sql);
		return sql;
	}
	
	
	protected String SQL_loadAllStreetNodesForWays(long fromWayId, long toWayId) {
		Set<Long> ids = new HashSet<Long>();		
		if (fromWayId != -1) {
			List<WayNode> wayNodeIds1 = completeWays.get(fromWayId).getWayNodes();				
			for (WayNode wayNode : wayNodeIds1)
				ids.add(wayNode.getNodeId());
		}			
		if (toWayId != -1) {
			List<WayNode> wayNodeIds2 = completeWays.get(toWayId).getWayNodes();			
			for (WayNode wayNode : wayNodeIds2)
				ids.add(wayNode.getNodeId());
		}		
		if (ids.size() > 0) {		
			StringBuffer sql = new StringBuffer("SELECT id,lat,lon FROM nodes WHERE id IN (");			
			Iterator<Long> id_itr = ids.iterator();
			while (id_itr.hasNext()) {
				sql.append(id_itr.next());
				if (id_itr.hasNext())
					sql.append(", ");
				else
					sql.append(");");
			}
			
			sqlLog(sql.toString());
			return sql.toString();
		} else {
			sqlLog("null");
			return null;
		}
	}
	
	
	protected String SQL_loadCompleteWaysForNodes(long fromNodeId, long toNodeId) {
		String sql = String.format("SELECT ways FROM nodes WHERE id=%d OR id=%d;", fromNodeId, toNodeId);
		sqlLog(sql);
		return sql;
	}

	
	protected String SQL_loadFullWay(long wayId) {
		String sql = String.format("SELECT id,name,highway,tags,wn FROM ways WHERE id=%d;", wayId);			
		sqlLog(sql);
		return sql;
	}
	
	
	protected String SQL_loadReducedWays() {
		String sql = "SELECT id,name,highway,tags,wn_red FROM ways;";
		sqlLog(sql);
		return sql;
	}

	
	protected String SQL_loadRoutingStreetNodes() {
		String sql = "SELECT id,lat,lon FROM nodes WHERE stnd=-1;";
		sqlLog(sql);
		return sql;
	}
	
	
	protected String SQL_loadNodes(long nodeId1, long nodeId2, boolean loadTags) {
		String tags = "";
		if (loadTags)
			tags = ",tags";
		String sql = String.format("SELECT id,lat,lon%s FROM nodes WHERE id=%d OR id=%d;", tags, nodeId1, nodeId2);
		sqlLog(sql);
		return sql;		
	}
	
	
	/* methods to be implemented by MDSDatabaseAdapters */
	
	public abstract boolean open(String source);
	
	
	public abstract boolean isOpen();
	
	
	public abstract void close();
	
	
	public abstract void loadAllStreetNodesAround(Place center, double radius);
	
	
	public abstract void loadAllStreetNodesForWays(long fromWayId, long toWayId);
	
	
	public abstract void loadReducedWays();
	
	
	public abstract void loadCompleteWaysForNodes(long fromNodeId, long toNodeId);
	
	
	public abstract void loadCompleteWay(long wayId);
	
	
	public abstract void loadRoutingStreetNodes();
	
	
	public abstract void loadNodes(long nodeId1, long nodeId2, boolean loadTags);
	
	
	/*  */	
	
	protected Set<MapTile> mapTiles = null;

	
	protected Map<Long, Node> streetNodes = null;
	

	protected Map<Long, Way> reducedWays = null;
	
	
	protected Map<Long, Way> completeWays = null;
	
	
	protected Map<Long, Set<Long>> waysForNodes = null;	
	
	
	public void setCompleteWaysMap(Map<Long, Way> map) {
		this.completeWays = map;
	}

	
	public void setMaps(Map<Long, Node> nodes, Map<Long, Way> completeWays,
			Map<Long, Way> reducedWays, Map<Long, Set<Long>> waysForNodes) {
		this.streetNodes = nodes;
		this.completeWays = completeWays;
		this.reducedWays = reducedWays;
		this.waysForNodes = waysForNodes;
	}

	
	public void setStreetNodesMap(Map<Long, Node> map) {
		this.streetNodes = map;
	}

	
	public void setReducedWaysMap(Map<Long, Way> map) {
		this.reducedWays = map;
	}

	
	public void setWaysForNodesMap(Map<Long, Set<Long>> map) {
		this.waysForNodes = map;
	}
	
	
	protected void addWayToMap(Map<Long, Way> wayMap, Way way) {		
		if (!wayMap.containsKey(way.getId())) {
			wayMap.put(way.getId(), way);			
			List<Long> wayNodeIds = OsmHelper.getWayNodeIds(way);
			for (Long id : wayNodeIds) {				
				Set<Long> wayIds = waysForNodes.get(id);
				if (wayIds == null)
					wayIds = new HashSet<Long>();
				wayIds.add(way.getId());
				waysForNodes.put(id, wayIds);
			}			
		}		
	}
	
	
}
