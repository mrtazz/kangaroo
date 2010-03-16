package com.kangaroo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintHelper;
import com.kangaroo.task.TaskConstraintInterface;
import com.kangaroo.task.TaskPriorityComparator;
import com.mobiletsm.routing.GeoConstraints;
import com.mobiletsm.routing.NoRouteFoundException;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RouteParameter;
import com.mobiletsm.routing.RoutingEngine;

public class GreedyTaskInsertionOptimizer implements DayPlanOptimizer {

	
	private DayPlan originalDayPlan = null;
	
	
	private RoutingEngine routingEngine = null;
	
	
	/**
	 * returns true if day plan and routing engine are 
	 * set and routing engine is initialized, false otherwise
	 * @return
	 */
	private boolean initialized() {
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
		optimizedDayPlan.setEvents(originalDayPlan.getEvents());
		optimizedDayPlan.setRoutingEngine(routingEngine);
		optimizedDayPlan.setOptimizer(this);

		
		int timeLeft = 0;	
		CalendarEvent nextEvent = originalDayPlan.getNextEvent(now);
			System.out.println("nextEvent = " + nextEvent);
		if (nextEvent != null) {
			try {
				timeLeft = originalDayPlan.checkComplianceWith(now, here, nextEvent, vehicle);
			} catch (NoRouteFoundException e) {
				optimizedDayPlan.setTasks(originalDayPlan.getTasks());
				return optimizedDayPlan.optimize(nextEvent.getEndDate(), nextEvent.getPlace(), vehicle);
			}
		}

		
		/* get, sort and iterate over all tasks in active day plan */
		List<Task> tasksToHandle = new ArrayList<Task>(originalDayPlan.getTasks());
		Collections.sort(tasksToHandle, new TaskPriorityComparator());
		
		boolean taskSet = false;
		
		Iterator<Task> task_itr = tasksToHandle.iterator();
		while (task_itr.hasNext()) {
						
			Task task = task_itr.next();
			CalendarEvent taskAsEvent = null;			
			
			if (!taskSet) {
				TaskConstraintHelper constraintHelper = new TaskConstraintHelper(task);
				constraintHelper.setRoutingEngine(routingEngine);				
				
					System.out.println("GreedyTaskInsertionOptimizer.optimize(): analyzing task " + task.toString());
				
				int taskDuration = constraintHelper.getDuration();
				if (nextEvent == null || taskDuration <= timeLeft) {
					/* skip this task if its constraints forbid to execute it now
					 * TODO: checking task constraint consistency with now is not
					 * correct, because we have to check if the actual time and 
					 * duration is consistent with the task's constraints. */
					if (constraintHelper.isAllowed(now)) {
						
						GeoConstraints geoConstraints = null;
						if (nextEvent != null) {
							geoConstraints = new GeoConstraints(nextEvent.getPlace());
						}
						Place taskExecutionPlace = constraintHelper.getLocation(here, geoConstraints);	
						
						if (taskExecutionPlace != null) {
							
							if (nextEvent != null) {
								RouteParameter fromHereToTask = routingEngine.routeFromTo(here, taskExecutionPlace, vehicle);
								RouteParameter fromTaskToNextEvent = routingEngine.routeFromTo(taskExecutionPlace, nextEvent.getPlace(), vehicle);
								
								if (!fromHereToTask.getNoRouteFound() && !fromTaskToNextEvent.getNoRouteFound()) {
									int newTimeLeft = (int)(timeLeft - fromHereToTask.getDurationOfTravel() - 
											fromTaskToNextEvent.getDurationOfTravel() - taskDuration);
									
									if (newTimeLeft >= 0) {
										
										Date taskStartDate = new Date(now.getTime() + (int)(fromHereToTask.getDurationOfTravel() * 1000 * 60));
										taskAsEvent = new CalendarEvent(task, taskStartDate, taskExecutionPlace);
									}
								} else {
									System.out.println("GreedyTaskInsertionOptimizer.optimize(): SKIP this task! " +
										"unable to find routes from here to task and from task to next event");
								}
							}
								
						} else {
							/* task can be executed now and here */
							taskAsEvent = new CalendarEvent(task, now, here);
						}
						
					} else {
						System.out.println("GreedyTaskInsertionOptimizer.optimize(): SKIP this task! " +
								"constraints forbid to execute it now");
					}
				} else {
					System.out.println("GreedyTaskInsertionOptimizer.optimize(): SKIP this task! " +
							"timeLeft = " +	timeLeft + ", duration = " + taskDuration);
				}
			}
		
			if (taskAsEvent != null) {
				/* task was set as event */
				optimizedDayPlan.addEvent(taskAsEvent);
				now = taskAsEvent.getEndDate();
				here = taskAsEvent.getPlace();
				
				taskSet = true;
				
				System.out.println("GreedyTaskInsertionOptimizer.optimize(): SET this task! " +
						task.toString() + " at " + taskAsEvent.getPlace().toString());
			} else {
				/* this task could not be handled */
				optimizedDayPlan.addTask(task);
			}			
		}
		
		if (nextEvent != null && optimizedDayPlan.getNumberOfTasks() > 0) {
			if (taskSet) {
				return optimizedDayPlan.optimize(now, here, vehicle);				
			} else {
				return optimizedDayPlan.optimize(nextEvent.getEndDate(), nextEvent.getPlace(), vehicle);	
			}
		} else {
			return optimizedDayPlan;//.optimize(now, here, vehicle);
		}
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
