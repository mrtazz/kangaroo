/**
 * 
 */
package com.kangaroo.routing;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Iterator;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import com.kangaroo.tsm.osm.io.FileLoader;
import com.kangaroo.tsm.osm.io.KangarooTSMFileLoader;

import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author Andreas Walz
 *
 */
public class TSMKangarooRoutingEngine implements KangarooRoutingEngine {
	
	
	/**
	 * the data source where osm routing data is initially read from
	 */
	private URI dataSource = null;
	
	
	/**
	 * the dataset containing all osm routing data
	 */
	private IDataSet map = null;
	
	
	/**
	 * the status listener to inform about status changes
	 */
	private StatusListener routingEngineStatusListener = null;
	
	
	/**
	 * 
	 * @param status
	 */
	private void publishStatus(StatusChange status) {
		if (routingEngineStatusListener != null)
			routingEngineStatusListener.onStatusChanged(status);
	}
	
	
	/**
	 * 
	 * @param msg
	 * @param done
	 * @param id
	 */
	private void publishStatus(String msg, boolean done, int id) {
		if (routingEngineStatusListener != null)
			routingEngineStatusListener.onStatusChanged(new StatusChange(msg, done, id));
	}
	
	
	/**
	 * create routing engine and set data source uri
	 * @param aDataSource
	 */
	public TSMKangarooRoutingEngine(URI aDataSource) {
		super();
		setDataSource(aDataSource);
	}
	
	
	@Override
	public synchronized Place getNearestNode(Place place, Selector selector, Limits limits) {		
		if (map == null)
			return null;
		
		publishStatus(KangarooRoutingEngine.LOOKING_FOR_NEAREST_NODE_MSG, false, 
				KangarooRoutingEngine.LOOKING_FOR_NEAREST_NODE_ID);
		
		try {
			Node node = map.getNearestNode(new LatLon(place.getLatitude(), place.getLongitude()), selector);		
			//publishStatus(KangarooRoutingEngine.LOOKING_FOR_NEAREST_NODE_DONE, true, 
			//		KangarooRoutingEngine.LOOKING_FOR_NEAREST_NODE_ID);
			return new Place(node);	
		} catch (Exception e) {
			publishStatus(KangarooRoutingEngine.LOOKING_FOR_NEAREST_NODE_FAILED, true, 
					KangarooRoutingEngine.LOOKING_FOR_NEAREST_NODE_ID);
		}
		return null;
	}

	
	
	@Override
	public synchronized RouteParameter routeFromTo(Place start, Place destination, Vehicle vehicle) {
		return null;
	}
	
	
	@Override
	/**
	 * initialize the routing engine and load data from given data
	 * source
	 */
	public synchronized void init() {		
		/*
		if (dataSource.getScheme() == null || !dataSource.getScheme().startsWith("file")) 
			throw new Exception("URI scheme for data source not supported.");
	
		RunnableFileLoader fileLoader = new RunnableFileLoader(this, new File(dataSource.getPath()));
		fileLoader.setStatusListener(workingThreadStatusListener);		
		(new Thread(fileLoader)).start();
		*/
		
		publishStatus(KangarooRoutingEngine.INITIALIZING_ROUTING_ENGINE_MSG, false, 
				KangarooRoutingEngine.INITIALIZING_ROUTING_ENGINE_ID);
		
		try {
			map = (new KangarooTSMFileLoader(new File(dataSource.getPath()))).parseOsmKangarooTSM();
			publishStatus(KangarooRoutingEngine.INITIALIZING_ROUTING_ENGINE_DONE, true, 
					KangarooRoutingEngine.INITIALIZING_ROUTING_ENGINE_ID);
		} catch (Exception e) {
			publishStatus(KangarooRoutingEngine.INITIALIZING_ROUTING_ENGINE_FAILED, true, 
					KangarooRoutingEngine.INITIALIZING_ROUTING_ENGINE_ID);
		}	
	}
	
	
		
	@Override
	/**
	 * set data source URI
	 */
	public synchronized void setDataSource(URI aDataSource) {
		dataSource = aDataSource;		
	}


	@Override
	public synchronized String getInfo() {
		StringBuffer result = new StringBuffer();		
		result.append("Traveling Salesman (with Kangaroo modifications); ");		
		if (map != null) {
			result.append("#nodes =" + ((MemoryDataSet)map).getNodesCount());
		} else {
			result.append("no map file loaded");
		}		
		return result.toString();
	}


	@Override
	public synchronized void shutdown() {
		/* release reference to the listener and thus allow
		 * the garbage collector to collect the listener object
		 */
		routingEngineStatusListener = null;
	}


	@Override
	/**
	 * set the status listener that will be informed about changes in
	 * the routing engine's status
	 */
	public synchronized void setStatusListener(StatusListener listener) {
		routingEngineStatusListener = listener;
	}
	
	
	
	
	
	
	/* ------------------- */
	
	public synchronized Way getWayForNode(Node node) {
		publishStatus("getWayForNode", false, 42);
		Iterator<Way> ways = map.getWaysForNode(node.getId());
		publishStatus("getWayForNode", true, 42);
		if (ways.hasNext())
			return ways.next();
		else
			return null;
	}
	


}
