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
import org.openstreetmap.osm.data.searching.NearestStreetSelector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.travelingsalesman.routing.IRouter;
import org.openstreetmap.travelingsalesman.routing.Route;
import org.openstreetmap.travelingsalesman.routing.describers.SimpleRouteDescriber;
import org.openstreetmap.travelingsalesman.routing.routers.MultiTargetDijkstraRouter;
import org.openstreetmap.travelingsalesman.routing.routers.TurnRestrictedAStar;

import com.kangaroo.statuschange.JobDoneStatusChange;
import com.kangaroo.statuschange.JobFailedStatusChange;
import com.kangaroo.statuschange.JobStartedStatusChange;
import com.kangaroo.statuschange.StatusChange;
import com.kangaroo.statuschange.StatusListener;
import com.kangaroo.statuschange.SubJobDoneStatusChange;
import com.kangaroo.statuschange.SubJobStartedStatusChange;
import com.kangaroo.tsm.osm.data.KangarooTSMMemoryDataSet;
import com.kangaroo.tsm.osm.io.FileLoader;
import com.kangaroo.tsm.osm.io.KangarooTSMFileLoader;
import com.mobiletsm.osm.POINodeSelector;
import com.mobiletsm.routing.Vehicle;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author Andreas Walz
 *
 */
public class TSMKangarooRoutingEngine extends KangarooRoutingEngine {
	
	
	/**
	 * the dataset containing all openstreetmap routing data
	 */
	private IDataSet map = null;
	
	
	/**
	 * 
	 */
	private StatusListener workingThreadStatusListener = new StatusListener() {
		@Override
		public void onStatusChanged(StatusChange status) {
			if (status instanceof JobDoneStatusChange && status.jobID == KangarooRoutingEngine.JOBID_INIT_ROUTING_ENGINE)
				map = (IDataSet)status.result;
			publishStatus(status);
		}
	};
	
	
	/**
	 * 
	 * @param status
	 */
	private void publishStatus(StatusChange status) {
		if (statusListener != null)
			statusListener.onStatusChanged(status);
	}
	
	
	/**
	 * 
	 * @author Andreas Walz
	 *
	 */
	private class RunnableGetNearestNode implements Runnable {
		private StatusListener listener;
		private IDataSet map;
		private Place place;
		private Selector selector;
		private Limits limits;
		
		public RunnableGetNearestNode(StatusListener listener, IDataSet map,
				Place place, Selector selector, Limits limits) {
			super();
			this.listener = listener;
			this.map = map;
			this.place = place;
			this.selector = selector;
			this.limits = limits;
		}
		
