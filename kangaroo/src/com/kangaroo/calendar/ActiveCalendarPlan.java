package com.kangaroo.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.kangaroo.task.Task;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RouteParameter;
import com.mobiletsm.routing.RoutingEngine;
import com.mobiletsm.routing.Vehicle;

public class ActiveCalendarPlan implements CalendarPlan {

	private List<CalendarEvent> events;
	
	
	private Collection<Task> tasks;
	
	
	private CalendarPlanOptimizer optimizer = null;
	
	
	private RoutingEngine routingEngine = null;
	
	
	public ActiveCalendarPlan() {
		super();
		events = new ArrayList<CalendarEvent>();
		tasks = new ArrayList<Task>();
	}
	
	
	public ActiveCalendarPlan(CalendarPlan plan) {
		this();
		events.addAll(plan.getEvents());
		tasks.addAll(plan.getTasks());
	}
	
	
	@Override
	public List<CalendarEvent> getEvents() {
		return events;
	}

	
	@Override
	public CalendarEvent getNextEvent(Date now) {
		// TODO Auto-generated method stub
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
			throw new RuntimeException("ActiveCalendarPlan.setEvents(): null reference given");
		}
	}

	
	@Override
	public void setTasks(Collection<Task> tasks) {
		if (tasks != null) {
			this.tasks = tasks;
		} else {
			throw new RuntimeException("ActiveCalendarPlan.setTasks(): null reference given");
		}
	}

	
	/**
	 * return the number of minutes left to start moving towards the next event
	 * @param now
	 * @param currentPlace
	 * @param vehicle
	 * @return
	 */
	public int checkComplianceWith(Date now, Place currentPlace, Vehicle vehicle) {
		CalendarEvent nextEvent = this.getNextEvent(now);
		
		// TODO: use specific exceptions
		
		if (nextEvent != null) {
			if (routingEngine == null) {
				throw new RuntimeException("ActiveCalendarPlan.checkComplianceWith(): No RoutingEngine defined");
			}
			
			Place eventPlace = nextEvent.getPlace();
			
			if (eventPlace == null) {
				eventPlace = new Place(nextEvent.getLocationLatitude(), nextEvent.getLocationLongitude());
				nextEvent.setPlace(eventPlace);
			}
		
			RouteParameter route = routingEngine.routeFromTo(currentPlace, eventPlace, vehicle);
		
			double timeLeft = (nextEvent.getStartDate().getTime() - now.getTime()) / (1000 * 60);
			
			if (route.getNoRouteFound()) {
				throw new RuntimeException("ActiveCalendarPlan.checkComplianceWith(): No route found");
			}
						
			timeLeft = timeLeft - route.getDurationOfTravel();
			
			return (int)timeLeft;
			
		} else {
			throw new RuntimeException("ActiveCalendarPlan.checkComplianceWith(): Event missing");
		}
	}
	
	
	public boolean checkConsistency() {
		throw new UnsupportedOperationException("ActiveCalendarPlan.checkConsistency(): operation not yet supported");
	}
	
	
	public CalendarPlan optimize() {
		if (optimizer == null) {
			throw new RuntimeException("ActiveCalendarPlan.optimize(): No CalendarPlanOptimizer defined");
		}
		
		optimizer.setCalendarPlan(this);
		optimizer.setRoutingEngine(routingEngine);
				
		return null;
	}
	
	
	public void setOptimizer(CalendarPlanOptimizer optimizer) {
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
