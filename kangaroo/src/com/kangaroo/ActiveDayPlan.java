package com.kangaroo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.calendar.CalendarEventComparator;
import com.kangaroo.task.Task;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RouteParameter;
import com.mobiletsm.routing.RoutingEngine;
import com.mobiletsm.routing.Vehicle;

public class ActiveDayPlan implements DayPlan {
	

	private List<CalendarEvent> events;
	
	
	private Collection<Task> tasks;
	
	
	private DayPlanOptimizer optimizer = null;
	
	
	private RoutingEngine routingEngine = null;
	
	
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
	
	
	@Override
	public List<CalendarEvent> getEvents() {
		return events;
	}

	
	@Override
	/**
	 * return the event in the calendar that is next starting from given Date
	 * @param now
	 * @return
	 */
	public CalendarEvent getNextEvent(Date now) {
		
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
		if (destinationEvent == null) {
			destinationEvent = this.getNextEvent(now);
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
	public boolean sync() {
		throw new UnsupportedOperationException("ActiveCalendarPlan.sync(): operation not yet supported");
	}
}