		@Override
		public void run() {
			listener.onStatusChanged(new JobStartedStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_POI_NODE));
			try {
				Node node;
				synchronized(map) {
					node = map.getNearestNode(new LatLon(place.getLatitude(), place.getLongitude()), selector);
				}
				listener.onStatusChanged(new JobDoneStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_POI_NODE, node));
			} catch (Exception exception) {
				listener.onStatusChanged(new JobFailedStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_POI_NODE, exception));
			}					
		}		
	}
	
	
	@Override
	/**
	 * 
	 */
	public void getNearestPOINode(Place place, POINodeSelector selector, Limits limits) {		
		if (!initialized()) {
			throw new RuntimeException("TSMKangarooRoutingEngine.getNearestPOINode(): " +
					"routing engine not initialized");
		}		
		RunnableGetNearestNode job = new RunnableGetNearestNode(workingThreadStatusListener, map, place, selector, limits);
		Thread worker = new Thread(job);
		worker.setName("TSMKangarooRoutingEngine.getNearestPOINode()");
		worker.start();
	}
	
	
	/**
	 * 
	 * @author Andreas Walz
	 *
	 */
	private class RunnableRouter implements Runnable {
		private StatusListener listener;
		private IDataSet map;
		private Place start;
		private Place destination;
		private Vehicle vehicle;		
		
		public RunnableRouter(StatusListener listener, IDataSet map,
				Place start, Place destination, Vehicle vehicle) {
			super();
			this.listener = listener;
			this.map = map;
			this.start = start;
			this.destination = destination;
			this.vehicle = vehicle;
		}

		@Override
		public void run() {
			listener.onStatusChanged(new JobStartedStatusChange(KangarooRoutingEngine.JOBID_ROUTE_FROMTO));
			synchronized(map) {
				Node startNode;
				Node destinationNode;				
				try {
					if (start.isOsmNode()) {
						startNode = map.getNodeByID(start.getOsmNodeId());
					} else {
						listener.onStatusChanged(new SubJobStartedStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_POI_NODE));
						startNode = map.getNearestNode(new LatLon(start.getLatitude(), start.getLongitude()), 
								new NearestStreetSelector());
						listener.onStatusChanged(new SubJobDoneStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_POI_NODE, startNode));
					}

					if (destination.isOsmNode()) {
						destinationNode = map.getNodeByID(destination.getOsmNodeId());
					} else {
						listener.onStatusChanged(new SubJobStartedStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_POI_NODE));
						destinationNode = map.getNearestNode(new LatLon(destination.getLatitude(), destination.getLongitude()), 
								new NearestStreetSelector());
						listener.onStatusChanged(new SubJobDoneStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_POI_NODE));
					}
					
					IRouter router = new MultiTargetDijkstraRouter();
						Log.v("MyTag", "route from " + startNode.getId() + " to " + destinationNode.getId());
					
					Route route = router.route(map, startNode, destinationNode, vehicle);	
						if (route != null)
							Log.v("MyTag", "Route = " + route.toString());
						else
							Log.v("MyTag", "Route = null");
					listener.onStatusChanged(new JobDoneStatusChange(KangarooRoutingEngine.JOBID_ROUTE_FROMTO, route));
				} catch (Exception exception) {
					listener.onStatusChanged(new JobFailedStatusChange(KangarooRoutingEngine.JOBID_ROUTE_FROMTO, exception));
					
				}
			}	
			
		}
		
	}
	
	
	@Override
	/**
	 * 
	 */
	public void routeFromTo(Place start, Place destination, Vehicle vehicle) {
		if (!initialized()) {
			throw new RuntimeException("TSMKangarooRoutingEngine.routeFromTo(): " +
			"routing engine not initialized");
		}
		
		RunnableRouter job = new RunnableRouter(workingThreadStatusListener, map, start, destination, vehicle);
		Thread worker = new Thread(job);
		worker.setName("TSMKangarooRoutingEngine.routeFromTo()");
		worker.start();
	}
	
	
	/**
	 * 
	 * @author Andreas Walz
	 *
	 */
	private class RunnableFileLoader implements Runnable {
		private StatusListener listener;
		
		public RunnableFileLoader(StatusListener listener) {
			super();
			this.listener = listener;
		}

		@Override
		public void run() {
			listener.onStatusChanged(new JobStartedStatusChange(KangarooRoutingEngine.JOBID_INIT_ROUTING_ENGINE));
			try {				
				KangarooTSMFileLoader fileLoader = new KangarooTSMFileLoader(new File(dataSource.getPath())); 
				//IDataSet map = fileLoader.parseOsmKangarooTSM();
				IDataSet map = fileLoader.readOsmDatabase("/sdcard/map-em.db");
				((KangarooTSMMemoryDataSet)map).openDatabase("/sdcard/map-em.db");
				listener.onStatusChanged(new JobDoneStatusChange(KangarooRoutingEngine.JOBID_INIT_ROUTING_ENGINE, map));
			} catch (Exception exception) {
				listener.onStatusChanged(new JobFailedStatusChange(KangarooRoutingEngine.JOBID_INIT_ROUTING_ENGINE, exception));
			}			
		}		
	}
	
	
	@Override
	/**
	 * initialize the routing engine and load data from given data
	 * source
	 */
	public boolean init() {		
		if (initialized())
			return false;
		if (dataSource.getScheme() == null || !dataSource.getScheme().startsWith("file")) 
			return false;
				
		RunnableFileLoader job = new RunnableFileLoader(workingThreadStatusListener);
		Thread worker = new Thread(job);
		worker.setName("TSMKangarooRoutingEngine.init()");
		worker.start();
		return true;
	}
	

	/**
	 * returns true if the routing engine is initialized
	 * and ready to accept routing jobs
	 * @return
	 */
	public boolean initialized() {
		return (map != null);
	}
	
	
	@Override
	public String getInfo() {
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
	public void shutdown() {
		/* release reference to the listener and thus allow
		 * the garbage collector to collect the listener object
		 */
		statusListener = null;
		
		((KangarooTSMMemoryDataSet)map).closeDatabase();
	}


	@Override
	public void getNearestStreetNode(Place center) {
		// TODO Auto-generated method stub
		
	}


}
