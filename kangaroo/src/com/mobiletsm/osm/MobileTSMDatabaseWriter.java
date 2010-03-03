package com.mobiletsm.osm;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.coordinates.Bounds;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.travelingsalesman.routing.IVehicle;

import com.mobiletsm.osm.data.searching.CombinedSelector;
import com.mobiletsm.osm.data.searching.POINodeSelector;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;
import com.mobiletsm.routing.AllStreetVehicle;

public class MobileTSMDatabaseWriter {

	/*
	 * TODO: change data structure
	 * 
	 * table: street_nodes_0
	 * 			id integer primary key
	 * 			lat real not null			
	 * 			lon real not null
	 * 			ways text not null
	 * 			type integer not null
	 * 
	 * 
	 * table: poi_nodes_0
	 * 			id integer primary key
	 * 			lat real not null
	 * 			lon real not null
	 * 			poicode integer not null
	 * 			nst integer not null
	 * 
	 * 
	 * table: ways_0
	 * 
	 * 
	 * 
	 */
	
	private static final String createTable_Nodes = 
			"CREATE TABLE IF NOT EXISTS nodes (" +
			/* osm node id */
			"id integer primary key," +
			/* latitude */
			"lat real not null," +
			/* longitude */
			"lon real not null," +
			/* serialized tags */
			"tags text," +
			/* serialized list of ways connected to this node */
			"ways text default null," + 
			/* osm node id of (s)treet (n)ode */
			"stnd integer default null," +
			/* id of amenity type, -1 if none */
			"amenity integer not null" +
		");";
	
	
	private static final String createTable_Ways =
			"CREATE TABLE IF NOT EXISTS ways (" +
	
			"id integer primary key," +
			
			"name text default null," +
			
			"highway text default null," +
			
			"tags text default null," +
			
			"tag_flags integer not null," +
			/* all way nodes (osm node ids) */
			"wn text not null," +
			/*  reduced list of way nodes*/
			"wn_red text default null" +
		");";
	
	
	
	private PrintStream logStream = null;
	
	
	private void log(String msg) {
		if (logStream != null) {
			logStream.println("MobileTSMDatabaseWriter: " + msg);
		}
	}
	
	
	public void setLogStream(PrintStream logStream) {
		this.logStream = logStream;
	}
	
	
	
