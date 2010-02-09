package com.mobiletsm.osmosis.core.domain.v0_6;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;


public class MobileWayNode extends WayNode {

	/**
	 * 
	 */
	private double distanceToPredecessor = 0;
		
	
	/**
	 * 
	 */
	private Way way = null;
	
	
	/**
	 * 
	 * @param nodeId
	 */
	public MobileWayNode(long nodeId) {
		super(nodeId);
	}
	
	
	/**
	 * 
	 * @param nodeId
	 * @param distanceToPredecessor
	 */
	public MobileWayNode(long nodeId, double distanceToPredecessor) {
		super(nodeId);
		this.distanceToPredecessor = distanceToPredecessor;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getDistanceToPredecessor() {
		return distanceToPredecessor;
	}
	
	
	public void setDistanceToPredecessor(double dist) {
		this.distanceToPredecessor = dist;
	}
	
	
	public void setWay(Way way) {
		this.way = way;
	}
	
	
	public Way getWay() {
		return way;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WayNode)
			return this.getNodeId() == ((WayNode)obj).getNodeId();
		else
			return false;
	}
	
	
	

}
