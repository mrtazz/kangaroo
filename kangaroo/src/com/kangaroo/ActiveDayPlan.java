package com.kangaroo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.calendar.CalendarEventComparator;
import com.kangaroo.calendar.CalendarLibrary;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskManager;
import com.mobiletsm.routing.NoRouteFoundException;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RouteParameter;
import com.mobiletsm.routing.RoutingEngine;
import com.mobiletsm.routing.Vehicle;

public class ActiveDayPlan extends DayPlan {

	
	private Context ctx;
	
	
	private void loadTasks()
	{
		TaskManager tm = new TaskManager(ctx);
		tasks = tm.getTasks();
	}
	
	
	private void saveTasks()
	{
		TaskManager tm = new TaskManager(ctx);
		tm.putTasks((ArrayList<Task>)tasks);
	}
	
	
	private void loadEvents()
	{
		CalendarLibrary cl = new CalendarLibrary(ctx);
		int calendarId = cl.getCalendar("kangaroo@lordofhosts.de").getId();
		//if our calendar is not present, return null
		
		ArrayList<CalendarEvent> myMap = cl.getTodaysEvents(String.valueOf(calendarId));
		events.addAll(myMap);

	}
	
	
	private void saveEvents()
	{

		CalendarLibrary cl = new CalendarLibrary(ctx);
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
	


	public void setContext(Context myCtx)
	{
		ctx = myCtx;
	}



	@Override
	public int checkComplianceWith(Date now, Place here,
			CalendarEvent destinationEvent, Object vehicle) throws NoRouteFoundException {
		return super.checkComplianceWith(now, here, destinationEvent, vehicle);
	}



	@Override
	public DayPlanConsistency checkConsistency(Vehicle vehicle) {
		// TODO Auto-generated method stub
		return super.checkConsistency(vehicle);
	}



	@Override
	public List<CalendarEvent> getEvents() {
		// TODO Auto-generated method stub
		return super.getEvents();
	}



	@Override
	public CalendarEvent getNextEvent(Date now) {
		// TODO Auto-generated method stub
		return super.getNextEvent(now);
	}



	@Override
	public Collection<Task> getTasks() {
		// TODO Auto-generated method stub
		return super.getTasks();
	}



	@Override
	public DayPlan optimize() {
		// TODO Auto-generated method stub
		return super.optimize();
	}



	@Override
	public void setEvents(List<CalendarEvent> events) {
		// TODO Auto-generated method stub
		super.setEvents(events);
	}



	@Override
	public void setTasks(Collection<Task> tasks) {
		// TODO Auto-generated method stub
		super.setTasks(tasks);
	}
	
	
	
	
	
}
