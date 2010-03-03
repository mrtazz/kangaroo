package com.kangaroo;

import java.util.Set;

import com.mobiletsm.routing.RoutingEngine;

public interface DayPlanOptimizer {

	
	public void setCalendarPlan(DayPlan plan);
	
	
	public void setRoutingEngine(RoutingEngine routingEngine);
	
	
	public DayPlan optimize();
	
	
	public Set<DayPlan> optimize(int suggestions);
	
	
}
