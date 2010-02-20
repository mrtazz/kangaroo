/**
 * 
 */
package com.mobiletsm.routing.statuschange;

/**
 * @author andreaswalz
 *
 */
public class JobFailedStatusChange extends StatusChange {

	public JobFailedStatusChange(int id) {
		this.busy = false;
		this.jobID = id;
		this.message = null;
		this.progress = UNDEFINED;
		this.result = null;
	}
	
	public JobFailedStatusChange(int id, Exception exception) {
		this.busy = false;
		this.jobID = id;
		this.message = null;
		this.progress = UNDEFINED;
		this.result = exception;
	}
	
}
