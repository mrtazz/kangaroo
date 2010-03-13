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
			
			TaskConstraintHelper constraintHelper = new TaskConstraintHelper(task);
			
			/* skip this task if task takes more time than left till next event */		
			Integer taskDuration = constraintHelper.getDuration();
			if (taskDuration != null && timeGapToNextEvent != null && 
					taskDuration.intValue() > timeGapToNextEvent.intValue()) {
				continue;
			}

			/* skip this task if  */
			if (!constraintHelper.isAllowed(now)) {
				continue;
			}
			
			
			
			List<TaskConstraintInterface> locationConstraint = 
				task.getConstraintsOfType(TaskConstraintInterface.TYPE_LOCATION);
			
			
			List<TaskConstraintInterface> poiConstraint = 
				task.getConstraintsOfType(TaskConstraintInterface.TYPE_POI);
			

			if (locationConstraint.size() == 0) {
			
			} else {
				
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
