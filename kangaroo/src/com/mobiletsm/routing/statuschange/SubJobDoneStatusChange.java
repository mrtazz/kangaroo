/**
 * 
 */
package com.mobiletsm.routing.statuschange;

/**
 * @author andreaswalz
 *
 */
public class SubJobDoneStatusChange extends JobDoneStatusChange {

	/**
	 * 
	 */
	public int parentJobID = this.UNDEFINED;
	
	
	public SubJobDoneStatusChange(int id) {
		super(id);
		this.busy = true;
	}
	
	public SubJobDoneStatusChange(int id, Object result) {
		super(id, result);
		this.busy = true;
	}

}
