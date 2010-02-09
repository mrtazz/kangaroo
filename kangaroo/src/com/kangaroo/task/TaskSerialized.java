package com.kangaroo.task;

/**
 * This class is a container that holds the byte-array representation of one serialized Task-object.
 * 
 * @author alex
 *
 */
public class TaskSerialized 
{
	byte[] taskContent;
	
	/**
	 * Constructor that sets the byte-array
	 * @param task
	 */
	public TaskSerialized(byte[] task)
	{
		taskContent = task;
	}
	
	/**
	 * Get the byte-array with the serialized representation of this task
	 * 
	 * @return byte[]: serialized Task
	 */
	public byte[] getTaskContent() 
	{
		return taskContent;
	}
}
