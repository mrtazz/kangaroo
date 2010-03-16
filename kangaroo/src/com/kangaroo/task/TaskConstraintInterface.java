package com.kangaroo.task;

/**
 * This interface has to be implemented by all classes, that will be used as constraints in the Task-Class.
 * @author alex
 *
 */
public interface TaskConstraintInterface {
	
	public static final String TYPE_POI = "amenity";
	
	public static final String TYPE_DATE = "date";
	
	public static final String TYPE_DAYTIME = "daytime";
	
	public static final String TYPE_LOCATION = "location";
	
	public static final String TYPE_PENDING_TASK = "pending";
	
	public static final String TYPE_DURATION = "duration";
	
	public static final String TYPE_PRIORITY = "priority";
	
		
	
	/**
	 * This method returns the type of this concrete TaskConstraint.
	 * Depending on the type a different validation method is used the different constraints.
	 * 
	 * @return String: the type of this concrete TaskConstraint. TODO: String is not a good choice here
	 */
	public String getType();
	
}
