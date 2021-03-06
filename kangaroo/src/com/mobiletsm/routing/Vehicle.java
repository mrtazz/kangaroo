/**
 * 
 */
package com.mobiletsm.routing;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.osm.ConfigurationSection;
import org.openstreetmap.osm.Tags;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.travelingsalesman.routing.IVehicle;

import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;



/**
 * This class represents a type of vehicle that is used 
 * to move from place to place, e.g. a car, a bicycle or
 * simply the feet.
 * @author Andreas Walz
 *
 */
public abstract class Vehicle implements IVehicle {
	
	/**
	 * mapping from highway tag to maximum speed
	 */
	public static Map<String, Double> maxSpeedMap = null;
		
	
	static {
		maxSpeedMap = new HashMap<String, Double>();
		maxSpeedMap.put("residential", 50.0);
		maxSpeedMap.put("living_street", 7.0);
		maxSpeedMap.put("motorway", 130.0);
		maxSpeedMap.put("trunk", 100.0);
		maxSpeedMap.put("primary", 100.0);
		maxSpeedMap.put("secondary", 100.0);
		maxSpeedMap.put("tertiary", 100.0);		
	}
	
	
	/**
	 * default maximum speed of a vehicle, specified in km/h
	 */
	public static double MAXSPEED_DEFAULT = 150;
	
	
	/**
	 * The maximum speed for this vehicle, specified in km/h
	 */
	protected double maxSpeed = MAXSPEED_DEFAULT;
	
	
	/**
	 * set the maximum speed for this vehicle
	 * @param	speed	maximum speed in km/h
	 */
	protected void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	
	/**
	 * get the maximum speed for this vehicle in km/h
	 */
	public double getMaxSpeed() {
		return maxSpeed;
	}
	

	/**
	 * returns the maximum speed this vehicle can move on the given way in km/h
	 * @param way
	 * @return maximum speed this vehicle can move on the given way in km/h
	 */
	public double getMaxSpeedOnWay(Way way) {
		/* maximum speed in km/h that is assumed if the 
		 * given way does not specify any maximum speed */
		final double defaultWayMaxSpeed = 50;  
	
		/* maximum speeds in km/h for given way and vehicle */
		double wayMaxSpeed = defaultWayMaxSpeed;
		double vehicleMaxSpeed = getMaxSpeed();
		
		/* check if way specifies maximum speed */
		if ((way instanceof MobileWay) && ((MobileWay)way).hasMaxSpeed()) {
			wayMaxSpeed = ((MobileWay)way).getMaxSpeed();
				System.out.println("Vehicle.getMaxSpeedOnWay(): MobileWay.getMaxSpeed() = "
						+ ((MobileWay)way).getMaxSpeed());
		} else {
			/* check way tags for specification of a maximum speed */
			String maxSpeedStr = WayHelper.getTag(way.getTags(), "maxspeed");		
			if (maxSpeedStr != null) {
				try {
					wayMaxSpeed = Double.parseDouble(maxSpeedStr);				
				} catch (NumberFormatException e) {
				}
			} else {			
				/* check type of highway */
				String highway = WayHelper.getTag(way.getTags(), Tags.TAG_HIGHWAY);			
				if (highway != null) {
					
					Double maxSpeedFromMap = maxSpeedMap.get(highway);
					if (maxSpeedFromMap != null) {
						wayMaxSpeed = maxSpeedFromMap.doubleValue();
					}
								
					/*
					if (highway.startsWith("residential")) {
						wayMaxSpeed = 50;
					} else if (highway.startsWith("living_street")) {
						wayMaxSpeed = 7;
					} else if (highway.startsWith("motorway")) {
						wayMaxSpeed = 130;
					} else if (highway.startsWith("trunk") || highway.startsWith("primary") ||
							highway.startsWith("secondary") || highway.startsWith("tertiary")) {
						wayMaxSpeed = 100;
					}
					*/
				}
			}	
		}
		
		/* return the one that is lower */
		if (wayMaxSpeed < vehicleMaxSpeed) {
			return wayMaxSpeed;
		} else {
			return vehicleMaxSpeed;
		}
	}
	
	
	/* methods to be implemented by a vehicle */
	
	public abstract boolean equals(Object object);
	
	public abstract boolean isAllowed(IDataSet arg0, Node arg1);

	public abstract boolean isAllowed(IDataSet arg0, Way arg1);

	public abstract boolean isOneway(IDataSet arg0, Way arg1);

	public abstract boolean isReverseOneway(IDataSet arg0, Way arg1);

	public abstract boolean isAllowed(IDataSet arg0, Relation arg1);

	public abstract ConfigurationSection getSettings();	

}
