package com.kangaroo.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.kangaroo.task.Task;
import com.kangaroo.task.TaskManager;

public class CalendarAccessAdapterAndroid implements CalendarAccessAdapter {

	
	private Context context = null;
	
	
	@Override
	public List<CalendarEvent> loadEvents() {
		CalendarLibrary cl = new CalendarLibrary(context);
		int calendarId = cl.getCalendar("kangaroo@lordofhosts.de").getId();
		//if our calendar is not present, return null
		
		ArrayList<CalendarEvent> myMap = cl.getTodaysEvents(String.valueOf(calendarId));
		return myMap;
	}
	

	@Override
	public Collection<Task> loadTasks() {
		TaskManager tm = new TaskManager(context);
		return tm.getTasks();
	}
	

	@Override
	public void saveEvents(List<CalendarEvent> events) {
		CalendarLibrary cl = new CalendarLibrary(context);
		int calendarId = cl.getCalendar("kangaroo@lordofhosts.de").getId();
		
		//get List with event currently in Calendar
		ArrayList<CalendarEvent> calendarList = cl.getTodaysEvents(String.valueOf(calendarId));
		Iterator<CalendarEvent> it = calendarList.iterator();
		while(it.hasNext())
		{
			cl.deleteEventFromBackend(it.next());
		}
		
		it = events.iterator();
		while(it.hasNext())
		{
			cl.insertEventToBackend(it.next());
		}
	}
	

	@Override
	public void saveTasks(Collection<Task> tasks) {
		TaskManager tm = new TaskManager(context);
		ArrayList<Task> tasksToPut = new ArrayList<Task>();
		tasksToPut.addAll(tasks);
		tm.putTasks(tasksToPut);
	}
	

	@Override
	public void setContext(Object context) {
		this.context = (Context)context;
	}
	

}
