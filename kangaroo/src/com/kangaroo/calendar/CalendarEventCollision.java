package com.kangaroo.calendar;

public class CalendarEventCollision extends CalendarEventConflict {

	
	private int lack;
	
	
	public CalendarEventCollision(CalendarEvent event1, CalendarEvent event2, int lack) {
		super(event1, event2, CONFLICT_PRIORITY_SOFT_COLLISION);
		this.lack = lack;
	}
	
	
	public int getLack() {
		return lack;
	}
	
	
	@Override
	public String toString() {
		return "CalendarEventCollision: {lacking "+ lack + " minutes}";
	}

}
