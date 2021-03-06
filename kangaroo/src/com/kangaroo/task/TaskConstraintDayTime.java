package com.kangaroo.task;

import java.util.Date;

/**
 * Instances of this class represent TaskConstraints, that require the task to be executed at
 * a certain time of the day.
 * 
 * @author alex
 */
public class TaskConstraintDayTime implements TaskConstraintInterface 
{
	private Date startTime;
	private Date endTime;
	
	public TaskConstraintDayTime()
	{
		
	}
	
	public TaskConstraintDayTime(Date startTime, Date endTime)
	{
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public TaskConstraintDayTime(int startHour, int startMinute, int endHour, int endMinute)
	{
		this(new Date(0, 0, 0, startHour, startMinute), new Date(0, 0, 0, endHour, endMinute));
	}
	
	public Date getStartTime()
	{
		return startTime;
	}
	
	public Date getEndTime()
	{
		return endTime;
	}
	
	public String getType() 
	{
		return "daytime";
	}
}
