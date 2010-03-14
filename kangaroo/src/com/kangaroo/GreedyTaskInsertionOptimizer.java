package com.kangaroo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintDuration;
import com.kangaroo.task.TaskConstraintInterface;
import com.mobiletsm.routing.NoRouteFoundException;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RouteParameter;
import com.mobiletsm.routing.RoutingEngine;

public class GreedyTaskInsertionOptimizer implements DayPlanOptimizer {

	
	private DayPlan originalDayPlan = null;
	
	
	private RoutingEngine routingEngine = null;
	
	
	
	private boolean initialized() {
		/* return true if day plan and routing engine are 
		 * set and routing engine is initialized, false otherwise */
		return (originalDayPlan != null && routingEngine != null && routingEngine.initialized());
	}
	
	
	@Override
	public DayPlan optimize(Date now, Place here, Object vehicle) {
		
		/* check if resources are ready */
		if (!initialized()) {
			throw new RuntimeException("GreedyTaskInsertionOptimizer.optimize(): resources " +
					"(day plan and/or routing engine) not ready");
		}
				
		/* initialize new day plan with events of original day plan */
		DayPlan optimizedDayPlan = new DayPlan();
		List<CalendarEvent> dayPlanEvents = originalDayPlan.getEvents();
		optimizedDayPlan.setEvents(dayPlanEvents);
		
		
		/* cases:
		 * - 
		 * 
		 */
		
		Integer timeGapToNextEvent = null;
		CalendarEvent nextEvent = originalDayPlan.getNextEvent(now);		
		if (nextEvent != null) {
			try {
				timeGapToNextEvent = new Integer(originalDayPlan.checkComplianceWith(
						now, here, nextEvent, vehicle));
			} catch (NoRouteFoundException e) {
				return null;
			}			
		}	
		
		/* get, sort and iterate over all tasks in active day plan */
		List<Task> tasksToHandle = new ArrayList<Task>();
		tasksToHandle.addAll(originalDayPlan.getTasks());
		Collections.sort(tasksToHandle, new TaskPriorityComparator());
		Iterator<Task> task_itr = tasksToHandle.iterator();

		while (task_itr.hasNext()) {
						
			Task task = task_itr.next();
			CalendarEvent taskAsEvent = null;
			
			System.out.println("GreedyTaskInsertionOptimizer.optimize(): analyzing task " + task.toString());
			
			TaskConstraintHelper constraintHelper = new TaskConstraintHelper(task);
			
			/* skip this task if task takes more time than left till next event */		
			int taskDuration = constraintHelper.getDuration();
			if (timeGapToNextEvent != null && taskDuration > timeGapToNextEvent.intValue()) {
				System.out.println("GreedyTaskInsertionOptimizer.optimize(): SKIP! gap = " + 
						timeGapToNextEvent + ", duration = " + taskDuration);
				continue;
			}

			/* skip this task if its constraints forbid to execute it now */
			if (!constraintHelper.isAllowed(now)) {
				System.out.println("GreedyTaskInsertionOptimizer.optimize(): SKIP! cannot be done now");
				continue;
			}
			
			/* get nearest location that is consistent with the task's constraints */
			Place place = constraintHelper.getLocation(routingEngine, here);
			
			
			if (place != null) {
				
				System.out.println("GreedyTaskInsertionOptimizer.optimize(): place = " + place.getOsmNodeId());
				
				/* check if suggestion is consistent with next event
				 * TODO: this should be skipped if there is no next event */
				RouteParameter fromHereToTask = routingEngine.routeFromTo(here, place, vehicle);
				RouteParameter fromTaskToNextEvent = routingEngine.routeFromTo(place, nextEvent.getPlace(), vehicle);
				
				System.out.println("GreedyTaskInsertionOptimizer.optimize(): fromHereToTask = " + fromHereToTask);	
				System.out.println("GreedyTaskInsertionOptimizer.optimize(): fromTaskToNextEvent = " + fromTaskToNextEvent);
				
				if (fromHereToTask.getNoRouteFound() || fromTaskToNextEvent.getNoRouteFound()) {
					continue;
				}
				
				if (timeGapToNextEvent == null) {
					continue;
				}
				
				int timeLeft = (int)(timeGapToNextEvent - fromHereToTask.getDurationOfTravel() - fromTaskToNextEvent.getDurationOfTravel() - taskDuration);
				
				if (timeLeft > 0) {
					System.out.println(" set task " + task.toString() + " at " + place.toString());
				}
				
			} else {
				/* the task has no location constraints and may be done now */
				
				System.out.println("GreedyTaskInsertionOptimizer.optimize(): SKIP! no location");
			}		
			
		
			if (taskAsEvent != null) {
				optimizedDayPlan.addEvent(taskAsEvent);
			} else {
				optimizedDayPlan.addTask(task);
			}			
		}
		
		return optimizedDayPlan;
	}
	

	@Override
	public Set<DayPlan> optimize(Date now, Place here, Object vehicle, int suggestions) {
		return null;
	}
	

	/**
	 * @param dayPlan the dayPlan to set
	 */
	public void setDayPlan(DayPlan dayPlan) {
		this.originalDayPlan = dayPlan;
	}


	@Override
	public void setRoutingEngine(RoutingEngine routingEngine) {
		this.routingEngine = routingEngine;
	}

}
