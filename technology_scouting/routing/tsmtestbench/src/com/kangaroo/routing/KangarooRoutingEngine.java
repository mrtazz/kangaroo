/**
 * 
 */
package com.kangaroo.routing;

import java.net.URI;

import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;

import com.kangaroo.statuschange.StatusListener;

import android.content.Context;
import android.location.LocationListener;

/**
 * This defines the interface between the routing 
 * engine and the rest of the application that makes
 * use of it. 
 * @author Andreas Walz
 *
 */
public interface KangarooRoutingEngine {
	
	/**
	 * 
	 */
	public final static int JOBID_INIT_ROUTING_ENGINE = 1;
	
	/**
	 * 
	 */
	public final static int JOBID_GET_NEAREST_NODE = 2;
	
	/**
	 * 
	 */
	public final static int JOBID_GET_WAYS_FOR_NODE = 3;
	
	/**
	 * 
	 */
	public final static int JOBID_ROUTE_FROMTO = 4;

	
	/**
	 * 
	 * @param jobID
	 */
	public String getJobMessage(int jobID);
	
	
	/**
	 * set the data source URI where data is initially read from
	 * @param aDataSource
	 * @throws Exception 
	 */
	public void setDataSource(URI aDataSource) throws Exception;
		
	
	/**
	 * set the status listener that will be informed about changes in
	 * the routing engine's status
	 * @param listener
	 */
	public void setStatusListener(StatusListener listener);
	
	
	/**
	 * Initialize the routing engine
	 * @throws Exception 
	 */
	public void init() throws Exception;
	
	
	/**
	 * returns true if the routing engine is initialized
	 * and ready to accept routing jobs
	 * @return
	 */
	public boolean initialized();
	
	
	/**
	 * stop the routing engine and release all used resources
	 * @throws Exception 
	 */
	public void shutdown();	
	
	
	/**
	 * 
	 * @param start
	 * @param destination
	 * @param vehicle
	 * @throws Exception 
	 */
	public void routeFromTo(Place start, Place destination, Vehicle vehicle) throws Exception;
	

	/**
	 * 
	 * @param position
	 * @param selector
	 * @param limits
	 * @throws Exception 
	 */
	public void getNearestNode(Place position, Selector selector, Limits limits) throws Exception;
		
	
	/**
	 * 
	 * @param node
	 * @throws Exception 
	 */
	public void getWaysForNode(Node node) throws Exception;
	
	
	/**
	 * return a string describing the routing engine and its data source
	 * @return
	 */
	public String getInfo();
	
	
	/**
	 * 
	 * @param context
	 */
	public void setContext(Context context);

}
