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
		
		/* a compliance check should only operate on events */
		prepareEventAccess(false);
		
		int result = 0;
		
		try {
			result = super.checkComplianceWith(now, here, destinationEvent, vehicle);		
		} catch (RuntimeException exception) {
			/* make sure we step from this level even if a
			 * runtime exception occurs */
			terminateEventAccess(true);
			throw exception;
		}
		
		/* the routing engine may have affected even places */
		terminateEventAccess(false);
		return result;
	}



	@Override
	public DayPlanConsistency checkConsistency(Vehicle vehicle) {
		/* a consistency check should only operate on events */
		prepareEventAccess(false);
		
		DayPlanConsistency result = null;
		
		try {
			result = super.checkConsistency(vehicle);			
		} catch (RuntimeException exception) {
			/* make sure we step from this level even if a
			 * runtime exception occurs */
			terminateEventAccess(true);
			throw exception;
		}
		
		/* the routing engine may have affected even places */
		terminateEventAccess(false);
		return result;
	}



	@Override
	public List<CalendarEvent> getEvents() {
		/* prepare to access events */
		prepareEventAccess(false);
		
		List<CalendarEvent> result = null;
		
		try {
			result = super.getEvents();			
		} catch (RuntimeException exception) {
			/* make sure we step from this level even if a
			 * runtime exception occurs */
			terminateEventAccess(true);
			throw exception;
		}
		
		/* this is only a read access, so don't write to the calendar */
		terminateEventAccess(true);
		return result;		
	}



	@Override
	public CalendarEvent getNextEvent(Date now) {
		/* prepare to access events */
		prepareEventAccess(false);
		
		CalendarEvent result = null;
		
		try {
			result = super.getNextEvent(now);	
		} catch (RuntimeException exception) {
			/* make sure we step from this level even if a
			 * runtime exception occurs */
			terminateEventAccess(true);
			throw exception;
		}
		
		/* this is only a read access, so don't write to the calendar */
		terminateEventAccess(true);
		return result;	
	}



	@Override
	public Collection<Task> getTasks() {
		/* prepare to access tasks */
		prepareTaskAccess(false);
		
		Collection<Task> result = null;
		
		try {
			result = super.getTasks();			
		} catch (RuntimeException exception) {
			/* make sure we step from this level even if a
			 * runtime exception occurs */
			terminateTaskAccess(true);
			throw exception;
		}
		
		/* this is only a read access, so don't write to the calendar */
		terminateTaskAccess(true);
		return result;
	}



	@Override
	public DayPlan optimize() {
		/* prepare to access events and tasks */
		prepareEventAccess(false);
		prepareTaskAccess(false);
		
		DayPlan result = null;
		
		try {
			result = super.optimize();
		} catch (RuntimeException exeption) {
			/* make sure we step from this level even if a
			 * runtime exception occurs */
			terminateTaskAccess(true);
			terminateEventAccess(true);
			throw exeption;
		}
		
		/* the routing engine may have affected places in events and tasks */
		terminateTaskAccess(false);
		terminateEventAccess(false);
		return result;
	}



	@Override
	public void setEvents(List<CalendarEvent> events) {
		/* we don't have to read the calendar */
		prepareEventAccess(true);
		
		try {
			super.setEvents(events);
		} catch (RuntimeException exception) {
			/* make sure we step from this level even if a
			 * runtime exception occurs */
			terminateEventAccess(true);
			throw exception;
		}
		
		terminateEventAccess(false);
	}



	@Override
	public void setTasks(Collection<Task> tasks) {
		/* we don't have to read the calendar */
		prepareTaskAccess(true);
		
		try {
			super.setTasks(tasks);
		} catch (RuntimeException exception) {
			/* make sure we step from this level even if a
			 * runtime exception occurs */
			terminateTaskAccess(true);
			throw exception;
		}
		
		terminateTaskAccess(false);
	}
	
	
	
	private int eventAccessDepth = 0;
	
	
	private int taskAccessDepth = 0;
	
	
	private void prepareEventAccess(boolean skipLoad) {
		if (eventAccessDepth++ == 0 && !skipLoad) {
			loadEvents();
		}
	}
	
	
	private void terminateEventAccess(boolean skipSave) {
		if (eventAccessDepth <= 0) {
			throw new RuntimeException("ActiveDayPlan.terminateEventAccess(): " +
					"cannot step back from level 0");
		}
		if (--eventAccessDepth == 0 && !skipSave) {
			saveEvents();
		}
	}
	
	
	private void prepareTaskAccess(boolean skipLoad) {
		if (taskAccessDepth++ == 0 && !skipLoad) {
			loadTasks();
		}
	}
	
	
	private void terminateTaskAccess(boolean skipSave) {
		if (taskAccessDepth <= 0) {
			throw new RuntimeException("ActiveDayPlan.terminateTaskAccess(): " +
			"cannot step back from level 0");
		}
		if (--taskAccessDepth == 0 && !skipSave) {
			saveTasks();
		}
	}
	
	
	
	
}
