package com.kangaroo.calendar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CalendarConsistency {

	
	private Set<CalendarEventCollision> collisions;
	
	
	public CalendarConsistency() {
		super();
		this.collisions = new HashSet<CalendarEventCollision>();
	}
	
	
	public boolean isConsistent() {
		return (collisions.size() == 0);
	}
	
	
	public Set<CalendarEventCollision> getCollisions() {
		return collisions;
	}
	
	
	public void addCollision(CalendarEvent event, CalendarEvent predecessor, double timeLeft) {
		collisions.add(new CalendarEventCollision(event, predecessor, timeLeft));
	}
	
	
	
	
}
