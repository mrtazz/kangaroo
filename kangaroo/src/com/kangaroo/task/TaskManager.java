package com.kangaroo.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;

import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.calendar.CalendarLibrary;
import com.mobiletsm.routing.Place;

public class TaskManager 
{
	private CalendarLibrary cl;
	private Context ctx;
	private String calendarName;
	private int calendarId;
	
	//Constructor
	public TaskManager(Context myCtx)
	{
		ctx = myCtx;
		cl = new CalendarLibrary(ctx);
	}
	
	//get all Tasks from Calendar
	public ArrayList<Task> getTasks()
	{
		String[] calendars = cl.getAllCalendarNames();

		for(int i=0; i<calendars.length; i++)
		{
			if(calendars[i].equalsIgnoreCase("kangaroo@lordofhosts.de"))
			{
				calendarName = calendars[i];
				calendarId = i;
				break;
			}
		}
		//if our calendar is not present, return null
		if(calendarName == null)
		{
			return null;
		}
		HashMap<String,CalendarEvent> myMap = cl.getEventsByDate(calendarName, new Date(0,0,1));
		
		CalendarEvent myEvents[] = myMap.values().toArray(new CalendarEvent[0]);
		ArrayList<Task> myTasks = new ArrayList<Task>();
		for(int i=0; i<myEvents.length; i++)
		{
			myTasks.add(Task.deserialize(myEvents[i].getDescription()));
		}
		
		return myTasks;
	}
	
	//put all Tasks in Calendar
	public int putTasks(ArrayList<Task> tasks)
	{
		Iterator<Task> it = tasks.iterator();
		while(it.hasNext())
		{
			addTask(it.next());
		}
		return 0;
	}
	
	//add one Task
	public int addTask(Task myTask)
	{
		cl.updateIfNotInsertEventToBackend(getEventForTask(myTask));
		return 0;
	}
	
	//delete Task
	public int deleteTask(Task myTask)
	{
		cl.deleteEventFromBackend(getEventForTask(myTask));
		return 0;
	}
	
	private CalendarEvent getEventForTask(Task myTask)
	{
		String taskString = myTask.serialize();
		Date startDate = new Date(0,0,1,0,0);
		Date endDate = new Date(0,0,1,0,1);
		return new CalendarEvent(myTask.getJsonHash(), "task", "", 0.0, 0.0, startDate, endDate, false, false, false, taskString, 0, "", (Place)null);	
	}
	
}
