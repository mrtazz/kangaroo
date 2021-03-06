package com.mobiletsm.osm.data.providers;

import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.travelingsalesman.routing.IVehicle;

import com.mobiletsm.osm.data.MobileInterfaceDataSet;
import com.mobiletsm.osm.data.adapters.RoutingDataAdapter;
import com.mobiletsm.osm.data.searching.POINodeSelector;
import com.mobiletsm.routing.GeoConstraints;
import com.mobiletsm.routing.Place;

/**
 * This class can be considered as the interface between the database containing
 * the openstreetmap data optimized for mobile purposes and the routing framework
 * of traveling salesman. The traveling salesman framework is designed to operate 
 * on a single data set which will thus not be optimized for special operations. 
 * To overcome this confinement and to meet the needs of a mobile application while
 * maintaining the compatibility to the traveling salesman framework, this class
 * is used to get specific data sets for every operation to be performed on the 
 * data in the database.
 *     
 * The connection to the database is abstracted in the MDSDatabaseAdapter class. 
 *      
 *     
 * @author andreaswalz
 *
 */
public abstract class MobileDataSetProvider {

	/**
	 * 
	 */
	protected RoutingDataAdapter adapter;
	
	
	/**
	 * create an instance of MobileDataSetProvider using an specific RoutingDataAdapter
	 * @param adapter
	 */
	public MobileDataSetProvider(RoutingDataAdapter adapter) {
		super();
		this.adapter = adapter;
	}
	
	
	/**
	 * returns a short description of the MobileDataSetProvider
	 * @return
	 */
	public String getInfo() {
		return "MobileDataSetProvider";
	}
	
	
	/* methods to be implemented by MobileDataSetProviders */
	
	public abstract boolean open(String source);
	
	
	public abstract boolean isOpen();
	
	
	public abstract void close();
	
	
	public abstract Place getNearestStreetNode(Place center);
	

	public abstract Place getNearestStreetNode(Place center, boolean updateCenter);
	
	
	public abstract Place getNearestPOINode(Place center, POINodeSelector selector, GeoConstraints limits);
	
	
	public abstract MobileInterfaceDataSet getCompleteDataSet();
	
	
	public abstract MobileInterfaceDataSet getRoutingDataSet(long fromNodeId, long toNodeId, IVehicle vehicle);
	
	
	public abstract MobileInterfaceDataSet updateRoutingDataSet(long fromNodeId, long toNodeId, IVehicle vehicle);
	
	
	public abstract MobileInterfaceDataSet getPOINodeDataSet(LatLon center, POINodeSelector selector);
	
	
	public abstract MobileInterfaceDataSet updatePOINodeDataSet(LatLon center, POINodeSelector selector);
	
	
}
