/**
 * 
 */
package com.kangaroo.routing;

/**
 * This class represents a change in the status of any engine
 * or working thread and any operation.
 * 
 * @author andreaswalz
 *
 */
public class StatusChange {

	/**
	 * if the field progress equals this constant, progress
	 * is undefined
	 */
	public static final int PROGRESS_UNDEFINED = -1;
	
	
	/**
	 * 
	 */
	public static final int PROGRESS_FINISHED = 100;
	
	
	/**
	 * 
	 */
	public static final int OPERATION_ID_UNDEFINED = -1;
	
	
	/**
	 * true, if this status change was caused by the end
	 * of an operation
	 */
	public boolean operationFinished = false;
	
	
	/**
	 * true, if this status change was caused while an
	 * operation is still enduring	
	 */
	public boolean operationEnduring = false;
	
	
	/**
	 * progress of an enduring or recently finished operation
	 * (in percent)
	 */
	public int operationProgress = PROGRESS_UNDEFINED;
	
	
	/**
	 * message string describing the status change
	 */
	public String message = null;
	
	
	/**
	 * ID describing the operation that caused the status change
	 */
	public int operationID = OPERATION_ID_UNDEFINED;
	
	
	/**
	 * 
	 * @param finished
	 * @param enduring
	 * @param progress
	 * @param msg
	 * @param id
	 */
	public StatusChange(boolean finished, boolean enduring, int progress, String msg, int id) {
		super();
		
		operationFinished = finished;
		operationEnduring = enduring;
		operationProgress = progress;
		message = msg;
		operationID = id;
	}

	
	/**
	 * 
	 * @param finished
	 * @param enduring
	 * @param progress
	 * @param msg
	 */
	public StatusChange(boolean finished, boolean enduring, int progress, String msg) {
		super();
		
		operationFinished = finished;
		operationEnduring = enduring;
		operationProgress = progress;
		message = msg;
	}
	
	
	/**
	 * create a RoutingEngineStatusChange object an set its parameter
	 * @param progress
	 * @param id
	 */
	public StatusChange(int progress, int id) {
		super();
		
		if (progress == PROGRESS_FINISHED)
			operationFinished = true;
		if ((progress > 0) && (progress < PROGRESS_FINISHED))
			operationEnduring = true;
		operationProgress = progress;
		operationID = id;
	}

	
	/**
	 * 
	 * @param progress
	 */
	public StatusChange(int progress) {
		super();
		
		if (progress == PROGRESS_FINISHED)
			operationFinished = true;
		if ((progress > 0) && (progress < PROGRESS_FINISHED))
			operationEnduring = true;
		operationProgress = progress;
	}
	
	
	/**
	 * 
	 * @param msg
	 * @param done
	 * @param id
	 */
	public StatusChange(String msg, boolean done, int id) {
		super();
		
		operationFinished = done;
		operationEnduring = !done;
		if (done)
			operationProgress = PROGRESS_FINISHED;
		message = msg;
		operationID = id;
	}
	
	
	/**
	 * 
	 * @param msg
	 * @param done
	 */
	public StatusChange(String msg, boolean done) {
		super();
		
		operationFinished = done;
		operationEnduring = !done;
		if (done)
			operationProgress = PROGRESS_FINISHED;
		message = msg;
	}
}
