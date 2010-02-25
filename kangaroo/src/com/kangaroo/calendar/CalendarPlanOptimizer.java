package com.kangaroo.calendar;

import java.util.Set;

import com.mobiletsm.routing.RoutingEngine;

public interface CalendarPlanOptimizer {

	
	public void setCalendarPlan(CalendarPlan plan);
	
	
	public void setRoutingEngine(RoutingEngine routingEngine);
	
	
	public CalendarPlan optimize();
	
	
	public Set<CalendarPlan> optimize(int suggestions);
	
	
}
