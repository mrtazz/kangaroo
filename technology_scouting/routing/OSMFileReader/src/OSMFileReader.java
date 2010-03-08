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
import java.util.Date;
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
import com.mobiletsm.osm.MobileTSMDatabaseWriter;
import com.mobiletsm.osm.OsmHelper;
import com.mobiletsm.osm.data.MobileDataSet;
import com.mobiletsm.osm.data.providers.DatabaseMDSProvider;
import com.mobiletsm.osm.data.providers.MobileDataSetProvider;
import com.mobiletsm.osm.data.searching.CombinedSelector;
import com.mobiletsm.osm.data.searching.POICode;
import com.mobiletsm.osm.data.searching.POINodeSelector;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;
import com.mobiletsm.routing.AllStreetVehicle;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.metrics.MobileRoutingMetric;
import com.mobiletsm.routing.routers.MobileMultiTargetDijkstraRouter;



public class OSMFileReader {
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {	
		
		// load map file
		File mapFile = new File("/Users/andreaswalz/Downloads/maps/in/map-fr.osm");		
		IDataSet map = (new FileLoader(mapFile)).parseOsm();
		System.out.println("FileLoader: output: # nodes = " + OsmHelper.getNumberOfNodes(map));
		System.out.println("FileLoader: output: # ways = " + OsmHelper.getNumberOfWays(map));	
				
		
		// write map to a mobile database		
		MobileTSMDatabaseWriter writer = 
			new MobileTSMDatabaseWriter("jdbc:sqlite:/Users/andreaswalz/Downloads/maps/out/map-fr.db");
		writer.setLogStream(System.out);		
		writer.openDatabase();
		writer.writeDatabaseV2(map);
		//writer.readDatabaseV2();		
		System.out.print("closing database...");
		if (writer.closeDatabase()) {
			System.out.println("successful!");
		} else {
			System.out.println("failed!");
		}
		map = null;
		
					
		/*
		IVehicle vehicle = new AllStreetVehicle();
		MobileDataSetProvider provider = new DatabaseMDSProvider(new MDSSQLiteDatabaseAdapter());		
		provider.open("jdbc:sqlite:/Users/andreaswalz/Downloads/map.db");	
		
		
		POICode poiCode = new POICode(POICode.AMENITY_BANK);
		Place home = new Place(48.1208603, 7.8581893); 
		
		Place poiPlace = provider.getNearestPOINode(home, new POINodeSelector(poiCode), null);
		
		if (poiPlace != null) {
			System.out.println("Place = " + poiPlace.toString());
			System.out.println("dist = " + home.distanceTo(poiPlace) + " Meter");
		} else {
			System.out.println("did not find any POI node of type " + poiCode.getType());
		}
		
		provider.close();
		*/
		
		/*
		// check routing
		long fromNodeId = 469369005;
		long toNodeId = 251508961;

		MobileDataSet routingMap = provider.getRoutingDataSet(fromNodeId, toNodeId, null);			
		
		System.out.println("provider.getRoutingDataSet: # nodes = " + routingMap.getNodesCount());
		System.out.println("provider.getRoutingDataSet: # ways  = " + routingMap.getWaysCount());		
		
		//check(routingMap, 469369005, 251508961, 1307.8384132660117);
		
		
		IRouter router = new MobileMultiTargetDijkstraRouter();
		router.setMetric(new MobileRoutingMetric());
		Node fromNode = routingMap.getNodeByID(fromNodeId);
		Node toNode = routingMap.getNodeByID(toNodeId);	
		
		Route route = router.route(routingMap, toNode, fromNode, vehicle);
		
		OsmHelper.followRouteOnMap(routingMap, route, vehicle, System.out);
		OsmHelper.followRouteOnMap(map, route, vehicle, System.out);
		
		provider.close();			
		*/
		
	}
	
	
	
	private static void check(IDataSet dataSet, long fromId, long toId, double dist) {
		IRouter router = new MobileMultiTargetDijkstraRouter();
		router.setMetric(new MobileRoutingMetric());
		Route route = router.route(dataSet, dataSet.getNodeByID(toId), 
				dataSet.getNodeByID(fromId), new AllStreetVehicle());
		double myDist = OsmHelper.getRouteLength(route);
		System.out.println("check: myDist = " + myDist + ", (external dist = " + dist + ")");
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




/* STUFF:


		// test amenity selector
		Amenity foo = new Amenity(Amenity.SCHOOL);
		System.out.println("id = " + foo.getId() + ", type = " + foo.getType());		
		AmenityPOINodeSelector sel1 = new AmenityPOINodeSelector();
		AmenityPOINodeSelector sel2 = new AmenityPOINodeSelector(foo);		
		Collection<Tag> tags = new Vector<Tag>();
		tags.add(new Tag("amenity", Amenity.SCHOOL));
		Node node = new Node(0, 0, (Date)null, null, 0, tags, 0, 0);		
		System.out.println("sel1.isAllowed() = " + sel1.isAllowed(null, node));
		System.out.println("sel2.isAllowed() = " + sel2.isAllowed(null, node));
		
		
		
		// compare routing on two maps
		IDataSet routingMap = OsmHelper.simplifyDataSet(map, new AllStreetVehicle());
		OsmHelper.compareRouting(map, routingMap, new AllStreetVehicle(), System.out);
		

		//<bounds minlat="48.032" minlon="7.784" maxlat="48.147" maxlon="8.011"/>
		double minLat = 48.032;
		double maxLat = 48.147;
		double minLon = 7.784;
		double maxLon = 8.011;
		
		double a1 = Place.distance(minLat, minLon, minLat, maxLon);
		double a2 = Place.distance(maxLat, minLon, maxLat, maxLon);
		double b = Place.distance(minLat, minLon, maxLat, minLon);
		
		System.out.println("a1 = " + a1);
		System.out.println("a2 = " + a2);
		System.out.println("b = " + b);
		
		System.out.println("area = " + (a1*b)/1000000 + " km^2");



*/
