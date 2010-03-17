package com.kangaroo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.task.NoLocationFoundException;
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
				
		System.out.println("GreedyTaskInsertionOptimizer.optimize(): starting (now = " + 
				now.toString() + ", here = " + here.toString() + ") ...");
		
		/* initialize new day plan with events of original day plan */
		DayPlan optimizedDayPlan = new DayPlan();
		optimizedDayPlan.setEvents(originalDayPlan.getEvents());
		optimizedDayPlan.setRoutingEngine(routingEngine);
		optimizedDayPlan.setOptimizer(this);

		
		int timeLeftToWait = 0;
		
		CalendarEvent nextEvent = originalDayPlan.getNextEvent(now);
			System.out.println("nextEvent = " + nextEvent);
		if (nextEvent != null) {
			try {
				timeLeftToWait = originalDayPlan.checkComplianceWith(now, here, nextEvent, vehicle);
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
				
				/* how long does the task take to be executed */ 
				int taskDuration = constraintHelper.getDuration();
				
					System.out.println("GreedyTaskInsertionOptimizer.optimize(): INFO: " +
							"timeLeftToWait = " +	timeLeftToWait + ", duration = " + taskDuration);
				
				/* check if there is enough time to insert this task before
				 * the next event (this is a heuristic approach)
				 * TODO: this may reject tasks that can be executed between
				 * now and the next event anyhow (think of tasks that can
				 * be executed while moving towards the next event) */
				if (nextEvent == null || taskDuration <= timeLeftToWait) {
					
					/* skip this task if its constraints forbid to execute it now
					 * TODO: checking task constraint consistency with now is not
					 * correct, because we have to check if the actual time and 
					 * duration is consistent with the task's constraints. */
					if (constraintHelper.isAllowed(now)) {
						
						/* find a location to execute the current task */
						GeoConstraints geoConstraints = null;
						if (nextEvent != null) {
							geoConstraints = new GeoConstraints(nextEvent.getPlace());
						}
						Place taskExecutionPlace;
						try {
							taskExecutionPlace = constraintHelper.getLocation(here, geoConstraints);
							
							/*  */
							if (taskExecutionPlace != null) {

								/*  */
								RouteParameter fromHereToTask = routingEngine.routeFromTo(here, taskExecutionPlace, vehicle);
								
								if (nextEvent != null) {
									
									RouteParameter fromTaskToNextEvent = 
										routingEngine.routeFromTo(taskExecutionPlace, nextEvent.getPlace(), vehicle);
									
									if (!fromHereToTask.getNoRouteFound() && !fromTaskToNextEvent.getNoRouteFound()) {
										/* calculate the time left until next event */
										int timeLeftUntilNextEvent = 
											(int)Math.ceil((nextEvent.getStartDate().getTime() - now.getTime()) / (1000 * 60));
											
										/* calculate the time left if current task is executed before the next event */
										int newTimeLeft = (int)(timeLeftUntilNextEvent - fromHereToTask.getDurationOfTravel() - 
												fromTaskToNextEvent.getDurationOfTravel() - taskDuration);
										
										/* check if there is enough time to execute the task
										 * TODO: allow time buffer */
										if (newTimeLeft >= 0) {										
											/* set the start date/time of the event to the time of estimated 
											 * arrival time at the tasks execution location
											 * TODO: use a time buffer */
											Date taskStartDate = new Date(now.getTime() + 
													(int)Math.ceil(fromHereToTask.getDurationOfTravel()) * 1000 * 60);
											taskAsEvent = new CalendarEvent(task, taskStartDate, taskExecutionPlace);
										}
									} else {
										System.out.println("GreedyTaskInsertionOptimizer.optimize(): SKIP this task! " +
											"unable to find routes from here to task and/or from task to next event");
									}
									
								} else {								
									/* there is no event chronologically succeeding the task */
									
									if (!fromHereToTask.getNoRouteFound()) {
										/*  */
										Date taskStartDate = new Date(now.getTime() + 
												(int)Math.ceil(fromHereToTask.getDurationOfTravel()) * 1000 * 60);
										taskAsEvent = new CalendarEvent(task, taskStartDate, taskExecutionPlace);
									} else {
										System.out.println("GreedyTaskInsertionOptimizer.optimize(): SKIP this task! " +
											"unable to find a route from here to task");
									}
									
								}
									
							} else {
								/* task has no location constraints and can 
								 * be executed everywhere, so do it here
								 * TODO: this assumes the task will be executed
								 * while 'standing' at the point 'here'. For a
								 * task without any location constraints, this
								 * may in principle be done while moving towards
								 * the next event */
								taskAsEvent = new CalendarEvent(task, now, here);
							}
							
						} catch (NoLocationFoundException e) {
							/* cannot find an execution location for this task */
							
							System.out.println("GreedyTaskInsertionOptimizer.optimize(): SKIP this task! " +
								"cannot find an execution location for this task");
						}							
						
					} else {
						/* the task cannot be executed now, because date and/or
						 * daytime constraints forbid its execution. */
						
						System.out.println("GreedyTaskInsertionOptimizer.optimize(): SKIP this task! " +
								"constraints forbid to execute it now");
					}
				} else {
					/* executing this task  */
					
					System.out.println("GreedyTaskInsertionOptimizer.optimize(): SKIP this task! " +
							"timeLeftToWait = " +	timeLeftToWait + ", duration = " + taskDuration);
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
				/* recursive optimization
				 * TODO: include a maximal recursion depth */
				return optimizedDayPlan.optimize(now, here, vehicle);				
			} else {
				/* recursive optimization
				 * TODO: include a maximal recursion depth */
				return optimizedDayPlan.optimize(nextEvent.getEndDate(), nextEvent.getPlace(), vehicle);	
			}
		} else {
			/* there no task left to set or there is no chronologically
			 * succeeding event constraining optimization */
			
			if (optimizedDayPlan.getNumberOfTasks() > 0 && taskSet) {
				/* nextEvent = null */
				
				/* there are still some tasks left that may be set,
				 * use recursive optimization
				 * TODO: include a maximal recursion depth */
				return optimizedDayPlan.optimize(now, here, vehicle);
			} else {
				/* there is nothing to optimize anymore, because
				 * either there are no more tasks to set or none
				 * of the remaining tasks could be set */
				return optimizedDayPlan;
			}
		}
	}
	

	@Override
	public Set<DayPlan> optimize(Date now, Place here, Object vehicle, int suggestions) {
		throw new UnsupportedOperationException("GreedyTaskInsertionOptimizer.optimize(): Operation not yet supported");
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
