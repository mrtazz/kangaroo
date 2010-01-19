/**
 * 
 */
package com.kangaroo.routing;

/**
 * @author Andreas Walz
 *
 */
public interface StatusListener {

	/**
	 * called whenever a significant change in the routing
	 * engine's status occurred
	 */
	public void onRoutingManagerStatusChanged(StatusChange status);
	
}
