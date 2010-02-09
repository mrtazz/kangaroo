/**
 * 
 */
package com.kangaroo.statuschange;


/**
 * @author Andreas Walz
 *
 */
public class JobStartedStatusChange extends StatusChange {

	public JobStartedStatusChange(int id) {
		this.busy = true;
		this.jobID = id;
		this.message = null;
		this.progress = JOBSTARTED_PROGRESS;
		this.result = null;
	}
	
	public JobStartedStatusChange(int id, String message) {
		this.busy = true;
		this.jobID = id;
		this.message = message;
		this.progress = JOBSTARTED_PROGRESS;
		this.result = null;
	}
	
}
