package com.kangaroo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.calendar.CalendarEventComparator;
import com.kangaroo.calendar.conflicts.CalendarEventCollision;
import com.kangaroo.calendar.conflicts.CalendarEventConflict;
import com.kangaroo.calendar.conflicts.CalendarEventOverlap;
import com.kangaroo.calendar.conflicts.CalendarEventUnroutable;
import com.kangaroo.task.Task;
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


	private int minimalTimeLeft = 0;
	
	
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
	

	public int getNumberOfEvents() {
		return events.size();
	}
	
	
	public int getNumberOfTasks() {
		return tasks.size();
	}
	
	
	public void addEvent(CalendarEvent event) {
		events.add(event);
	}
	
	
	public void addTask(Task task) {
		tasks.add(task);
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
		while (itr.hasNext()) {
			CalendarEvent event = itr.next();
			Date startDate = event.getStartDate();
			/* ignore calendar event that do not specify a start date */
			if (startDate != null && startDate.compareTo(now) > 0) {
				if (result == null) {
					result = event;
				} else if (result.getStartDate().compareTo(startDate) == 0) {
					/* there are at least two different events with equal start date
					 * TODO: should better not be a RuntimeException */
					throw new RuntimeException("DayPlan.getNextEvent(): " +
							"There are two different events with equal start date"); 
				}
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
		
		/* initialEvent has to specify an end date */
		if (initialEvent.getEndDate() == null) {
			throw new MissingParameterException("DayPlan.checkComplianceBetween(): No end date given");
		}
		
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
			/* we need the routing engine to be ready */
			if (routingEngine == null || !routingEngine.initialized()) {
				throw new RuntimeException("DayPlan.checkComplianceWith(): Routing engine not ready");
			}
			
			/* we need the current time */
			if (now == null) {
				throw new MissingParameterException("DayPlan.checkComplianceWith(): No current time given");
			}
			
			/* we need the current position */
			if (here == null) {
				throw new MissingParameterException("DayPlan.checkComplianceWith(): No current position given");
			}
			
			/* destinationEvent has to specify a start date */
			if (destinationEvent.getStartDate() == null) {
				throw new MissingParameterException("DayPlan.checkComplianceWith(): " +
						"destinationEvent does not specify a start date");
			}
			
			/* destinationEvent has to specify a place */
			if (destinationEvent.getPlace() == null) {
				throw new MissingParameterException("DayPlan.checkComplianceWith(): " +
						"destinationEvent does not specify a location (Place)");
			}
			
				System.out.println("DayPlan.checkComplianceWith(): route from " + here.toString() + 
						" to " + destinationEvent.getPlace().toString());
			
			RouteParameter route = routingEngine.routeFromTo(here, destinationEvent.getPlace(), vehicle);
		
				System.out.println("DayPlan.checkComplianceWith(): " + route.toString());
				
			/* not finding any route should be an exotic case */
			if (route.getNoRouteFound()) {
				throw new NoRouteFoundException("DayPlan.checkComplianceWith(): No route found");
			}
			
			/* Date.getTime() returns time in milliseconds, so 
			 * we have to divide by 1000*60 to get minutes */
			int timeLeft = (int)Math.ceil((destinationEvent.getStartDate().getTime() - now.getTime()) / (1000 * 60));
			
				System.out.println("DayPlan.checkComplianceWith(): timeLeft (w/o route) = " + timeLeft);
						
			/* be pessimistic and round up the duration of travel and 
			 * round down the gap between subsequent events */
			/* TODO: Math.rint() does not always round up */
			timeLeft = timeLeft - (int)Math.ceil(route.getDurationOfTravel());
			
				System.out.println("DayPlan.checkComplianceWith(): timeLeft (with route) = " + timeLeft);
			
			return (int)timeLeft;
			
		} else {
			/* if there is no event to be compliant with */
			return Integer.MAX_VALUE;
		}
	}
	
		
	/**
	 * @return the minimalTimeLeft
	 */
	public int getMinimalTimeLeft() {
		return minimalTimeLeft;
	}


	/**
	 * @param minimalTimeLeft the minimalTimeLeft to set
	 */
	public void setMinimalTimeLeft(int minimalTimeLeft) {
		this.minimalTimeLeft = minimalTimeLeft;
	}


	/**
	 * checks if the calendar is self-consistent
	 * @return
	 */
	public DayPlanConsistency checkConsistency(Vehicle vehicle, Date now) {		
				
		DayPlanConsistency consistency = new DayPlanConsistency();
		
		Date pos = now;
		CalendarEvent event;
		CalendarEvent predecessor = null;
		
		/* iterate over all CalendarEvents in the calendar and check consistency  */
		while ((event = getNextEvent(pos)) != null) {
			
			/* events have to specify an end date
			 * TODO: one can also think of considering the case of an undefined end
			 * date in a calendar event as an event of zero duration. This would imply
			 * using its start date has an effective end date. */
			if (event.getEndDate() == null) {
				throw new MissingParameterException("DayPlan.checkConsistency(): No end date given");
			}
			
			/* at this point we can be sure that event has a start and an end date
			 * (because getNextEvent() only accounts for events with a start date) */

			if (predecessor != null) {
				
				/* make sure these both events do not overlap */
				if (event.getStartDate().compareTo(predecessor.getEndDate()) >= 0) {
					
					/* ignore events that do not specify a location */
					if (predecessor.hasLocation() && event.hasLocation()) {
						try {
							/* check compliance of the event with end date and place
							 * of preceding calendar event (predecessor)  */
							int timeLeft = checkComplianceWith(predecessor.getEndDate(), 
									predecessor.getPlace(), event, vehicle);
	
							/* add a collision, if time between two events is less
							 * than it will probably take to move from one event to the other */
							if (timeLeft < minimalTimeLeft) {
								CalendarEventConflict conflict = new CalendarEventCollision(
										event, predecessor, Math.abs(timeLeft));
								consistency.addConflict(conflict);
							}
	
						} catch (NoRouteFoundException e) {
							/* not finding any route between two events is  
							 * considered as a CalendarEventConflict  */
							CalendarEventConflict conflict = new CalendarEventUnroutable(
									event, predecessor);
							consistency.addConflict(conflict);
						}
					} 
				} else {
					/* there is an overlap of these both events, calculate overlap in minutes */
					int overlap = (int)Math.ceil((predecessor.getEndDate().getTime() - 
							event.getStartDate().getTime()) / (1000 * 60));
					
					CalendarEventConflict conflict = new CalendarEventOverlap(event, predecessor, overlap);
					consistency.addConflict(conflict);
				}
			}			
			
			predecessor = event;
			pos = event.getStartDate();
		}
		
		return consistency;
	}
		

	/**
	 * try to optimize this day plan using the previously set DayPlanOptimizer
	 * @param now
	 * @param here
	 * @param vehicle TODO
	 * @return
	 */
	public DayPlan optimize(Date now, Place here, Object vehicle) {
		if (optimizer == null) {
			throw new RuntimeException("DayPlan.optimize(): No DayPlanOptimizer defined");
		}
		
		optimizer.setDayPlan(this);
		optimizer.setRoutingEngine(routingEngine);
				
		return optimizer.optimize(now, here, vehicle);
	}
	
	
	
	@Override
	public String toString() {
		
		if (events.size() > 0 || tasks.size() > 0) {
			StringBuffer buf = new StringBuffer("DayPlan: {# events = " + events.size() + 
					", # tasks = " + tasks.size() + ":\n");
			
			/* make sure the events are in correct order */
			Collections.sort(events, new CalendarEventComparator(CalendarEventComparator.START_DATE));
			
			Iterator<CalendarEvent> event_itr = events.iterator();
			Iterator<Task> task_itr = tasks.iterator();
			
			while (event_itr.hasNext()) {
				buf.append("    " + event_itr.next().toString());
				if (event_itr.hasNext() || task_itr.hasNext()) {
					buf.append(",\n");
				} else {
					buf.append("\n");
				}
			}
			
			while (task_itr.hasNext()) {
				buf.append("    " + task_itr.next().toString());
				if (task_itr.hasNext()) {
					buf.append(",\n");
				} else {
					buf.append("\n");
				}
			}
			
			buf.append("}");
			
			return buf.toString();			
		} else {
			return "DayPlan: {no tasks or events}";
		}
	}
	
}
