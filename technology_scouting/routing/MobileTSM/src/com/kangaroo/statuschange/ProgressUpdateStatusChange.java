/**
 * 
 */
package com.kangaroo.statuschange;

/**
 * @author andreaswalz
 *
 */
public class ProgressUpdateStatusChange extends StatusChange {

	public ProgressUpdateStatusChange(int id, int progress) {
		this.busy = true;
		this.jobID = id;
		this.message = null;
		this.progress = progress;
		this.result = null;
	}
	
}
