/**
 * 
 */
package com.kangaroo.statuschange;

/**
 * This class represents a change in the status of any engine
 * or working thread and any operation.
 * 
 * @author Andreas Walz
 *
 */
public abstract class StatusChange {

	/**
	 * if the field progress equals this constant, progress
	 * is undefined
	 */
	public static final int UNDEFINED = -1;
	
	public static final int JOBSTARTED_PROGRESS = 0;
	
	public static final int JOBDONE_PROGRESS = 100;
	
	
	/**
	 * 
	 */
	public int jobID;
	
	
	/**
	 * true, if this status change was caused while an
	 * operation is still enduring	
	 */
	public boolean busy;
	
	
	/**
	 * progress of an enduring or recently finished operation
	 * (in percent)
	 */
	public int progress;
	
	
	/**
	 * message string describing the status change
	 */
	public String message;
	
	
	/**
	 * object containing the result of the operation
	 */
	public Object result;
		
}
