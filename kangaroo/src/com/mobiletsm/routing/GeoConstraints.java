/**
 * 
 */
package com.mobiletsm.routing;

/**
 * @author andreaswalz
 *
 */
public class GeoConstraints {
	
	
	private Place direction = null;

	
	/**
	 * @return the direction
	 */
	public Place getDirection() {
		return direction;
	}

	
	/**
	 * @param direction the direction to set
	 */
	public void setDirection(Place direction) {
		this.direction = direction;
	}
	
	
	public GeoConstraints(Place direction) {
		super();
		this.direction = direction;
	}

}
