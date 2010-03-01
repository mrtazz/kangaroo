package com.kangaroo.task;

/**
 * Instances of this class are TaskConstraints, that specify that the task can only be executed,
 * after a list of certain other tasks have been fulfilled.
 *
 * @author alex
 */
public class TaskConstraintPendingTasks implements TaskConstraintInterface
{
	private int id;
	private String dependsOnTask;
	
	public TaskConstraintPendingTasks()
	{
		
	}
	
	public TaskConstraintPendingTasks(String TaskName)
	{
		dependsOnTask = TaskName;
	}
	
	public String getTaskName() 
	{
		return dependsOnTask;
	}

	public String getType() 
	{
		return "pending";
	}
}
