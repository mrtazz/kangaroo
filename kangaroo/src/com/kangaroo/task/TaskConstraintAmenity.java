package com.kangaroo.task;

/**
 * Instances of this class represent TaskConstraints 
 * that require a certain type of Amenity to fulfill the Task
 * 
 * @author alex
 */
public class TaskConstraintAmenity implements TaskConstraintInterface
{
	private int amenityID;
	private String amenityText;
	
	public TaskConstraintAmenity()
	{
		
	}
	
	public TaskConstraintAmenity(int id)
	{
		amenityID = id;
	}
	
	public String getType() 
	{
		return "amenity";
	}
	
	public int getId()
	{
		return amenityID;
	}
	
	public String getText()
	{
		return amenityText;
	}
	
}
