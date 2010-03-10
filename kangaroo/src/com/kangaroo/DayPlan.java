package com.kangaroo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.calendar.CalendarEventCollision;
import com.kangaroo.calendar.CalendarEventComparator;
import com.kangaroo.calendar.CalendarEventConflict;
import com.kangaroo.calendar.CalendarLibrary;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskManager;
import com.mobiletsm.routing.NoRouteFoundException;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RouteParameter;
import com.mobiletsm.routing.RoutingEngine;
import com.mobiletsm.routing.Vehicle;

public class DayPlan {

	
	protected List<CalendarEvent> events;
	
	
	protected Collection<Task> tasks;
	
	
	protected DayPlanOptimizer optimizer = null;
	
	
	protected RoutingEngine routingEngine = null;
	
	
	/**
	 * create a new instance of DayPlan with empty event and task list
	 */
	public DayPlan() {
		super();
		events = new ArrayList<CalendarEvent>();
		tasks = new ArrayList<Task>();
	}
	
	
	/**
	 * create a new instance of DayPlan using events and task from the given plan
	 * @param plan DayPlan to take events and task from
	 */
	public DayPlan(DayPlan plan) {
		this();
		events.addAll(plan.getEvents());
		tasks.addAll(plan.getTasks());
	}	
	
	
	/**
	 * return associated routing engine
	 * @return
	 */
	public RoutingEngine getRoutingEngine() {
		return routingEngine;
	}
	
	
	/**
	 * return associated day plan optimizer
	 * @return
	 */
	public DayPlanOptimizer getOptimizer() {
		return optimizer;
	}
	

	/**
	 * set routing engine
	 * @param routingEngine
	 */
	public void setRoutingEngine(RoutingEngine routingEngine) {
		this.routingEngine = routingEngine;
	}
	

	/**
	 * set day plan optimizer
	 * @param optimizer
	 */
	public void setOptimizer(DayPlanOptimizer optimizer) {
		this.optimizer = optimizer;
	}
	
	
	/**
	 * get the list of calendar events in this plan. This list
	 * is not guaranteed to be in chronological order.
	 * @return
	 */
	public List<CalendarEvent> getEvents() {
		/* do not return the list itself, but a copy of it */
		return new ArrayList<CalendarEvent>(events);
	}

	
	/**
	 * get the collection of tasks in this plan.
	 * @return
	 */
	public Collection<Task> getTasks() {
		/* do not return the list itself, but a copy of it */
		return new ArrayList<Task>(tasks);
	}
	

	/**
	 * return the event in the calendar that is chronologically
	 * the next starting from given Date. If Date now is null, the
	 * first event is returned. 
	 * @param now 
	 * @return the event in the calendar that is chronologically
	 * the next starting from given Date. If Date now is null, the
	 * first event is returned. 
	 */
	public CalendarEvent getNextEvent(Date now) {
		/* make sure the events are in correct order */
		Collections.sort(events, new CalendarEventComparator(CalendarEventComparator.START_DATE));
		
		/* return first event in the list if no date is given */
		if (now == null && events.size() > 0) {
			return events.get(0);
		}
		
		CalendarEvent result = null;
		
		/* return the first event that has a start date after now */
		Iterator<CalendarEvent> itr = events.iterator();
		while (itr.hasNext() && result == null) {
			CalendarEvent event = itr.next();
			Date startDate = event.getStartDate();
			if (startDate != null && startDate.compareTo(now) > 0) {
				result = event;
			}
		}
		
		return result;
	}


