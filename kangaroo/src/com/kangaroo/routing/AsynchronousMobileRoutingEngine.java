package com.kangaroo.routing;

import java.io.File;
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
import org.openstreetmap.travelingsalesman.routing.routers.MultiTargetDijkstraRouter;

import com.mobiletsm.osm.data.MobileInterfaceDataSet;
import com.mobiletsm.osm.data.adapters.MDSAndroidDatabaseAdapter;
import com.mobiletsm.osm.data.providers.DatabaseMDSProvider;
import com.mobiletsm.osm.data.providers.MobileDataSetProvider;
import com.mobiletsm.osm.data.searching.POINodeSelector;
import com.mobiletsm.routing.Limits;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.Vehicle;
import com.mobiletsm.routing.statuschange.JobDoneStatusChange;
import com.mobiletsm.routing.statuschange.JobFailedStatusChange;
import com.mobiletsm.routing.statuschange.JobStartedStatusChange;
import com.mobiletsm.routing.statuschange.StatusChange;
import com.mobiletsm.routing.statuschange.StatusListener;
import com.mobiletsm.routing.statuschange.SubJobDoneStatusChange;
import com.mobiletsm.routing.statuschange.SubJobStartedStatusChange;

public class AsynchronousMobileRoutingEngine extends AsynchronousRoutingEngine {
	
	
	/**
	 * 
	 */
	private MobileDataSetProvider dsProvider = null;
	
	
	/**
	 * 
	 */
	private StatusListener workingThreadStatusListener = new StatusListener() {
		@Override
		public void onStatusChanged(StatusChange status) {
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
	
		
	
	@Override
	/**
	 * 
	 */
	public void getNearestPOINode(Place place, POINodeSelector selector, Limits limits) {		
		throw new UnsupportedOperationException("getNearestPOINode() not supported by MobileRoutingEngine");
	}
	
	
	/**
	 * 
	 * @author Andreas Walz
	 *
	 */
	private class RunnableRouter implements Runnable {
		private StatusListener listener;
		private MobileDataSetProvider provider;
		private Place from;
		private Place to;
		private Vehicle vehicle;		
		
		public RunnableRouter(StatusListener listener, MobileDataSetProvider provider, 
				Place from, Place to, Vehicle vehicle) {
			super();
			this.listener = listener;
			this.provider = provider;
			this.from = from;
			this.to = to;
			this.vehicle = vehicle;
		}

		@Override
		public void run() {
			listener.onStatusChanged(new JobStartedStatusChange(JOBID_ROUTE_FROMTO));
			synchronized(provider) {
				try {
					long fromNodeId;
					long toNodeId;
					
					/*  */
					listener.onStatusChanged(new SubJobStartedStatusChange(JOBID_GET_NEAREST_STREET_NODE));
					fromNodeId = provider.getNearestStreetNode(
							new LatLon(from.getLatitude(), from.getLongitude())).getId();
					listener.onStatusChanged(new SubJobDoneStatusChange(JOBID_GET_NEAREST_STREET_NODE));
										
					/*  */				
					listener.onStatusChanged(new SubJobStartedStatusChange(JOBID_GET_NEAREST_STREET_NODE));
					toNodeId = provider.getNearestStreetNode(
							new LatLon(to.getLatitude(), to.getLongitude())).getId();
					listener.onStatusChanged(new SubJobDoneStatusChange(JOBID_GET_NEAREST_STREET_NODE));

					listener.onStatusChanged(new SubJobStartedStatusChange(JOBID_CREATE_DATASET));
					MobileInterfaceDataSet routingDataSet = provider.getRoutingDataSet(fromNodeId, toNodeId, null);
					listener.onStatusChanged(new SubJobDoneStatusChange(JOBID_CREATE_DATASET));
					
					IRouter router = new MultiTargetDijkstraRouter();
					Route route = router.route(routingDataSet, routingDataSet.getNodeByID(toNodeId), 
							routingDataSet.getNodeByID(fromNodeId), vehicle);						
						
					listener.onStatusChanged(new JobDoneStatusChange(JOBID_ROUTE_FROMTO, route));
				} catch (Exception exception) {
					listener.onStatusChanged(new JobFailedStatusChange(JOBID_ROUTE_FROMTO, exception));
					
				}
			}	
			
		}
		
	}
	
	
	@Override
	/**
	 * 
	 */
	public void routeFromTo(Place from, Place to, Vehicle vehicle) {		
		RunnableRouter job = new RunnableRouter(workingThreadStatusListener, dsProvider, from, to, vehicle);
		Thread worker = new Thread(job);
		worker.setName("MobileRoutingEngine.routeFromTo()");
		worker.start();
	}
	
	
	/**
	 * 
	 * @author Andreas Walz
	 *
	 */
	private class RunnableInitializer implements Runnable {
		private StatusListener listener;
		private MobileDataSetProvider provider;
		private URI source;
		
		public RunnableInitializer(StatusListener listener, MobileDataSetProvider provider, URI source) {
			super();
			this.listener = listener;
			this.provider = provider;
			this.source = source;
		}

		@Override
		public void run() {
			listener.onStatusChanged(new JobStartedStatusChange(JOBID_INIT_ROUTING_ENGINE));
			synchronized (provider) {
				try {
					provider.open(source.getPath(), new MDSAndroidDatabaseAdapter());
					listener.onStatusChanged(new JobDoneStatusChange(JOBID_INIT_ROUTING_ENGINE));
				} catch (Exception exception) {
					listener.onStatusChanged(new JobFailedStatusChange(JOBID_INIT_ROUTING_ENGINE, exception));
				}
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
			throw new RuntimeException("already initialized.");
		if (dataSource.getScheme() == null || !dataSource.getScheme().startsWith("file")) 
			throw new RuntimeException("scheme for data source not supported.");
		
		dsProvider = new DatabaseMDSProvider();
		RunnableInitializer job = new RunnableInitializer(workingThreadStatusListener, dsProvider, dataSource);
		Thread worker = new Thread(job);
		worker.setName("MobileRoutingEngine.init()");
		worker.start();
		return true;
	}
	

	@Override
	/**
	 * returns true if the routing engine is initialized
	 * and ready to accept routing jobs
	 * @return
	 */
	public boolean initialized() {
		return (dsProvider != null);
	}
	
	
	@Override
	public String getInfo() {
		return super.getInfo() + " MobileRoutingEngine:";
	}


	@Override
	public void shutdown() {
		/* release reference to the listener and thus allow
		 * the garbage collector to collect the listener object
		 */
		statusListener = null;
		
		synchronized (dsProvider) {
			dsProvider.close();
		}
	}



	@Override
	public void getNearestStreetNode(Place center) {
		// TODO Auto-generated method stub
		
	}
	
	
	private class RunnableGetNearestStreetNode implements Runnable {
		private StatusListener listener;
		private MobileDataSetProvider provider;
		private Place center;
		
		public RunnableGetNearestStreetNode(StatusListener listener, MobileDataSetProvider provider, Place center) {
			super();
			this.listener = listener;
			this.provider = provider;
			this.center = center;
		}
		
		@Override
		public void run() {
			listener.onStatusChanged(new JobStartedStatusChange(JOBID_GET_NEAREST_STREET_NODE));
			synchronized (provider) {
				try {
					Long nodeId = provider.getNearestStreetNode(
							new LatLon(center.getLatitude(), center.getLongitude())).getId();
					listener.onStatusChanged(new JobDoneStatusChange(JOBID_GET_NEAREST_STREET_NODE, nodeId));
				} catch (Exception exception) {
					listener.onStatusChanged(new JobFailedStatusChange(JOBID_GET_NEAREST_STREET_NODE, exception));
				}
			}
		}
		
	}




}
