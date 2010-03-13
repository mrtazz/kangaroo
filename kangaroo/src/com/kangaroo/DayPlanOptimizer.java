package com.kangaroo;

import java.util.Date;
import java.util.Set;

import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RoutingEngine;

public interface DayPlanOptimizer {

	
	public void setDayPlan(DayPlan plan);
	
	
	public void setRoutingEngine(RoutingEngine routingEngine);
	
	
	public DayPlan optimize(Date now, Place here, Object vehicle);
	
	
	public Set<DayPlan> optimize(Date now, Place here, Object vehicle, int suggestions);
	
	
}
