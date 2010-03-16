package com.mobiletsm.osm;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.openstreetmap.travelingsalesman.routing.IVehicle;

import com.mobiletsm.osm.data.searching.CombinedSelector;
import com.mobiletsm.osm.data.searching.POICode;
import com.mobiletsm.osm.data.searching.POINodeSelector;
import com.mobiletsm.routing.AllStreetVehicle;

public class MobileTSMDatabaseWriter {

	/*
	 * TODO: change data structure
	 * 
	 * table: street_nodes_0
	 * 			id integer primary key
	 * 			lat real not null			
	 * 			lon real not null
	 * 			tags text not null
	 * 			ways text not null
	 * 			type integer not null
	 * 
	 * 
	 * table: poi_nodes_0
	 * 			id integer primary key
	 * 			lat real not null
	 * 			lon real not null
	 * 			poicode integer not null
	 * 			tags text not null
	 * 			nst integer not null
	 * 
	 * 
	 * table: ways_0
	 *			id integer primary key 
	 * 			name text not null
	 * 			highway not null
	 * 			maxspeed integer not null
	 * 			tags text not null
	 * 			flags integer not null
	 * 			waynodes text not null
	 * 			waynodes_red text not null
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
			logStream.println(msg);
		}
	}
	
	
	public void setLogStream(PrintStream logStream) {
		this.logStream = logStream;
	}
	
	
	private String database;
	
	
	private Connection connection = null;
	
	
	public MobileTSMDatabaseWriter(String database) {
		this.database = database;
	}
	
	
	/**
	 * open a connection to the specified database
	 * @return true if connected successfully, false otherwise
	 */
	public boolean openDatabase() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(database);
			return !connection.isClosed();
		} catch (Exception e) {
			return false;
		}
	}
	
	
	/**
	 * close the database connection if open
	 */
	public boolean closeDatabase() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
	}
	
	
	public void writeDatabase(IDataSet map) {
		
		try {
			if (connection == null || connection.isClosed()) {
				throw new RuntimeException("MobileTSMDatabaseWriter.writeDatabase(): No connection opened");
			}
		} catch (SQLException e1) {
			throw new RuntimeException("MobileTSMDatabaseWriter.writeDatabase():" + e1.getMessage());
		}		
		
		log("writeDatabase: input: # nodes = " + OsmHelper.getNumberOfNodes(map));
		log("writeDatabase: input: # ways = " + OsmHelper.getNumberOfWays(map));
		
		try {
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
				String tagString = OsmHelper.serializeTags(tags);
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
				String tagString = OsmHelper.serializeTags(tags);				
				if (tagString == null) tagString = "";
				ps.setString(3, tagString);				
					
				/* set way nodes */
				String wayNodeString = OsmHelper.packLongsToString(OsmHelper.getWayNodeIds(way));
				ps.setString(6, wayNodeString);
				
				/* set reduced list of way nodes */
				ps.setString(7, OsmHelper.serializeMobileWayNodes(OsmHelper.getReducedWay(completeMap, intermediateWayNodes, way)));					
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public static final int STREET_NODE_TYPE_ESSENTIAL = 0;
	
	
	public static final int STREET_NODE_TYPE_INTERMEDIATE = 1;
	
	
	private static final String createTable_poi_nodes_0 =
		"CREATE TABLE IF NOT EXISTS poi_nodes_0 (" +
		/* id of node */
		"id integer primary key," +
		/* latitude of node */ 
		"lat real not null," +
		/* longitude of node */
		"lon real not null," +
		/* poi code of node */
		"poicode integer not null," +
		/* tag list of node */
		"tags text not null," +
		/* id of nearest street node to this node */
		"nst integer not null" +
	");";
		
		
	private static final String createTable_street_nodes_0 =
		"CREATE TABLE IF NOT EXISTS street_nodes_0 (" +
		/* id of street node */
		"id integer primary key," +
		/* latitude of street node */
		"lat real not null," +
		/* longitude of street node */
		"lon real not null," +
		/* tag list of street node */
		"tags text not null," +
		/* way list of street node */
		"ways text not null," +
		/* type of street node */
		"type integer not null" +
	");";
	
	
	private static final String createTable_ways_0 =
		"CREATE TABLE IF NOT EXISTS ways_0 (" +
		/* id of way */
		"id integer primary key," +
		/* name of way (from tags) */
		"name text not null," +
		/* highway type (from tags) */
		"highway text " +
		"not null," +
		/* maximum speed on way (from tags) */
		"maxspeed integer not null," +
		/* tag list */
		"tags text not null," +
		/* flags (from tags) */
		"flags integer not null," +
		/* unreduced list of way nodes */
		"waynodes text not null," +
		/* reduced list of way nodes */
		"waynodes_red text not null" +
	");";
	
	
	private static final String createTable_index =
		"CREATE TABLE IF NOT EXISTS index (" +
		/*  */
		"id integer primary key," +
		/* keys */
		"key text not null," + 
		/* values */
		"value text not null" + 
	");";
	
	
	private static final String createTable_android_metadata = 
		"CREATE TABLE \"android_metadata\" (\"locale\" TEXT DEFAULT 'en_US')";
	
	
	private static final String insert_android_metadata = 
		"INSERT INTO \"android_metadata\" VALUES ('en_US')";
	
	
	/**
	 * writes the given map to a database using data model V2
	 * @param map
	 */
	public void writeDatabaseV2(IDataSet map) {
		
		try {
			if (connection == null || connection.isClosed()) {
				throw new RuntimeException("MobileTSMDatabaseWriter.writeDatabase(): No connection opened");
			}
		} catch (SQLException e1) {
			throw new RuntimeException("MobileTSMDatabaseWriter.writeDatabase():" + e1.getMessage());
		}		
		
		log("writeDatabaseV2: input: # nodes = " + OsmHelper.getNumberOfNodes(map));
		log("writeDatabaseV2: input: # ways = " + OsmHelper.getNumberOfWays(map));
		
		
		try {
			/* prepare statement */
			Statement statement = connection.createStatement();
			connection.setAutoCommit(false);
			PreparedStatement ps;
			
			/* drop and recreate tables in database */
			statement.executeUpdate("drop table if exists street_nodes_0;");
			statement.executeUpdate("drop table if exists poi_nodes_0;");
			statement.executeUpdate("drop table if exists ways_0;");
			statement.executeUpdate(createTable_street_nodes_0);
			statement.executeUpdate(createTable_poi_nodes_0);
			statement.executeUpdate(createTable_ways_0);
			
			Selector poiNodeSelector = new POINodeSelector();
			
			Selector routingVehicle = new AllStreetVehicle();			
			IDataSet routingMap = OsmHelper.applyFilter(map, routingVehicle);			
			Collection<Long> intermediateWayNodes = OsmHelper.getIntermediateWayNodes(routingMap);
			
			log("writeDatabaseV2: writing street nodes...");			
			
			/* write routing street nodes */
			ps = connection.prepareStatement("INSERT INTO street_nodes_0 " +
			"(id, lat, lon, tags, ways, type) VALUES (?, ?, ?, ?, ?, ?);");			
			int numStreetNodes = 0;
			int numIntermediateStreetNodes = 0;
			int numEssentialStreetNode = 0;
			Iterator<Node> streetNodes = routingMap.getNodes(Bounds.WORLD);
			while (streetNodes.hasNext()) {
				Node node = streetNodes.next();
				
				/* set node id */
				ps.setLong(1, node.getId());
				/* set latitude and longitude */
				ps.setDouble(2, node.getLatitude());
				ps.setDouble(3, node.getLongitude());
				/* set tags */
				ps.setString(4, OsmHelper.serializeTags(node.getTags()));
				/* set ways */
				ps.setString(5, OsmHelper.packLongsToString(OsmHelper.getWayIdsForNode(routingMap, node)));
				/* set type of street node */
				if (intermediateWayNodes.contains(node.getId())) {
					ps.setInt(6, STREET_NODE_TYPE_INTERMEDIATE);
					numIntermediateStreetNodes++;
				} else {
					ps.setInt(6, STREET_NODE_TYPE_ESSENTIAL);
					numEssentialStreetNode++;
				}				
				
				/* execute statement */
				ps.execute();				
				
				numStreetNodes++;
			}
			
			log("writeDatabaseV2: output: # street nodes = " + numStreetNodes);
			log("writeDatabaseV2: output: # intermediate street nodes = " + numIntermediateStreetNodes);
			log("writeDatabaseV2: output: # essential street nodes = " + numEssentialStreetNode);
			
			
			
			log("writeDatabaseV2: writing POI nodes...");	
			
			/* write POI nodes */
			ps = connection.prepareStatement("INSERT INTO poi_nodes_0 " +
					"(id, lat, lon, poicode, tags, nst) VALUES (?, ?, ?, ?, ?, ?);");	
			int numPOINodes = 0;
			Iterator<Node> poiNodes = map.getNodes(Bounds.WORLD);
			while (poiNodes.hasNext()) {
				Node node = poiNodes.next();
				if (poiNodeSelector.isAllowed(map, node)) {
					
					/* get POI code of node */
					POICode poiCode = POICode.createFromTags(node.getTags());

					/* find nearest street node to this node */
					Node nst = routingMap.getNearestNode(new LatLon(node.getLatitude(), node.getLongitude()), routingVehicle);
					if (nst == null) {
						throw new RuntimeException("writeDatabaseV2: ERROR: could not find nearest street node for nodeid:" + node.getId());
					}
					
					/* set node id */
					ps.setLong(1, node.getId());
					/* set latitude and longitude */
					ps.setDouble(2, node.getLatitude());
					ps.setDouble(3, node.getLongitude());
					/* set POI code */
					ps.setInt(4, poiCode.getId());
					/* set tags */
					ps.setString(5, OsmHelper.serializeTags(node.getTags()));
					/* set nearest street node id */
					ps.setLong(6, nst.getId());
					
					/* execute statement */
					ps.execute();					
					
					numPOINodes++;
				}
			}			
			
			log("writeDatabaseV2: output: # POI nodes = " + numPOINodes);
			
			
			log("writeDatabaseV2: writing ways...");			
			
			/* write routing street nodes */
			ps = connection.prepareStatement("INSERT INTO ways_0 (id, name, highway, maxspeed, " +
					"flags, tags, waynodes, waynodes_red) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");	
			int numWays = 0;
			Iterator<Way> ways = routingMap.getWays(Bounds.WORLD);
			while (ways.hasNext()) {
				Way way = ways.next();
				Collection<Tag> tags = way.getTags();
				
				/* set way id */
				ps.setLong(1, way.getId());				
				/* set name if available */
				String name = OsmHelper.getAndRemoveTag(tags, "name");
				if (name == null) name = "";
				ps.setString(2, name);
				/* set highway */
				String highway = OsmHelper.getAndRemoveTag(tags, "highway");
				if (highway == null) highway = "";
				ps.setString(3, highway);
				/* set maximum speed */
				String maxSpeedStr = OsmHelper.getAndRemoveTag(tags, "maxspeed");
				try {
					int maxSpeed = Integer.parseInt(maxSpeedStr);
					ps.setInt(4, maxSpeed);
				} catch (Exception e) { /* TODO: only catch specific exceptions */
					/*  */
					ps.setInt(4, 0);				
				}
				/* set tag flags */
				int tagFlags = OsmHelper.getAndRemoveTagFlags(tags);
				ps.setInt(5, tagFlags);
				/* set tags */
				String tagString = OsmHelper.serializeTags(tags);				
				if (tagString == null) tagString = "";
				ps.setString(6, tagString);									
				/* set way nodes */
				String wayNodeString = OsmHelper.packLongsToString(OsmHelper.getWayNodeIds(way));
				ps.setString(7, wayNodeString);				
				/* set reduced list of way nodes */
				ps.setString(8, OsmHelper.serializeMobileWayNodes(OsmHelper.getReducedWay(routingMap, intermediateWayNodes, way)));					
				ps.execute();
				
				numWays++;
			}
			
			log("writeDatabaseV2: output: # ways = " + numWays);
			
			connection.setAutoCommit(true);
			
			
			/* write android metadata table */
			statement.execute(createTable_android_metadata);
			statement.execute(insert_android_metadata);
			
			
			/* write database version */
			//ps = connection.prepareStatement("");
			
			
						
		} catch (Exception e) {			
			e.printStackTrace();
		}		
		
	}

	
	public void readDatabaseV2() {
		try {
			/* prepare statement */
			Statement statement = connection.createStatement();
			
			log("readDatabaseV2: reading and summarizing database...");
			
			/* check written database rows */
			ResultSet rs = statement.executeQuery("SELECT * FROM street_nodes_0;");
			int numStreetNodes = 0;
			int numIntermediateStreetNodes = 0;
			int numEssentialStreetNode = 0;
		    while(rs.next()) {
		    	numStreetNodes++;
		    	int type = rs.getInt("type");
		    	if (type == STREET_NODE_TYPE_ESSENTIAL) {
		    		numEssentialStreetNode++;
		    	} else if (type == STREET_NODE_TYPE_INTERMEDIATE) {
		    		numIntermediateStreetNodes++;
		    	}
		    }
		    log("readDatabaseV2: database: # street nodes = " + numStreetNodes);
			log("readDatabaseV2: database: # intermediate street nodes = " + numIntermediateStreetNodes);
			log("readDatabaseV2: database: # essential street nodes = " + numEssentialStreetNode);
			
			
			rs = statement.executeQuery("SELECT * FROM poi_nodes_0;");
			int numPOINodes = 0;
		    while(rs.next()) {
		    	numPOINodes++;
		    }
		    log("readDatabaseV2: database: # POI nodes = " + numPOINodes);
		    
		} catch (Exception e) {		/* TODO: catch specific exceptions */
			
		}
	}
	
	
}
