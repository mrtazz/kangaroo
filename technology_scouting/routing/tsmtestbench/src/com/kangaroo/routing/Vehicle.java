/**
 * 
 */
package com.kangaroo.routing;

/**
 * This class represents a type of vehicle that is used 
 * to move from place to place, e.g. a car, a bicycle or
 * simply the feet.
 * @author Andreas Walz
 *
 */
public class Vehicle {
	
	/**
	 * The maximum speed for this vehicle, specified in km/s
	 */
	private double maxSpeed = 100;
	
	/**
	 * set the maximum speed for this vehicle
	 * @param	speed	maximum speed in km/s
	 */
	public void setMaxSpeed(double speed) {
		maxSpeed = speed;
	}
	
	/**
	 * get the maximum speed for this vehicle in km/s
	 */
	public double getMaxSpeed() {
		return maxSpeed;
	}
	
	

}
