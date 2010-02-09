package com.kangaroo.task;

/**
 * Instances of this class represent TaskConsteraints that require to the task to be executed at 
 * a certain exact location.
 * 
 * @author alex
 */
public class TaskConstraintLocation implements TaskConstraintInterface
{
	private int id;
	
	public TaskConstraintLocation(int newId)
	{
		id = newId;
	}
	
	public int getID() 
	{
		return id;
	}

	public String getType() 
	{
		return "location";
	}
}
