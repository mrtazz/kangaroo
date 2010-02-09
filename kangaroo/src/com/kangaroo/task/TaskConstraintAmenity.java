package com.kangaroo.task;

/**
 * Instances of this class represent TaskConstraints 
 * that require a certain type of Amenity to fulfill the Task
 * 
 * @author alex
 */
public class TaskConstraintAmenity implements TaskConstraintInterface
{
	private int id;
	
	public TaskConstraintAmenity(int newId)
	{
		id = newId;
	}
	
	//TODO implement a data-structure to hold the amenity-type required
	
	public int getID() 
	{
		return id;
	}

	public String getType() 
	{
		return "amenity";
	}
}
