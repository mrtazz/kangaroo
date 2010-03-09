package com.kangaroo.task;

import com.mobiletsm.routing.Place;

/**
 * Instances of this class represent TaskConsteraints that require to the task to be executed at 
 * a certain exact location.
 * 
 * @author alex
 */
public class TaskConstraintLocation implements TaskConstraintInterface
{
	private Place myPlace = null;
	
	public TaskConstraintLocation()
	{
		
	}
	
	public TaskConstraintLocation(Place place)
	{
		this.myPlace = place;
	}
	
	public Place getPlace()
	{
		return myPlace;
	}

	public String getType() 
	{
		return "location";
	}
}
