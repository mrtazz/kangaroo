import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
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
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.travelingsalesman.routing.IRouter;
import org.openstreetmap.travelingsalesman.routing.IVehicle;
import org.openstreetmap.travelingsalesman.routing.Route;
import org.openstreetmap.travelingsalesman.routing.routers.MultiTargetDijkstraRouter;

import com.kangaroo.tsm.osm.io.FileLoader;
import com.mobiletsm.osm.OsmHelper;
import com.mobiletsm.osm.data.MobileDataSet;
import com.mobiletsm.osm.data.adapters.MDSSQLiteDatabaseAdapter;
import com.mobiletsm.osm.data.providers.DatabaseMDSProvider;
import com.mobiletsm.osm.data.providers.MobileDataSetProvider;
import com.mobiletsm.osm.data.searching.AmenityPOINodeSelector;
import com.mobiletsm.osm.data.searching.CombinedSelector;
import com.mobiletsm.osm.data.searching.POINodeSelector;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;
import com.mobiletsm.routing.AllStreetVehicle;



public class OSMFileReader {
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		/*
		// load map file
		File mapFile = new File("/Users/andreaswalz/Downloads/map.osm");		
		IDataSet map = (new FileLoader(mapFile)).parseOsm();		
		System.out.println("FileLoader: output: # nodes = " + OsmHelper.getNumberOfNodes(map));
		System.out.println("FileLoader: output: # ways = " + OsmHelper.getNumberOfWays(map));	
		*/
		
		
		/*
		// write map to a mobile database
		OsmHelper.writeToMobileDatabase(map, "jdbc:sqlite:/Users/andreaswalz/Downloads/map.db");
		*/
		
		
		/*
		// compare routing on two maps
		IDataSet routingMap = OsmHelper.simplifyDataSet(map, new AllStreetVehicle());
		OsmHelper.compareRouting(map, routingMap, new AllStreetVehicle(), System.out);
		*/
		
		
		
		
		IVehicle vehicle = new AllStreetVehicle();

		MobileDataSetProvider provider = new DatabaseMDSProvider(new MDSSQLiteDatabaseAdapter());		
		provider.open("jdbc:sqlite:/Users/andreaswalz/Downloads/map.db");	
		
		
		/*
		// check routing
		long fromNodeId = 251508943;
		long toNodeId = 251509130;

		MobileDataSet routingMap = provider.getRoutingDataSet(fromNodeId, toNodeId, null);			
		
		System.out.println("# nodes = " + routingMap.getNodesCount());
		System.out.println("# ways  = " + routingMap.getWaysCount());
		
		IRouter router = new MultiTargetDijkstraRouter();
		Node fromNode = routingMap.getNodeByID(fromNodeId);
		Node toNode = routingMap.getNodeByID(toNodeId);	
		
		Route route = router.route(routingMap, toNode, fromNode, vehicle);
		OsmHelper.followRouteOnMap(routingMap, route, vehicle, System.out);
		
		System.out.println("OsmHelper.getRouteLengthOnMap(routingMap, route) = " + OsmHelper.getRouteLengthOnMap(routingMap, route) + "m");
		System.out.println("OsmHelper.getRoute(route) = " + OsmHelper.getRouteLength(route) + "m");
		*/
		
		provider.close();	
		
		
		
		/*
		Way way = routingMap.getWaysByID(20195279);
		System.out.println(OsmHelper.packTagsToString(way.getTags()));
		System.out.println(OsmHelper.packWayNodesToString(way));
		*/
		
		
	}
	
	
	
	
	public static void getBuckets(IDataSet map) throws Exception {
		final double bucketSize_lat = 0.1;
		final double bucketSize_lon = 0.1;
		
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
		
		//buckets.drawBucketMatrix(System.out);
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
	
}
