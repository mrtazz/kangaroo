package com.kangaroo;

import java.util.Set;

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
		
		
		
		return null;
	}
	

	@Override
	public Set<DayPlan> optimize(int suggestions) {
		return null;
	}
	

	@Override
	public void setDayPlan(DayPlan plan) {
		this.dayPlan = dayPlan;
	}
	

	@Override
	public void setRoutingEngine(RoutingEngine routingEngine) {
		this.routingEngine = routingEngine;
	}

}
