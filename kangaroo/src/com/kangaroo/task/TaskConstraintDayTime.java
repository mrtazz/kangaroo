package com.kangaroo.task;

/**
 * Instances of this class represent TaskConstraints, that require the task to be executed at
 * a certain time of the day.
 * 
 * @author alex
 */
public class TaskConstraintDayTime implements TaskConstraintInterface 
{
	private int id;
	
	public TaskConstraintDayTime(int newId)
	{
		id = newId;
	}
	public int getID() 
	{
		return id;
	}

	public String getType() 
	{
		return "daytime";
	}
}
