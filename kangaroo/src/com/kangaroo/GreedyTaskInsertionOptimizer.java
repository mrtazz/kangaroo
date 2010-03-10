package com.kangaroo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintInterface;
import com.mobiletsm.routing.RoutingEngine;

public class GreedyTaskInsertionOptimizer implements DayPlanOptimizer {

	
	private DayPlan dayPlan = null;
	
	
	private RoutingEngine routingEngine = null;
	
	
	
	private boolean initialized() {
		/* return true if day plan and routing engine are 
		 * set and routing engine is initialized, false otherwise */
		return (dayPlan != null && routingEngine != null && routingEngine.initialized());
	}
	
	
	@Override
	public DayPlan optimize() {
		
		/* check if resources are ready */
		if (!initialized()) {
			throw new RuntimeException("GreedyTaskInsertionOptimizer.optimize(): resources " +
					"(day plan and/or routing engine) not ready");
		}
		
		/* list of tasks to check */
		List<Task> tasksToCheck = new ArrayList<Task>();
		tasksToCheck.addAll(dayPlan.getTasks());
		
		DayPlan optimizedDayPlan = new DayPlan(dayPlan);
		
		while (tasksToCheck.size() > 0) {
			/* get one task from list */
			Task task = tasksToCheck.get(0);
			
			/*
			List<TaskConstraintInterface> durationConstraint = 
				task.getConstraintsOfType(TaskConstraintInterface.TYPE_DURATION);
			*/
			
			
			
			
		}
		
		return null;
	}
	

	@Override
	public Set<DayPlan> optimize(int suggestions) {
		return null;
	}
	

	/**
	 * @param dayPlan the dayPlan to set
	 */
	public void setDayPlan(DayPlan dayPlan) {
		this.dayPlan = dayPlan;
	}


	@Override
	public void setRoutingEngine(RoutingEngine routingEngine) {
		this.routingEngine = routingEngine;
	}

}
