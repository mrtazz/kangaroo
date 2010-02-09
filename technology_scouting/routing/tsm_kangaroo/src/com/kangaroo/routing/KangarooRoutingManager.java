package com.kangaroo.routing;

import java.net.URI;

import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.searching.NearestStreetSelector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import com.kangaroo.statuschange.StatusChange;
import com.kangaroo.statuschange.StatusListener;
import com.kangaroo.statuschange.JobDoneStatusChange;
import com.mobiletsm.routing.AllStreetVehicle;
import com.mobiletsm.routing.AllWayVehicle;
import com.mobiletsm.routing.Vehicle;

import android.content.Context;
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
	private boolean allowLocationUpdates = false;
	
	
	/**
	 * 
	 */
	private Place home = new Place(48.1216952, 7.8571635);
	
	
	/**
	 * 
	 */
	private Vehicle vehicle = new AllWayVehicle();
	
	
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
	private URI routingDataSource = null;
	
	
	/**
	 * 
	 */
	private KangarooRoutingEngine routingEngine = null;
	
	
	/**
	 * 
	 */
	private StatusListener routingEngineStatusListener = new StatusListener() {
		@Override
		public void onStatusChanged(StatusChange status) {
			publishStatus(status);
			allowLocationUpdates = !status.busy;
		}		
	};
	
	
	/**
	 * 
	 */
	private StatusListener statusListener = null;
	
	
	/**
	 * 
	 * @return
	 */
	public LocationListener getLocationListener() {
		return locationListener;
	}
	
	
	/**
	 * @throws Exception 
	 * 
	 */
	public void init() throws Exception {
		if (routingEngine != null && routingEngine.initialized())
			throw new Exception("already initialized.");
		if (routingDataSource == null)
			throw new Exception("no routingDataSource specified.");
		
		routingEngine = new MobileRoutingEngine();
		routingEngine.setDataSource(routingDataSource);
		routingEngine.setStatusListener(routingEngineStatusListener);	
		routingEngine.init();
	}
	
	
	/**
	 * 
	 * @param location
	 */
	private void onLocationChanged(Location location) {
		if (allowLocationUpdates) {
			routingEngine.routeFromTo(new Place(location.getLatitude(), 
					location.getLongitude()), home, vehicle);
		}
	}
	
	
	/**
	 * 
	 * @param status
	 */
	private void publishStatus(StatusChange status) {
		if (statusListener != null) {
			status.message = routingEngine.getJobMessage(status.jobID);
			statusListener.onStatusChanged(status);
		}
	}
	
	/*
	public void routeFromTo(Place start, Place destination, Vehicle vehicle) throws Exception {
		routingEngine.routeFromTo(start, destination, vehicle);
	}
	*/
	
	/**
	 * 
	 * @param dataSource
	 */
	public void setRoutingDataSource(URI dataSource) {
		routingDataSource = dataSource;
	}
	
	
	/**
	 * 
	 * @param listener
	 */
	public void setStatusListener(StatusListener listener) {
		statusListener = listener;
	}
		
	
	/**
	 * shutdown the routing manager including its routing engine
	 */
	public void shutdown() {
		if (routingEngine != null)
			routingEngine.shutdown();
		statusListener = null;
		routingEngine = null;
	}
	
	
	
}
