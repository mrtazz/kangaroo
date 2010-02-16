/**
 * 
 */
package com.kangaroo.techscout.routing;

import java.net.URI;

import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;

import com.mobiletsm.osm.data.searching.POINodeSelector;
import com.mobiletsm.routing.Limits;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.Vehicle;
import com.mobiletsm.routing.statuschange.StatusListener;


/**
 * This defines the interface between the routing 
 * engine and the rest of the application that makes
 * use of it. 
 * @author Andreas Walz
 *
 */
public abstract class AsynchronousRoutingEngine {
	
	
	protected URI dataSource = null;
	
	
	protected StatusListener statusListener = null;
	
	
	/**
	 * 
	 */
	public final static int JOBID_INIT_ROUTING_ENGINE = 1;
	
	/**
	 * 
	 */
	public final static int JOBID_GET_NEAREST_POI_NODE = 2;
		
	/**
	 * 
	 */
	public final static int JOBID_GET_NEAREST_STREET_NODE = 3;
	
	/**
	 * 
	 */
	public final static int JOBID_ROUTE_FROMTO = 4;	
	
	/**
	 * 
	 */
	public final static int JOBID_CREATE_DATASET = 5;
	
	
	
	/* methods to be implemented by KangarooRoutingEngines */
	
	/**
	 * Initialize the routing engine
	 * @throws Exception 
	 */
	public abstract boolean init();
	
	
	/**
	 * returns true if the routing engine is initialized
	 * and ready to accept routing jobs
	 * @return
	 */
	public abstract boolean initialized();
	
	
	/**
	 * stop the routing engine and release all used resources
	 * @throws Exception 
	 */
	public abstract void shutdown();	
		
	
	public abstract void routeFromTo(Place from, Place to, Vehicle vehicle);
	
	
	public abstract void getNearestPOINode(Place center, POINodeSelector selector, Limits limits);
	
	
	public abstract void getNearestStreetNode(Place center);
	
	
	
	/* methods that my be reimplemented by KangarooRoutingEngines */
	
	/**
	 * 
	 * @param jobID
	 */
	public String getJobMessage(int jobID) {
		if (jobID == JOBID_INIT_ROUTING_ENGINE) {
			return "initializing routing engine...";
		} else if (jobID == JOBID_GET_NEAREST_POI_NODE) {
			return "looking for points of interest...";
		} else if (jobID == JOBID_GET_NEAREST_STREET_NODE) {
			return "looking nearest street node...";
		} else if (jobID == JOBID_ROUTE_FROMTO) {
			return "routing...";
		} else if (jobID == JOBID_CREATE_DATASET) {
			return "creating data set...";
		} else {
			return "unknown job";
		}
	}
	
	
	/**
	 * set the data source URI where data is read from
	 * @param aDataSource
	 */
	public void setDataSource(URI aDataSource) {
		if (initialized()) {
			throw new RuntimeException("TSMKangarooRoutingEngine.setDataSource(): " +
					"cannot change data source after initialization.");
		} else {
			this.dataSource = aDataSource;
		}
	}
		
	
	/**
	 * set the status listener that will be informed about changes in
	 * the routing engine's status
	 * @param listener
	 */
	public void setStatusListener(StatusListener listener) {
		this.statusListener = listener;
	}
	
	
	
	
	/**
	 * return a string describing the routing engine and its data source
	 * @return
	 */
	public String getInfo() {
		return "KangarooRoutingEngine: ";
	}
	
	
}
