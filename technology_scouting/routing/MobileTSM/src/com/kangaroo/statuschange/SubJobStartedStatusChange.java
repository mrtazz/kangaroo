/**
 * 
 */
package com.kangaroo.statuschange;

/**
 * @author andreaswalz
 *
 */
public class SubJobStartedStatusChange extends JobStartedStatusChange {

	/**
	 * 
	 */
	public int parentJobID = this.UNDEFINED;
	
	
	public SubJobStartedStatusChange(int id) {
		super(id);
	}
	
	public SubJobStartedStatusChange(int id, String message) {
		super(id, message);
	}

}
