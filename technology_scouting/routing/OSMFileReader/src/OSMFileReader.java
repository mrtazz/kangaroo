import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.coordinates.Bounds;
import org.openstreetmap.osm.data.searching.NearestStreetSelector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.travelingsalesman.routing.IVehicle;

import com.kangaroo.tsm.osm.io.FileLoader;


public class OSMFileReader {


	public static String Create_Table_Nodes = 
		"create table if not exists nodes (node_id integer primary key, lat real not null, lon real not null, tags text, " +
		"tags_ text, node_ways text, isstreetnode integer not null, amenity integer not null);";
	
	public static String Create_Table_Ways =
		"create table if not exists ways (way_id integer primary key, tags text, tags_ text, way_nodes text);";
			
	
	public static void writeToDatabase(MemoryDataSet map) {

		int wayid = 30589240;
		
		System.out.println("writeToDatabase: input: # nodes = " + map.getNodesCount());
		System.out.println("writeToDatabase: input: # ways = " + map.getWaysCount());
		
		try {
			
			Class.forName("org.sqlite.JDBC");
			
			Connection connection = 
				DriverManager.getConnection("jdbc:sqlite:/Users/andreaswalz/Downloads/map-em.db");
						
			Statement statement = connection.createStatement();
			PreparedStatement ps;
		
			statement.executeUpdate("drop table if exists nodes;");
			statement.executeUpdate("drop table if exists ways;");
			statement.executeUpdate(Create_Table_Nodes);
			statement.executeUpdate(Create_Table_Ways);
				
			connection.setAutoCommit(false);
		
			/* write nodes to database */
			ps = connection.prepareStatement(
					"INSERT INTO nodes (node_id, lat, lon, tags, node_ways, isstreetnode, amenity) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?);");
			
			Selector selector = new NearestStreetSelector();			
			Iterator<Node> node_itr = map.getNodes(null);
			while(node_itr.hasNext()) {
				Node node = node_itr.next();				
				String tags = OsmHelper.packTagsToString(node.getTags());	//tagPacker(node.getTags());
				
				int isstreetnode = 0;
				if (selector.isAllowed(map, node))
					isstreetnode = 1;
				
				StringBuffer ways_str = new StringBuffer();			
				Iterator<Way> ways = map.getWaysForNode(node.getId());
				while(ways.hasNext()) {
					Way way = ways.next();
					
					String longString = Long.toHexString(way.getId());
					for (int i = 8; i > longString.length(); i--)
						ways_str.append("0");					
					ways_str.append(longString);								
				}				
				
				ps.setLong(1, node.getId());
				ps.setDouble(2, node.getLatitude());
				ps.setDouble(3, node.getLongitude());
				ps.setString(4, tags);
				ps.setString(5, ways_str.toString());
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
				
				String tags = OsmHelper.packTagsToString(way.getTags());
				String wayNodes = OsmHelper.packLongsToString(OsmHelper.getWayNodes(way));
								
				if (way.getId() == wayid) {
					System.out.println("writeToDatabase: tags = " + way.getTags().toString());
					System.out.println("writeToDatabase: wayNodes = " + OsmHelper.getWayNodes(way).toString());
					System.out.println("writeToDatabase: packed-tags = " + tags);
					System.out.println("writeToDatabase: packed-wayNodes = " + wayNodes);
				}
				
				ps.setLong(1, way.getId());
				ps.setString(2, tags);
				ps.setString(3, wayNodes);
				ps.execute();
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
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		// load map file
		File mapFile = new File("/Users/andreaswalz/Downloads/map.osm");		
		MemoryDataSet map = (new FileLoader(mapFile)).parseOsm();		
		
		System.out.println("FileLoader: output: # nodes = " + map.getNodesCount());
		System.out.println("FileLoader: output: # ways = " + map.getWaysCount());
		
		MemoryDataSet routingMap = OsmHelper.compressForRouting(map);
		writeToDatabase(routingMap);		
	}
	
	
	
	public static void getBuckets(MemoryDataSet map) throws Exception {
		final double bucketSize_lat = 0.008;
		final double bucketSize_lon = 0.008;
		
		Buckets buckets = new Buckets(map, bucketSize_lat, bucketSize_lon);
		
		Iterator<Node> nodes_itr = map.getNodes(null);
		while (nodes_itr.hasNext()) {			
			buckets.add(nodes_itr.next());			
		}
		
		Iterator<Way> ways_itr = map.getWays(Bounds.WORLD);
		while (ways_itr.hasNext()) {
			buckets.add(ways_itr.next());
		}
		
		List<Bucket> bucket_collection = buckets.getBuckets();
		Collections.sort(bucket_collection);
		Iterator<Bucket> bucket_itr = bucket_collection.iterator();
		
		int numberOfNodes = 0;
		int numberOfWays = 0;
		
		int maxNodes = 0;
		int maxWays = 0;
		
		while (bucket_itr.hasNext()) {
			Bucket bucket = bucket_itr.next();
			System.out.println(bucket.toString());
			numberOfNodes += bucket.getNumberOfNodes();
			numberOfWays += bucket.getNumberOfWays();
			
			if (bucket.getNumberOfNodes() > maxNodes)
				maxNodes = bucket.getNumberOfNodes();
			if (bucket.getNumberOfWays() > maxWays)
				maxWays = bucket.getNumberOfWays();
		}
		
		System.out.println("# buckets = " + bucket_collection.size());
		System.out.println("# nodes = " + numberOfNodes);
		System.out.println("# ways = " + numberOfWays);
		System.out.println("max # nodes = " + maxNodes);
		System.out.println("max # ways = " + maxWays);
		
		buckets.drawBucketMatrix(System.out);
	}
	
	
	public static void execSQL(String sql, Statement statement) throws SQLException {
		if (statement != null) {
			try {
				statement.executeUpdate(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.toString());
				System.out.println(sql);
			}	
		} else
			System.out.println(sql);
	}
		
	
	public static String escape(String input) {
		return input.replaceAll("'", "''");		
	}
	
	/*
	public static String tagPacker(Collection<Tag> tags) {
		return OsmHelper.packTagsToString(tags);
	}
	*/

	/*
	public static Collection<Tag> tagUnpacker(String tags) {
		return OsmHelper.unpackStringToTags(tags);
	}
	*/
	
	
	public static boolean isEdge(Way way) {
		return false;
	}
	
}
