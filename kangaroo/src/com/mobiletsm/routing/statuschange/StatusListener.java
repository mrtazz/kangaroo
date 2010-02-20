/**
 * 
 */
package com.mobiletsm.routing.statuschange;


/**
 * @author Andreas Walz
 *
 */
public interface StatusListener {

	/**
	 * called whenever a significant change in the routing
	 * engine's status occurred
	 */
	public void onStatusChanged(StatusChange status);
	
}
