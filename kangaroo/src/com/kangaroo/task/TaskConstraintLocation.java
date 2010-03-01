package com.kangaroo.task;

/**
 * Instances of this class represent TaskConsteraints that require to the task to be executed at 
 * a certain exact location.
 * 
 * @author alex
 */
public class TaskConstraintLocation implements TaskConstraintInterface
{
	private long nodeId;
	
	public TaskConstraintLocation()
	{
		
	}
	
	public TaskConstraintLocation(int nodeID)
	{
		this.nodeId = nodeID;
	}
	
	public long getNodeId() 
	{
		return nodeId;
	}

	public String getType() 
	{
		return "location";
	}
}
