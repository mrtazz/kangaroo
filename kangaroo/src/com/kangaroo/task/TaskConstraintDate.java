package com.kangaroo.task;

import java.util.Date;

/**
 * Instances of this class represent a TaskConstraint, that specifies 
 * in what range of dates the Task has do be completed.
 * 
 * @author alex
 */
public class TaskConstraintDate implements TaskConstraintInterface
{
	Date endDate = null;
	Date startDate = null;
	
	public TaskConstraintDate()
	{
		
	}
	
	public TaskConstraintDate(Date date)
	{
		endDate = date;
	}	
	
	public TaskConstraintDate(Date start, Date end)
	{
		endDate = end;
		startDate = start;
	}

	public Date getEnd()
	{
		return endDate;
	}
	
	public Date getStart()
	{
		return startDate;
	}
	
	public String getType() 
	{
		return "date";
	}
}