	/**
	 * set events in this plan. The list of events in this plan 
	 * is cleared and the events in the given list are added. 
	 * @param events
	 */
	public void setEvents(List<CalendarEvent> events) {
		this.events.clear();
		if (events != null) {
			this.events.addAll(events);
		}
	}

	
	/**
	 * set tasks in this plan. The collection of tasks in this plan 
	 * is cleared and the events in the given list are added. 
	 * @param tasks
	 */
	public void setTasks(Collection<Task> tasks) {
		this.tasks.clear();
		if (tasks != null) {
			this.tasks.addAll(tasks);
		}
	}

	
	/**
	 * return the number of minutes left to start moving towards destinationEvent when
	 * just having finished at initialEvent
	 * @param initialEvent
	 * @param destinationEvent
	 * @param vehicle
	 * @return
	 * @throws NoRouteFoundException if no route could be found
	 */
	public int checkComplianceBetween(CalendarEvent initialEvent, CalendarEvent destinationEvent, 
			Object vehicle) throws NoRouteFoundException {
		return checkComplianceWith(initialEvent.getEndDate(), initialEvent.getPlace(), destinationEvent, vehicle);
	}
	
	
	/**
	 * return the number of minutes left to start moving towards the next event
	 * @param now
	 * @param here
	 * @param vehicle
	 * @return
	 * @throws NoRouteFoundException if no route could be found
	 */
	public int checkComplianceWith(Date now, Place here, Vehicle vehicle) throws NoRouteFoundException {
		return checkComplianceWith(now, here, getNextEvent(now), vehicle);
	}
	
	
	/**
	 * return the number of minutes left to start moving towards the given event
	 * @param now
	 * @param here
	 * @param destinationEvent
	 * @param vehicle
	 * @return
	 * @throws NoRouteFoundException if no route could be found
	 */
	public int checkComplianceWith(Date now, Place here, CalendarEvent destinationEvent, 
			Object vehicle) throws NoRouteFoundException {
		// TODO: use specific exceptions
		
		if (destinationEvent != null) {			
			if (routingEngine == null) {
				throw new RuntimeException("DayPlan.checkComplianceWith(): No routing engine defined");
			}
		
			RouteParameter route = routingEngine.routeFromTo(here, destinationEvent.getPlace(), vehicle);
		
				System.out.println("route: = " + route.toString());
			
			/* Date.getTime() returns time in milliseconds, so 
			 * we have to divide by 1000*60 to get minutes */
			double timeLeft = (destinationEvent.getStartDate().getTime() - now.getTime()) / (1000 * 60);
			
				System.out.println("DayPlan.checkComplianceWith(): timeLeft (w/o route) = " + timeLeft);
			
			/* TODO: handle case where no route could be found */
			if (route.getNoRouteFound()) {
				throw new NoRouteFoundException("DayPlan.checkComplianceWith(): No route found");
			}
						
			/* be pessimistic and round up the duration of travel and 
			 * round down the gap between subsequent events */
			timeLeft = Math.floor(timeLeft) - Math.rint(route.getDurationOfTravel());
			
				System.out.println("DayPlan.checkComplianceWith(): timeLeft (with route) = " + timeLeft);
			
			return (int)timeLeft;
			
		} else {
			throw new RuntimeException("DayPlan.checkComplianceWith(): Event missing");
		}
	}
	
	
	/**
	 * checks if the calendar is self-consistent
	 * @return
	 */
	public DayPlanConsistency checkConsistency(Vehicle vehicle) {		
		if (routingEngine == null) {
			throw new RuntimeException("DayPlan.checkConsistency(): No routing engine defined");
		}
		
		DayPlanConsistency consistency = new DayPlanConsistency();
		
		Date pos = null;
		CalendarEvent event = null;
		CalendarEvent predecessor = null;
		
		/* TODO: include checking for overlap between events */
		
		/* iterate over all CalendarEvents in the calendar and check consistency  */
		while ((event = getNextEvent(pos)) != null) {
			if (predecessor != null) {				
				int timeLeft;				
				try {
					timeLeft = checkComplianceWith(predecessor.getEndDate(), 
							predecessor.getPlace(),	event, vehicle);
					/* add a collision, if time between two events is less
					 * than it will probably take to move from one event to the other */
					if (timeLeft < 0) {
						CalendarEventConflict conflict = 
							new CalendarEventCollision(predecessor, event, Math.abs(timeLeft));
						consistency.addConflict(conflict);
					}
				} catch (NoRouteFoundException e) {
					// TODO: add some kind of 'collision' indicating that no route could be found
				}									
			}			
			predecessor = event;
			pos = event.getEndDate();
		}
		
		return consistency;
	}
		

	
	public DayPlan optimize() {
		if (optimizer == null) {
			throw new RuntimeException("DayPlan.optimize(): No CalendarPlanOptimizer defined");
		}
		
		optimizer.setDayPlan(this);
		optimizer.setRoutingEngine(routingEngine);
				
		return optimizer.optimize();
	}
	
	
	
}
