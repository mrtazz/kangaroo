package com.kangaroo.task;

import com.mobiletsm.osm.data.searching.POICode;

/**
 * Instances of this class represent TaskConstraints 
 * that require a certain type of Amenity to fulfill the Task
 * 
 * @author alex
 */
public class TaskConstraintPOI implements TaskConstraintInterface
{
	private POICode myPOI;
	
	public TaskConstraintPOI()
	{
		
	}
	
	public TaskConstraintPOI(POICode id)
	{
		myPOI = id;
	}
	
	public String getType() 
	{
		return "amenity";
	}
	
	public int getId()
	{
		return myPOI.getId();
	}
	
	public String getText()
	{
		return myPOI.getType();
	}
	
}
