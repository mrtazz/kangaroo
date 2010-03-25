package com.kangaroo.task;

public class TaskConstraintDuration implements TaskConstraintInterface {

	private int duration = 0;
	
	public TaskConstraintDuration()
	{
		
	}
	
	public TaskConstraintDuration(int duration) 
	{
		this.duration = duration;
	}
	
	public void setDuration(int myD)
	{
		duration = myD;
	}
	
	public int getDuration() 
	{
		return duration;
	}
	
	
	public String getType() 
	{
		return TaskConstraintInterface.TYPE_DURATION;
	}

}
