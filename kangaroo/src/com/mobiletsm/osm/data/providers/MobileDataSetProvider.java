package com.mobiletsm.osm.data.providers;

import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.travelingsalesman.routing.IVehicle;

import com.mobiletsm.osm.data.MobileInterfaceDataSet;
import com.mobiletsm.osm.data.adapters.MDSDatabaseAdapter;
import com.mobiletsm.osm.data.searching.POINodeSelector;


public abstract class MobileDataSetProvider {

	
	protected MDSDatabaseAdapter adapter;
	
	
	public MobileDataSetProvider(MDSDatabaseAdapter adapter) {
		super();
		this.adapter = adapter;
	}
	
	
	public abstract boolean open(String source);
	
	
	public abstract boolean isOpen();
	
	
	public abstract void close();

	
	public abstract boolean isStreetNode(long nodeId);
	
	
	public abstract Node getNearestStreetNode(LatLon center);
	

	public abstract MobileInterfaceDataSet getCompleteDataSet();
	
	
	public abstract MobileInterfaceDataSet getRoutingDataSet(long fromNodeId, long toNodeId, IVehicle vehicle);
	
	
	public abstract MobileInterfaceDataSet updateRoutingDataSet(long fromNodeId, long toNodeId, IVehicle vehicle);
	
	
	public abstract MobileInterfaceDataSet getPOINodeDataSet(LatLon center, POINodeSelector selector);
	
	
	public abstract MobileInterfaceDataSet updatePOINodeDataSet(LatLon center, POINodeSelector selector);
	
	
}
