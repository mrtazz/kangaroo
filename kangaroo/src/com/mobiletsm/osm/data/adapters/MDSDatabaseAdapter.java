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

import com.mobiletsm.osm.MobileTSMDatabaseWriter;
import com.mobiletsm.osm.OsmHelper;
import com.mobiletsm.osm.data.MapTile;
import com.mobiletsm.osm.data.searching.POICode;
import com.mobiletsm.routing.Place;

public abstract class MDSDatabaseAdapter {

	
	protected void sqlLog(String sql) {
		System.out.println("SQL query: " + sql);
	}
	
	
	/* database table structure v1 */
	
	/*
	protected String SQL_loadAllStreetNodesAround(Place center, double radius) {
		// earth radius
		final double earthRadius = 6378140;
		
		// scale factor between root-squared-lat-lon distance and distance in meters
		double scaleFactor = 2 * Math.PI * (earthRadius * Math.cos(Math.toRadians(center.getLatitude()))) / 360;
		
		String sql = String.format(Locale.US, "SELECT id,lat,lon,ways FROM nodes WHERE stnd<=-1 AND " +
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
	
	
	protected String SQL_getWaysForNodes(long fromNodeId, long toNodeId) {
		String sql = String.format("SELECT ways FROM nodes WHERE id=%d OR id=%d;", fromNodeId, toNodeId);
		sqlLog(sql);
		return sql;
	}

	
	protected String SQL_loadCompleteWay(long wayId) {
		String sql = String.format("SELECT id,name,highway,tags,wn FROM ways WHERE id=%d;", wayId);			
		sqlLog(sql);
		return sql;
	}

	
	protected String SQL_loadReducedWays(List<Long> ways) {
		if (ways != null && ways.size() > 0) {		
			StringBuffer sql = new StringBuffer("SELECT id,name,highway,tags,wn_red FROM ways WHERE id IN (");			
			Iterator<Long> id_itr = ways.iterator();
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
			String sql = "SELECT id,name,highway,tags,wn_red FROM ways;";
			sqlLog(sql);
			return sql;
		}
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
	*/
	
	
	/* database table structure version 2 */
	
	protected String SQL_loadAllStreetNodesAround(Place center, double radius) {
		// earth radius
		final double earthRadius = 6378140;
		
		// scale factor between root-squared-lat-lon distance and distance in meters
		double scaleFactor = 2 * Math.PI * (earthRadius * Math.cos(Math.toRadians(center.getLatitude()))) / 360;
		
		double latlonRadius = Math.pow(radius / scaleFactor, 2);		
		if (latlonRadius < 1E-5) {
			latlonRadius = 1E-5;
		}
		
		String sql = String.format(Locale.US, "SELECT id,lat,lon,ways FROM street_nodes_0 " +
				"WHERE ((lat-%.6f)*(lat-%.6f) + (lon-%.6f)*(lon-%.6f)) < %.6f;",
				center.getLatitude(), center.getLatitude(), 
				center.getLongitude(), center.getLongitude(),
				latlonRadius);
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
			StringBuffer sql = new StringBuffer("SELECT id,lat,lon FROM street_nodes_0 WHERE id IN (");			
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
	
	
	protected String SQL_getWaysForNodes(long fromNodeId, long toNodeId) {
		String sql = String.format("SELECT ways FROM street_nodes_0 WHERE id=%d OR id=%d;", fromNodeId, toNodeId);
		sqlLog(sql);
		return sql;
	}

	
	protected String SQL_loadCompleteWay(long wayId) {
		String sql = String.format("SELECT id,name,highway,tags,waynodes FROM ways_0 WHERE id=%d;", wayId);			
		sqlLog(sql);
		return sql;
	}

	
	protected String SQL_loadReducedWays(List<Long> ways) {
		if (ways != null && ways.size() > 0) {		
			StringBuffer sql = new StringBuffer("SELECT id,name,highway,tags,waynodes_red FROM ways_0 WHERE id IN (");			
			Iterator<Long> id_itr = ways.iterator();
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
			String sql = "SELECT id,name,highway,tags,waynodes_red FROM ways_0;";
			sqlLog(sql);
			return sql;
		}
	}
	
	
	protected String sql_loadAllEssentialStreetNodes() {
		String sql = "SELECT id,lat,lon FROM street_nodes_0 WHERE type = " + 
			MobileTSMDatabaseWriter.STREET_NODE_TYPE_ESSENTIAL + ";";
		sqlLog(sql);
		return sql;
	}

	
	protected String sql_loadPOINodes(POICode poiCode) {
		String sql;
		if (poiCode != null) {
			sql = "SELECT id,lat,lon,tags,nst FROM poi_nodes_0 " +
				"WHERE poicode = " + poiCode.getId() + ";";
		} else {
			sql = "SELECT id,lat,lon,tags,nst FROM poi_nodes_0;";
		}
		sqlLog(sql);
		return sql;
	}
	
	
	protected String sql_loadStreetNodes(long nodeId1, long nodeId2, boolean loadTags) {
		String tags = "";
		if (loadTags) {
			tags = ",tags";
		}
		String sql = String.format("SELECT id,lat,lon%s FROM street_nodes_0 WHERE id=%d OR id=%d;", tags, nodeId1, nodeId2);
		sqlLog(sql);
		return sql;		
	}
	
	
	
	/* methods to be implemented by MDSDatabaseAdapters */
	
	public abstract boolean open(String source);
	
	
	public abstract boolean isOpen();
	
	
	public abstract void close();
	
	
	public abstract int loadAllStreetNodesAround(Place center, double radius);
	
	
	public abstract void loadAllStreetNodesForWays(long fromWayId, long toWayId);
	
	
	public abstract void loadReducedWays();
	
	
	public abstract void loadReducedWays(List<Long> ways);
	
	
	public abstract List<Long> loadCompleteWaysForNodes(long fromNodeId, long toNodeId);
	
	
	public abstract void loadCompleteWay(long wayId);
	
	
	public abstract void loadAllEssentialStreetNodes();
	
	
	public abstract int loadPOINodes(POICode poiCode);
	
	
	public abstract void loadStreetNodes(long nodeId1, long nodeId2, boolean loadTags);
	
	
	/*  */	
	
	/* TODO: use map tiles */
	protected Set<MapTile> mapTiles = null;

	
	protected Map<Long, Node> poiNodes = null;
	
	
	protected Map<Long, Node> streetNodes = null;
	

	protected Map<Long, Way> reducedWays = null;
	
	
	protected Map<Long, Way> completeWays = null;
	
	
	protected Map<Long, Set<Long>> waysForNodes = null;	
	

	
	public void setMaps(Map<Long, Node> poiNodes, Map<Long, Node> streetNodes, Map<Long, Way> completeWays,
			Map<Long, Way> reducedWays, Map<Long, Set<Long>> waysForNodes) {
		this.poiNodes = poiNodes;
		this.streetNodes = streetNodes;
		this.completeWays = completeWays;
		this.reducedWays = reducedWays;
		this.waysForNodes = waysForNodes;
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
