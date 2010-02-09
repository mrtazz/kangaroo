/**
 * 
 */
package com.kangaroo.statuschange;

/**
 * @author andreaswalz
 *
 */
public class JobDoneStatusChange extends StatusChange {

	public JobDoneStatusChange(int id) {
		this.busy = false;
		this.jobID = id;
		this.message = null;
		this.progress = JOBDONE_PROGRESS;
		this.result = null;
	}
	
	public JobDoneStatusChange(int id, Object result) {
		this.busy = false;
		this.jobID = id;
		this.message = null;
		this.progress = JOBDONE_PROGRESS;
		this.result = result;
	}

}
