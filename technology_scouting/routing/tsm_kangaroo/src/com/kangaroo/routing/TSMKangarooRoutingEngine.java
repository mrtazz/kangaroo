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

import com.kangaroo.statuschange.JobDoneStatusChange;
import com.kangaroo.statuschange.JobFailedStatusChange;
import com.kangaroo.statuschange.JobStartedStatusChange;
import com.kangaroo.statuschange.StatusChange;
import com.kangaroo.statuschange.StatusListener;
import com.kangaroo.statuschange.SubJobDoneStatusChange;
import com.kangaroo.statuschange.SubJobStartedStatusChange;
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
	 * the data source URI where the openstreetmap routing 
	 * data is initially read from
	 */
	private URI dataSource = null;
	
	
	@Override
	/**
	 * set data source URI
	 */
	public void setDataSource(URI aDataSource) throws Exception {
		if (map != null)
			throw new Exception("cannot change data source after initialization.");
		dataSource = aDataSource;		
	}
	
	
	/**
	 * the dataset containing all openstreetmap routing data
	 */
	private IDataSet map = null;
	
	
	/**
	 * the status listener to inform about status changes
	 * and job results
	 */
	private StatusListener statusListener = null;
	
	
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
	
	
	@Override
	/**
	 * set the status listener that will be informed about changes in
	 * the routing engine's status and job results
	 */
	public void setStatusListener(StatusListener listener) {
		statusListener = listener;
	}
	
	
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
			listener.onStatusChanged(new JobStartedStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_NODE));
			try {
				Node node;
				synchronized(map) {
					node = map.getNearestNode(new LatLon(place.getLatitude(), place.getLongitude()), selector);
				}
				listener.onStatusChanged(new JobDoneStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_NODE, node));
			} catch (Exception exception) {
				listener.onStatusChanged(new JobFailedStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_NODE, exception));
			}					
		}		
	}
	
	
	@Override
	/**
	 * 
	 */
	public void getNearestNode(Place place, Selector selector, Limits limits) throws Exception {		
		if (!initialized())
			throw new Exception("not initialized.");
		
		RunnableGetNearestNode job = new RunnableGetNearestNode(workingThreadStatusListener, map, place, selector, limits);
		Thread worker = new Thread(job);
		worker.setName("TSMKangarooRoutingEngine.getNearestNode()");
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
					if (start.isNode()) {
						startNode = start.getNode();
					} else {
						listener.onStatusChanged(new SubJobStartedStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_NODE));
						startNode = map.getNearestNode(new LatLon(start.getLatitude(), start.getLongitude()), 
								new NearestStreetSelector());
						listener.onStatusChanged(new SubJobDoneStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_NODE, startNode));
					}

					if (destination.isNode()) {
						destinationNode = destination.getNode();
					} else {
						listener.onStatusChanged(new SubJobStartedStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_NODE));
						destinationNode = map.getNearestNode(new LatLon(destination.getLatitude(), destination.getLongitude()), 
								new NearestStreetSelector());
						listener.onStatusChanged(new SubJobDoneStatusChange(KangarooRoutingEngine.JOBID_GET_NEAREST_NODE));
					}
					
					IRouter router = new MultiTargetDijkstraRouter();
					Route route = router.route(map, startNode, destinationNode, vehicle);	
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
	public void routeFromTo(Place start, Place destination, Vehicle vehicle) throws Exception {
		if (!initialized())
			throw new Exception("not initialized.");
		
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
				IDataSet map = (new KangarooTSMFileLoader(new File(dataSource.getPath()))).parseOsmKangarooTSM();
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
	public void init() throws Exception {		
		if (initialized())
			throw new Exception("already initialized.");
		if (dataSource.getScheme() == null || !dataSource.getScheme().startsWith("file")) 
			throw new Exception("scheme for data source not supported.");
		
		RunnableFileLoader job = new RunnableFileLoader(workingThreadStatusListener);
		Thread worker = new Thread(job);
		worker.setName("TSMKangarooRoutingEngine.init()");
		worker.start();
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
	}


	/**
	 * 
	 * @author Andreas Walz
	 *
	 */
	private class RunnableGetWaysForNode implements Runnable {
		private StatusListener listener;
		private IDataSet map;
		private Node node;
				
		public RunnableGetWaysForNode(StatusListener listener, IDataSet map,
				Node node) {
			super();
			this.listener = listener;
			this.map = map;
			this.node = node;
		}

		@Override
		public void run() {
			listener.onStatusChanged(new JobStartedStatusChange(KangarooRoutingEngine.JOBID_GET_WAYS_FOR_NODE));
			try {
				Iterator<Way> wayitr;
				synchronized(map) {
					wayitr = map.getWaysForNode(node.getId());
				}
				listener.onStatusChanged(new JobDoneStatusChange(KangarooRoutingEngine.JOBID_GET_WAYS_FOR_NODE, wayitr));
			} catch (Exception exception) {
				listener.onStatusChanged(new JobFailedStatusChange(KangarooRoutingEngine.JOBID_GET_WAYS_FOR_NODE, exception));
			}					
		}		
	}
	

	@Override
	/**
	 * 
	 */
	public void getWaysForNode(Node node) throws Exception {
		if (!initialized())
			throw new Exception("not initialized.");
		
		RunnableGetWaysForNode job = new RunnableGetWaysForNode(workingThreadStatusListener, map, node);
		Thread worker = new Thread(job);
		worker.setName("TSMKangarooRoutingEngine.getWaysForNode()");
		worker.start();
	}


	@Override
	public String getJobMessage(int jobID) {
		if (jobID == KangarooRoutingEngine.JOBID_INIT_ROUTING_ENGINE) {
			return "initializing routing engine...";
		} else if (jobID == KangarooRoutingEngine.JOBID_GET_NEAREST_NODE) {
			return "looking for nearest node...";
		} else if (jobID == KangarooRoutingEngine.JOBID_GET_WAYS_FOR_NODE) {
			return "looking for ways for node...";
		} else if (jobID == KangarooRoutingEngine.JOBID_ROUTE_FROMTO) {
			return "routing...";
		} else {
			return "unknown job";
		}
		
	}


}
