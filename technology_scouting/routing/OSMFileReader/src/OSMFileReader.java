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
import com.mobiletsm.osm.OsmHelper;
import com.mobiletsm.osm.data.MobileDataSet;
import com.mobiletsm.osm.data.adapters.MDSSQLiteDatabaseAdapter;
import com.mobiletsm.osm.data.providers.DatabaseMDSProvider;
import com.mobiletsm.osm.data.providers.MobileDataSetProvider;
import com.mobiletsm.osm.data.searching.Amenity;
import com.mobiletsm.osm.data.searching.AmenityPOINodeSelector;
import com.mobiletsm.osm.data.searching.CombinedSelector;
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
		File mapFile = new File("/Users/andreaswalz/Downloads/map.osm");		
		IDataSet map = (new FileLoader(mapFile)).parseOsm();
		System.out.println("FileLoader: output: # nodes = " + OsmHelper.getNumberOfNodes(map));
		System.out.println("FileLoader: output: # ways = " + OsmHelper.getNumberOfWays(map));	
			
		OsmHelper.printTagHighscore(map);
		
		/*
		check(map, 251509287, 251508961, 120.49640487214027);
		check(map, 251509130, 251508961, 331.4961956154394);
		check(map, 497272644, 251508961, 325.32961429340145);
		check(map, 256221938, 251508961, 364.94431352492535);
		check(map, 497272641, 251508961, 381.21675836061905);
		check(map, 497272643, 251508961, 413.2603406193887);
		check(map, 256221926, 251508961, 430.72580823124946);
		check(map, 251509218, 251508961, 517.62);
		check(map, 251345242, 251508961, 670.0026857361946);
		check(map, 251345241, 251508961, 687.5500000000001);
		check(map, 469369005, 251508961, 733.8784132660115);
		check(map, 311469703, 251508961, 1111.588165339088);
		check(map, 311486350, 251508961, 1062.8265957872009);
		check(map, 472945111, 251508961, 1227.365644377827);
		check(map, 472945238, 251508961, 1297.1111762650655);
		check(map, 472945179, 251508961, 1311.7721145377773);
		check(map, 472945172, 251508961, 1388.3932927796754);
		check(map, 183842750, 251508961, 1464.6121577612607);
		check(map, 181732799, 251508961, 1636.7872967894943);
		check(map, 181732792, 251508961, 1606.1228723916768);
		check(map, 461773975, 251508961, 1603.5566761878056);
		check(map, 181732907, 251508961, 1662.1996472615235);
		*/
		
		/*
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
		
		
		
		/*
		// write map to a mobile database
		OsmHelper.writeToMobileDatabase(map, "jdbc:sqlite:/Users/andreaswalz/Downloads/map.db");
		map = null;
		*/		
		
		/*
		// compare routing on two maps
		IDataSet routingMap = OsmHelper.simplifyDataSet(map, new AllStreetVehicle());
		OsmHelper.compareRouting(map, routingMap, new AllStreetVehicle(), System.out);
		*/
			
		/*
		IVehicle vehicle = new AllStreetVehicle();
		MobileDataSetProvider provider = new DatabaseMDSProvider(new MDSSQLiteDatabaseAdapter());		
		provider.open("jdbc:sqlite:/Users/andreaswalz/Downloads/map.db");	
		*/
		
		/*
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
		
		
		/*
		Way way = routingMap.getWaysByID(20195279);
		System.out.println(OsmHelper.packTagsToString(way.getTags()));
		System.out.println(OsmHelper.packWayNodesToString(way));
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