	public void writeDatabase(IDataSet map, String database) {
		
		/* TODO: 
		 * - split table 'nodes' in 'street_nodes' and 'poi_nodes' 
		 * - add column in 'ways' for maxspeed 
		 */
		
		log("writeDatabase: input: # nodes = " + OsmHelper.getNumberOfNodes(map));
		log("writeDatabase: input: # ways = " + OsmHelper.getNumberOfWays(map));
		
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection(database);
			
			Statement statement = connection.createStatement();
			statement.executeUpdate("drop table if exists nodes;");
			statement.executeUpdate("drop table if exists ways;");
			statement.executeUpdate(createTable_Nodes);
			statement.executeUpdate(createTable_Ways);
			
			connection.setAutoCommit(false);
		
			PreparedStatement ps;
			
			ps = connection.prepareStatement(
					"INSERT INTO nodes (id, lat, lon, tags, ways, stnd, amenity) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?);");	
			
			IVehicle routingVehicle = new AllStreetVehicle();
			Selector poiSelector = new POINodeSelector();
			Selector selector = new CombinedSelector(routingVehicle, poiSelector, CombinedSelector.FUNCTION_OR);			
			
			IDataSet routingMap = OsmHelper.applyFilter(map, routingVehicle);
			IDataSet completeMap = OsmHelper.applyFilter(map, selector);			
			
			Collection<Long> intermediateWayNodes = OsmHelper.getIntermediateWayNodes(routingMap);
			
			int nStreetNodes = 0;
			int nIntermediateStreetNodes = 0;
			
			Iterator<Node> nodes = completeMap.getNodes(Bounds.WORLD);
			while (nodes.hasNext()) {
				Node node = nodes.next();
				Collection<Tag> tags = node.getTags();
				
				/* set node id */				
				ps.setLong(1, node.getId());
				/* set coordinates (latitude and longitude) */
				ps.setDouble(2, node.getLatitude());
				ps.setDouble(3, node.getLongitude());
				/* set tags */
				String tagString = OsmHelper.packTagsToString(tags);
				ps.setString(4, tagString);				
				/* set ways for node */				
				ps.setString(5, OsmHelper.packLongsToString(OsmHelper.getWayIdsForNode(completeMap, node)));				
				/* set nearest street node */
				long stnd;
				if (routingVehicle.isAllowed(completeMap, node)) {
					if (intermediateWayNodes.contains(node.getId())) {
						stnd = OsmHelper.NODES_STND_IS_INTERMEDIATE_STREET_NODE;
						nIntermediateStreetNodes++;
					} else {
						stnd = OsmHelper.NODES_STND_IS_STREET_NODE;
						nStreetNodes++;
					}
				} else {
					Node nearestStreetNode = routingMap.getNearestNode(
							new LatLon(node.getLatitude(), node.getLongitude()), null);
					if (nearestStreetNode != null)
						stnd = nearestStreetNode.getId();
					else
						throw new RuntimeException("OsmHelper.writeToMobileDatabase(): " +
								"missing street nodes in routing map");
				}
				
				ps.setLong(6, stnd);				
				/* set amenity code */
				ps.setInt(7, 0);
				
				ps.execute();
			}				
			
			
			
			ps = connection.prepareStatement(
				"INSERT INTO ways (id, name, tags, highway, tag_flags, wn, wn_red) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?);");	
						
			Iterator<Way> ways = completeMap.getWays(Bounds.WORLD);
			while (ways.hasNext()) {
				Way way = ways.next();
				Collection<Tag> tags = way.getTags();
				
				/* set way id */
				ps.setLong(1, way.getId());				
				/* set name if available */
				String name = OsmHelper.getAndRemoveTag(tags, "name");
				if (name == null) name = "";
				ps.setString(2, name);
				/* set tag flags */
				int tagFlags = OsmHelper.getAndRemoveTagFlags(tags);
				ps.setInt(5, tagFlags);
				/* set highway */
				String highway = OsmHelper.getAndRemoveTag(tags, "highway");
				if (highway == null) highway = "";
				ps.setString(4, highway);
				/* set tags */
				String tagString = OsmHelper.packTagsToString(tags);				
				if (tagString == null) tagString = "";
				ps.setString(3, tagString);				
					
				/* set way nodes */
				String wayNodeString = OsmHelper.packLongsToString(OsmHelper.getWayNodeIds(way));
				ps.setString(6, wayNodeString);
				
				/* set reduced list of way nodes */
				MobileWay newWay = new MobileWay(way.getId());
				double length = 0;
				WayNode lastWayNode = null;
				for (WayNode wayNode : way.getWayNodes()) {
					if (lastWayNode != null) {
						length += LatLon.distanceInMeters(completeMap.getNodeByID(lastWayNode.getNodeId()), 
								completeMap.getNodeByID(wayNode.getNodeId()));
						if (!intermediateWayNodes.contains(wayNode.getNodeId())) {
							newWay.addWayNode(wayNode.getNodeId(), length);
							length = 0;
						}
					} else {
						newWay.addWayNode(wayNode.getNodeId(), 0);
					}
					lastWayNode = wayNode;
				}	
				ps.setString(7, OsmHelper.packWayNodesToString(newWay));					
				ps.execute();
			}
			
			
			log("writeDatabase: # street nodes = " + nStreetNodes);
			log("writeDatabase: # intermediate way nodes = " + nIntermediateStreetNodes);
			
			ResultSet rs = statement.executeQuery("SELECT * FROM nodes;");
		    int i = 0;
		    while(rs.next()) i++;
		    log("writeDatabase: # nodes written = " + i);
		    
		    rs = statement.executeQuery("SELECT * FROM ways;");
		    i = 0;
		    while(rs.next()) i++;
		    log("writeDatabase: # ways written = " + i);
		    		    
		    
		    rs = statement.executeQuery("SELECT * FROM ways WHERE id=17744176;");
		    while(rs.next()) {
		    	log("writeDatabase: wayid:17744176: wn_red = " + rs.getString("wn_red"));		    	
		    }
		    
		    
		    connection.setAutoCommit(true);
			connection.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
