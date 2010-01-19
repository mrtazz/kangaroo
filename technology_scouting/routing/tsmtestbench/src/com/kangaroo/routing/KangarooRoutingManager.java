package com.kangaroo.routing;

import java.net.URI;

import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.searching.NearestStreetSelector;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class KangarooRoutingManager {


	/**
	 * 
	 */
	private KangarooRoutingEngine routingEngine = null;
	
	
	/**
	 * 
	 */
	private URI routingDataSource = null;
	
	
	/**
	 * 
	 */
	private StatusListener routingManagerStatusListener = null;
	
	
	/**
	 * 
	 * @param msg
	 * @param done
	 * @param id
	 */
	private void publishStatus(String msg, boolean done, int id) {
		if (routingManagerStatusListener != null)
			routingManagerStatusListener.onRoutingManagerStatusChanged(new StatusChange(msg, done, id));
	}
		
	
	/**
	 * 
	 * @param status
	 */
	private void publishStatus(StatusChange status) {
		if (routingManagerStatusListener != null)
			routingManagerStatusListener.onRoutingManagerStatusChanged(status);
	}
	
	
	/**
	 * 
	 */
	private boolean allowLocationUpdates = false;
	
	
	/**
	 * 
	 */
	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			Message msg = Message.obtain();
			msg.obj = location;
			locationUpdateHandler.sendMessage(msg);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
		
	};
	
	
	/**
	 * 
	 */
	private Handler locationUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			onLocationChanged((Location)msg.obj);
		}		
	};
	
	
	/**
	 * 
	 */
	private StatusListener routingEngineStatusListener = new StatusListener() {
		@Override
		public void onRoutingManagerStatusChanged(StatusChange status) {
			Message msg = Message.obtain();
			msg.obj = status;
			routingEngineStatusChangedHandler.sendMessage(msg);
		}		
	};
	
	
	/**
	 * 
	 */
	private Handler routingEngineStatusChangedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			onRoutingEngineStatusChanged((StatusChange)msg.obj);
		}		
	};
	
	
	/**
	 * 
	 */
	public KangarooRoutingManager() {
		
	}
	
	
	/**
	 * 
	 * @param listener
	 */
	public void setStatusListener(StatusListener listener) {
		routingManagerStatusListener = listener;
	}
		
	
	/**
	 * 
	 * @author Andreas Walz
	 *
	 */
	private class RunnableInitRoutingEngine implements Runnable {
		@Override
		public void run() {
			routingEngine.init();
		}		
	}
	
	
	/**
	 * 
	 * @author Andreas Walz
	 *
	 */
	private class RunnableGetNearestNode implements Runnable {
		
		private Place place;
		private Selector selector;
		private Limits limits;
		
		public RunnableGetNearestNode(Place place, Selector selector, Limits limits) {
			super();
			this.place = place;
			this.selector = selector;
			this.limits = limits;
		}

		@Override
		public void run() {
			Message msg = Message.obtain();
			msg.obj = routingEngine.getNearestNode(place, selector, limits);
			runnableGetNearestNodeHandler.sendMessage(msg);
		}		
	}
	
	
	/**
	 * 
	 */
	private Handler runnableGetNearestNodeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null)
				onRunnableGetNearestNodeDone((Place)msg.obj);
			else
				onRunnableGetNearestNodeDone(null);
		}
	};
	
	
	
	private void onRunnableGetNearestNodeDone(Place place) {
		publishStatus(place.getNode().toString(), true, KangarooRoutingEngine.LOOKING_FOR_NEAREST_NODE_ID);
		Way way = ((TSMKangarooRoutingEngine)routingEngine).getWayForNode(place.getNode());
		if (way != null)
			Log.v("MyTag", way.toString());
		allowLocationUpdates = true;
	}
	
	
	/**
	 * @throws Exception 
	 * 
	 */
	public void init() throws Exception {
		if (routingEngine != null)
			return;
		if (routingDataSource == null)
			throw new Exception("no routingDataSource specified.");
		
		routingEngine = new TSMKangarooRoutingEngine(routingDataSource);	
		routingEngine.setStatusListener(routingEngineStatusListener);		
	
		(new Thread(new RunnableInitRoutingEngine())).start();
	}
	
	
	/**
	 * shutdown the routing manager including its routing engine
	 */
	public void shutdown() {
		if (routingEngine != null)
			routingEngine.shutdown();
		routingManagerStatusListener = null;
		routingEngine = null;
	}
	
	
	/**
	 * 
	 * @param location
	 */
	private void onLocationChanged(Location location) {
		if (allowLocationUpdates) {
			(new Thread(new RunnableGetNearestNode(new Place(location), 
					new NearestStreetSelector(), null))).start();
		}
	}
	
	
	/**
	 * 
	 * @param status
	 */
	private void onRoutingEngineStatusChanged(StatusChange status) {
		allowLocationUpdates = status.operationFinished;
		publishStatus(status);	
	}
	
	
	/**
	 * 
	 * @param dataSource
	 */
	public void setRoutingDataSource(URI dataSource) {
		routingDataSource = dataSource;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public LocationListener getLocationListener() {
		return locationListener;
	}
	
	
	
	
	
}
