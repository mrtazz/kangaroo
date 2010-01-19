/**
 * 
 */
package com.kangaroo.statuschange;

/**
 * @author andreaswalz
 *
 */
public class ResultUpdateStatusChange extends StatusChange {

	public ResultUpdateStatusChange(int id) {
		this.busy = true;
		this.jobID = id;
		this.message = null;
		this.progress = UNDEFINED;
		this.result = null;
	}
	
	public ResultUpdateStatusChange(int id, Object result) {
		this.busy = true;
		this.jobID = id;
		this.message = null;
		this.progress = UNDEFINED;
		this.result = result;
	}
	
}
