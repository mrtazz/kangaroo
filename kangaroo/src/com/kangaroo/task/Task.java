package com.kangaroo.task;

import java.util.HashSet;
import java.util.Set;

/**
 * Instances of this class represent tasks in the kangaroo system. 
 * A task is a action, that is not bound to a specific time, but my very well be associated with
 * one or more constraints (TaskConstraint) that have an impact on when and where the task can be executed.
 * 
 * @author alex
 */
public class Task 
{
	
	private Integer maxConstraintId;
	private Set<TaskConstraintInterface> constraintSet;
	private String name;
	
	/**
	 * Blank Constructor
	 */
	public Task()
	{
		maxConstraintId = 0;
		constraintSet = new HashSet<TaskConstraintInterface>();
		name = "";
	}
	
	/**
	 * Constructor to reconstruct a Task from a SerializedTask Container
	 * 
	 * @param myTask: SerializedTask object to build this Task from
	 */
	public Task(TaskSerialized myTask)
	{
		//TODO implement de-serialization
	}
	
	/**
	 * Constructor with setting of the name for the new Task
	 * @param newName
	 */
	public Task(String newName)
	{
		name = newName;
	}
	
	/**
	 * This can be called to get a serialized representation of this object to store in persistant memory.
	 * 
	 * @return SerializedTask-object thats represents this Task
	 */
	public TaskSerialized serialize()
	{
		//TODO implement serialization 
		return null;
	}
	
	/**
	 * Set the name-property of this Task
	 * 
	 * @param name
	 */
	public void setName(String newName)
	{
		this.name = newName;
	}
	
	/**
	 * Get the name-property of this Task
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Add a constraint represented by a TaskConstraint-object to this Task
	 * 
	 * @param currentConstraint: TaskConstraint-object to add
	 * @return int: 0 if ok, 1 if object already in set
	 */
	public int addConstraint(TaskConstraintInterface currentConstraint)
	{
		if(constraintSet.add(currentConstraint))
		{
			return 0;
		}
		return 1;
	}
	
	/**
	 * Get an array containing all the constraints associated with this Task
	 * 
	 * @return TasConstraint[]: array of constraints for this Task
	 */
	public TaskConstraintInterface[] getConstraints()
	{
		return constraintSet.toArray(new TaskConstraintInterface[0]);
	}
	
	/**
	 * Remove one TaskConstraint-object from the constraints associated with this Task
	 * 
	 * @param currentConstraint: The TaskConstraint-object to be removed
	 * @return int: 0 if ok, 1 if task was not in set
	 */
	public int removeConstraint(TaskConstraintInterface currentConstraint)
	{
		if(constraintSet.remove(currentConstraint))
		{
			return 0;
		}
		return 1;
	}
	
	/**
	 * Get the next free ID for TaskConstraint-objects for this Task.
	 * The ID is only unique for one Task
	 * @return int: the next ID
	 */
	public int getNextConstraintId()
	{
		++maxConstraintId;
		return maxConstraintId;
	}
}
