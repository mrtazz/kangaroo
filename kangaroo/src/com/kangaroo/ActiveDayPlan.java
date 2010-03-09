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
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RouteParameter;
import com.mobiletsm.routing.RoutingEngine;
import com.mobiletsm.routing.Vehicle;

public class ActiveDayPlan implements DayPlan {
	

	private List<CalendarEvent> events;
	
	private Collection<Task> tasks;
	
	private Context ctx;
	
	private DayPlanOptimizer optimizer = null;
	
	private RoutingEngine routingEngine = null;
	
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
	
	public ActiveDayPlan() {
		super();
		events = new ArrayList<CalendarEvent>();
		tasks = new ArrayList<Task>();
	}
	
	
	public ActiveDayPlan(DayPlan plan) {
		this();
		events.addAll(plan.getEvents());
		tasks.addAll(plan.getTasks());
	}
	
	
	
	public RoutingEngine getRoutingEngine() 
	{
		return routingEngine;
	}

	public void setRoutingEngine(RoutingEngine routingEngine) 
	{
		this.routingEngine = routingEngine;
	}

	@Override
	public List<CalendarEvent> getEvents() {
		return events;
	}

	public void setContext(Context myCtx)
	{
		ctx = myCtx;
	}
	
	@Override
	/**
	 * return the event in the calendar that is next starting from given Date
	 * @param now
	 * @return
	 */
	public CalendarEvent getNextEvent(Date now) {
		loadEvents();
		loadTasks();
		
		/* make sure the events are in correct order */
		Collections.sort(events, new CalendarEventComparator(CalendarEventComparator.START_DATE));
		
		/* return first event in the list if no date is given */
		if (now == null && events.size() > 0) {
			return events.get(0);
		}
		
		/* return the first event that has a start date after now */
		Iterator<CalendarEvent> itr = events.iterator();
		while (itr.hasNext()) {
			CalendarEvent event = itr.next();
			if (event.getStartDate().compareTo(now) > 0)
				return event;			
		}
		
		/* there are no events or all events are in the past */
		return null;
	}

	
	@Override
	public Collection<Task> getTasks() {
		return tasks;
	}

	
	@Override
	public void setEvents(List<CalendarEvent> events) {
		if (events != null) {
			this.events = events;
		} else {
			throw new RuntimeException("ActiveDayPlan.setEvents(): null reference given");
		}
	}

	
	@Override
	public void setTasks(Collection<Task> tasks) {
		if (tasks != null) {
			this.tasks = tasks;
		} else {
			throw new RuntimeException("ActiveDayPlan.setTasks(): null reference given");
		}
	}

	
	/**
	 * return the number of minutes left to start moving towards the next event
	 * @param now
	 * @param here
	 * @param vehicle
	 * @return
	 */
	public int checkComplianceWith(Date now, Place here, Vehicle vehicle) {
		return checkComplianceWith(now, here, null, vehicle);
	}
	
	
	/**
	 * return the number of minutes left to start moving towards the given event
	 * @param now
	 * @param here
	 * @param destinationEvent
	 * @param vehicle
	 * @return
	 */
	public int checkComplianceWith(Date now, Place here, CalendarEvent destinationEvent, Vehicle vehicle) {
		
		//in getNextEvent loadEvents() is executed, so
		if (destinationEvent == null) 
		{
			destinationEvent = this.getNextEvent(now);
		}
		else
		{
			loadEvents();
			loadTasks();
		}
		
		// TODO: use specific exceptions
		
		if (destinationEvent != null) {			
			if (routingEngine == null) {
				throw new RuntimeException("ActiveCalendarPlan.checkComplianceWith(): No RoutingEngine defined");
			}
			
			setCalendarEventPlace(destinationEvent);
		
			RouteParameter route = routingEngine.routeFromTo(here, destinationEvent.getPlace(), vehicle, true);
		
			/* Date.getTime() returns time in milliseconds, so 
			 * we have to divide by 1000*60 to get minutes */
			double timeLeft = (destinationEvent.getStartDate().getTime() - now.getTime()) / (1000 * 60);
			
			/* TODO: handle case where no route could be found */
			if (route.getNoRouteFound()) {
				throw new RuntimeException("ActiveCalendarPlan.checkComplianceWith(): No route found");
			}
						
			timeLeft = timeLeft - route.getDurationOfTravel();
			
			return (int)timeLeft;
			
		} else {
			throw new RuntimeException("ActiveCalendarPlan.checkComplianceWith(): Event missing");
		}
	}
	
	
	/**
	 * checks if the calendar is self-consistent
	 * @return
	 */
	public DayPlanConsistency checkConsistency(Vehicle vehicle) {		
		if (routingEngine == null) {
			throw new RuntimeException("ActiveCalendarPlan.checkConsistency(): No RoutingEngine defined");
		}
		
		DayPlanConsistency consistency = new DayPlanConsistency();
		
		Date pos = null;
		CalendarEvent event = null;
		CalendarEvent predecessor = null;
		
		/* iterate over all CalendarEvents in the calendar and check consistency  */
		while ((event = getNextEvent(pos)) != null) {
			if (predecessor != null) {				
				// TODO: add routing cache (needs hash for CalendarEvents)
				double timeLeft = checkComplianceWith(predecessor.getEndDate(), predecessor.getPlace(), event, vehicle);									
				if (timeLeft < 0) {
					consistency.addCollision(event, predecessor, timeLeft);
				}
			}			
			predecessor = event;
		}
		
		return consistency;
	}
		
	
	private void setCalendarEventPlace(CalendarEvent event) {
		Place eventPlace = event.getPlace();		
		if (eventPlace == null) {
			eventPlace = new Place(event.getLocationLatitude(), event.getLocationLongitude());
			event.setPlace(eventPlace);
		}
	}
	
	
	public DayPlan optimize() {
		if (optimizer == null) {
			throw new RuntimeException("ActiveCalendarPlan.optimize(): No CalendarPlanOptimizer defined");
		}
		
		optimizer.setDayPlan(this);
		optimizer.setRoutingEngine(routingEngine);
				
		return null;
	}
	
	
	public void setOptimizer(DayPlanOptimizer optimizer) {
		this.optimizer = optimizer;
	}
	
	
	/**
	 * synchronize this ActiveCalendarPlan with calendar system of mobile device 
	 * @return
	 */
	public boolean sync() 
	{
		//we need a context to work with the calendar
		if(ctx == null)
		{
			return false;
		}
		CalendarLibrary cl = new CalendarLibrary(ctx);
		
		
		return true;
	}
}
