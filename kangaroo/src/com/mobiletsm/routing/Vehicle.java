/**
 * 
 */
package com.mobiletsm.routing;

import org.openstreetmap.osm.ConfigurationSection;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.travelingsalesman.routing.IVehicle;



/**
 * This class represents a type of vehicle that is used 
 * to move from place to place, e.g. a car, a bicycle or
 * simply the feet.
 * @author Andreas Walz
 *
 */
public abstract class Vehicle implements IVehicle {
	
	/**
	 * The maximum speed for this vehicle, specified in km/h
	 */
	protected double maxSpeed = 100;
	
	
	/**
	 * set the maximum speed for this vehicle
	 * @param	speed	maximum speed in km/h
	 */
	public void setMaxSpeed(double speed) {
		maxSpeed = speed;
	}
	
	
	/**
	 * get the maximum speed for this vehicle in km/h
	 */
	public double getMaxSpeed() {
		return maxSpeed;
	}
	

	/* methods to be implemented by a vehicle */
	
	public abstract boolean isAllowed(IDataSet arg0, Node arg1);

	public abstract boolean isAllowed(IDataSet arg0, Way arg1);

	public abstract boolean isOneway(IDataSet arg0, Way arg1);

	public abstract boolean isReverseOneway(IDataSet arg0, Way arg1);

	public abstract boolean isAllowed(IDataSet arg0, Relation arg1);

	public abstract ConfigurationSection getSettings();
	
	

}
