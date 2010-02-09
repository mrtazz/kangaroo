package com.mobiletsm.osm.data;

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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import com.mobiletsm.osm.OsmHelper;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileNode;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;

public class MDSSQLiteDatabaseAdapter extends MDSDatabaseAdapter {

	
	private Connection connection = null;
	
	
	@Override
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	@Override
	public void loadAllStreetNodesAround(LatLon center) {
		try {
			Statement statement = connection.createStatement();			
			String sql = SQL_loadAllStreetNodesAround(center);			
			ResultSet rs = statement.executeQuery(sql);			
			while(rs.next()) {
				long id = rs.getLong("id");
				double lat = rs.getDouble("lat");
				double lon = rs.getDouble("lon");				
				Node node = new MobileNode(id, lat, lon);
				if (!streetNodes.containsKey(node.getId())) {
					streetNodes.put(node.getId(), node);
				}	
			}			
			rs.close();
			statement.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void loadAllStreetNodesForWays(long fromWayId, long toWayId) {
		try {
			String sql = SQL_loadAllStreetNodesForWays(fromWayId, toWayId);
			if (sql != null) {
				Statement statement = connection.createStatement();				
				ResultSet rs = statement.executeQuery(sql);				
				while (rs.next()) {
					long id = rs.getLong("id");
					double lat = rs.getDouble("lat");
					double lon = rs.getDouble("lon");				
					Node node = new MobileNode(id, lat, lon);
					if (!streetNodes.containsKey(node.getId())) {
						streetNodes.put(node.getId(), node);
					}	
				}			
				rs.close();
				statement.close();						
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	
	@Override
	public void loadCompleteWaysForNodes(long fromNodeId, long toNodeId) {
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(SQL_loadCompleteWaysForNodes(fromNodeId, toNodeId));			
			while(rs.next()) {
				String ways = rs.getString("ways");
				List<Long> wayIds = OsmHelper.unpackStringToLongs(ways);
				if (wayIds.size() == 1) {
					loadCompleteWay(wayIds.get(0));
				}				
			}			
			rs.close();
			statement.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void loadCompleteWay(long wayId) {
		try {
			Statement statement = connection.createStatement();			
			ResultSet rs = statement.executeQuery(SQL_loadFullWay(wayId));			
			while (rs.next()) {
				long id = rs.getLong("id");				
				String name = rs.getString("name");
				String highway = rs.getString("highway");
				String tags = rs.getString("tags");
				String wn = rs.getString("wn");	
				Way way = new MobileWay(id, tags, wn);	
				OsmHelper.addSpecificTags(way, name, highway);
				addWayToMap(completeWays, way);
			}			
			rs.close();
			statement.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	
	@Override
	public void loadReducedWays() {
		try {
			Statement statement = connection.createStatement();			
			ResultSet rs = statement.executeQuery(SQL_loadReducedWays());			
			while (rs.next()) {
				long id = rs.getLong("id");	
				String name = rs.getString("name");
				String highway = rs.getString("highway");
				String tags = rs.getString("tags");
				String wn_red = rs.getString("wn_red");
				Way way = new MobileWay(id, tags, wn_red);
				OsmHelper.addSpecificTags(way, name, highway);
				addWayToMap(reducedWays, way);
			}			
			rs.close();
			statement.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	
	@Override
	public void loadRoutingStreetNodesIncluding(long nodeId1, long nodeId2) {
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(SQL_loadRoutingStreetNodesIncluding(nodeId1, nodeId2));			
			while(rs.next()) {
				long id = rs.getLong("id");
				double lat = rs.getDouble("lat");
				double lon = rs.getDouble("lon");				
				Node node = new MobileNode(id, lat, lon);
				if (!streetNodes.containsKey(node.getId())) {
					streetNodes.put(node.getId(), node);
				}	
			}			
			rs.close();
			statement.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public boolean open(String source) {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(source);
			return true;
		} catch (Exception e) {
			return false;
		}
	}


}
