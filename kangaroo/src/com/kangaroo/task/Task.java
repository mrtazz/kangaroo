package com.kangaroo.task;

import java.util.HashSet;
import java.util.Iterator;

import com.google.gson.Gson;

/**
 * Instances of this class represent tasks in the kangaroo system. 
 * A task is a action, that is not bound to a specific time, but my very well be associated with
 * one or more constraints (TaskConstraint) that have an impact on when and where the task can be executed.
 * 
 * @author alex
 */
public class Task 
{
	
	private transient HashSet<TaskConstraintInterface> constraintSet;
	private String serializedConstraintSet = "";
	private String serializedConstraintSetTypes = "";
	private String name;
	private String description;
	
	/**
	 * Blank Constructor
	 */
	public Task()
	{
		constraintSet = new HashSet<TaskConstraintInterface>();
		name = "";
	}
	
	/**
	 * Constructor to generate a new Task
	 * 
	 * @param title: title for this task
	 */
	public Task(String myTasktitle)
	{
		name = myTasktitle;
		constraintSet = new HashSet<TaskConstraintInterface>();
	}
	
	
	public static Task deserialize(String task)
	{
		Gson serializer = new Gson();
		Task myTask = serializer.fromJson(task, Task.class);
		String constraints[] = myTask.serializedConstraintSet.split("\\|");
		String constraintTypes[] = myTask.serializedConstraintSetTypes.split("\\|");
		for(int i=0;i<constraints.length;i++)
		{
			if(constraintTypes[i].equalsIgnoreCase("amenity"))
			{
				myTask.addConstraint(serializer.fromJson(constraints[i], TaskConstraintAmenity.class));
			}
			else if(constraintTypes[i].equalsIgnoreCase("date"))
			{
				myTask.addConstraint(serializer.fromJson(constraints[i], TaskConstraintDate.class));
			}
			else if(constraintTypes[i].equalsIgnoreCase("daytime"))
			{
				myTask.addConstraint(serializer.fromJson(constraints[i], TaskConstraintDayTime.class));
			}
			else if(constraintTypes[i].equalsIgnoreCase("location"))
			{
				myTask.addConstraint(serializer.fromJson(constraints[i], TaskConstraintLocation.class));
			}
			else if(constraintTypes[i].equalsIgnoreCase("pending"))
			{
				myTask.addConstraint(serializer.fromJson(constraints[i], TaskConstraintPendingTasks.class));
			}
		}
		
		return myTask;
	}
	
	/**
	 * This can be called to get a serialized representation of this object to store in persistant memory.
	 * 
	 * @return SerializedTask-object thats represents this Task
	 */
	public String serialize()
	{
		Gson serializer = new Gson();
		TaskConstraintInterface currentTask;
		serializedConstraintSet = "";
		serializedConstraintSetTypes = "";
		Iterator<TaskConstraintInterface> it = constraintSet.iterator();
		while(it.hasNext())
		{
			currentTask = it.next();
			String type = currentTask.getType();
			String tempJSON = "";
			if(type.equalsIgnoreCase("amenity"))
			{
				TaskConstraintAmenity temp = (TaskConstraintAmenity)currentTask;
				tempJSON = serializer.toJson(temp);
			}
			else if(type.equalsIgnoreCase("date"))
			{
				TaskConstraintDate temp = (TaskConstraintDate)currentTask;
				tempJSON = serializer.toJson(temp);
			}
			else if(type.equalsIgnoreCase("daytime"))
			{
				TaskConstraintDayTime temp = (TaskConstraintDayTime)currentTask;
				tempJSON = serializer.toJson(temp);
			}
			else if(type.equalsIgnoreCase("location"))
			{
				TaskConstraintLocation temp = (TaskConstraintLocation)currentTask;
				tempJSON = serializer.toJson(temp);
			}
			else if(type.equalsIgnoreCase("pending"))
			{
				TaskConstraintPendingTasks temp = (TaskConstraintPendingTasks)currentTask;
				tempJSON = serializer.toJson(temp);
			}
			serializedConstraintSet = serializedConstraintSet + "|" + tempJSON;
			serializedConstraintSetTypes = serializedConstraintSetTypes +  "|" + type;
		}
		serializedConstraintSetTypes = serializedConstraintSetTypes.substring(1, serializedConstraintSetTypes.length());
		serializedConstraintSet = serializedConstraintSet.substring(1, serializedConstraintSet.length());
		return serializer.toJson(this);
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
	
	
	
	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description) 
	{
		this.description = description;
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
}
