package com.kangaroo.task;

/**
 * Instances of this class represent a TaskConstraint, that specifies 
 * in what range of dates the Task has do be completed.
 * 
 * @author alex
 */
public class TaskConstraintDate implements TaskConstraintInterface
{
	private int id;
	
	public TaskConstraintDate(int newId)
	{
		id = newId;
	}
	
	public int getID() 
	{
		return id;
	}

	public String getType() 
	{
		return "date";
	}
}
