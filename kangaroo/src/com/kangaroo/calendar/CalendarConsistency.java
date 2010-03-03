package com.kangaroo.calendar;

import java.util.HashSet;
import java.util.Set;

public class CalendarConsistency {

	/**
	 * set of calendar collisions
	 */
	private Set<CalendarEventCollision> collisions;
	
	
	public CalendarConsistency() {
		super();
		this.collisions = new HashSet<CalendarEventCollision>();
	}
	
	
	/**
	 * returns true if no collisions are known, false otherwise
	 * @return true if no collisions are known, false otherwise
	 */
	public boolean isConsistent() {
		return (collisions.size() == 0);
	}
	
	
	/**
	 * returns the set of calendar collisions
	 * @return set of calendar collisions
	 */
	public Set<CalendarEventCollision> getCollisions() {
		return collisions;
	}
	
	
	/**
	 * adds a new calendar collision to the set
	 * @param event
	 * @param predecessor
	 * @param timeLeft
	 */
	public void addCollision(CalendarEvent event, CalendarEvent predecessor, double timeLeft) {
		collisions.add(new CalendarEventCollision(event, predecessor, timeLeft));
	}
	
	
	/**
	 * adds a new calendar collision to the set
	 * @param collision
	 */
	public void addCollision(CalendarEventCollision collision) {
		collisions.add(collision);
	}
	
	
	
	
}
