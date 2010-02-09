package com.mobiletsm.osm.data.providers;

import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.travelingsalesman.routing.IVehicle;

import com.mobiletsm.osm.data.MobileInterfaceDataSet;
import com.mobiletsm.osm.data.adapters.MDSDatabaseAdapter;
import com.mobiletsm.osm.data.searching.POINodeSelector;

public interface MobileDataSetProvider {

	
	public boolean open(String source, MDSDatabaseAdapter adapter);
	
	
	/**
	 * close connection to data source
	 */
	public void close();
	
	
	//public Node getNodeById(long id);

	
	public boolean isStreetNode(long nodeId);
	
	
	public Node getNearestStreetNode(LatLon center);
	

	public MobileInterfaceDataSet getCompleteDataSet();
	
	
	public MobileInterfaceDataSet getRoutingDataSet(long fromNodeId, long toNodeId, IVehicle vehicle);
	
	
	public MobileInterfaceDataSet updateRoutingDataSet(long fromNodeId, long toNodeId, IVehicle vehicle);
	
	
	public MobileInterfaceDataSet getPOINodeDataSet(LatLon center, POINodeSelector selector);
	
	
	public MobileInterfaceDataSet updatePOINodeDataSet(LatLon center, POINodeSelector selector);
	
	
}
