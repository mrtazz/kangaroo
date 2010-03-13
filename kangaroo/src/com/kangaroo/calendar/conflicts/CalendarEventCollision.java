package com.kangaroo.calendar.conflicts;

import com.kangaroo.calendar.CalendarEvent;

public class CalendarEventCollision extends CalendarEventConflict {

	
	private int lack;
	
	
	public CalendarEventCollision(CalendarEvent event, CalendarEvent coEvent, int lack) {
		super(event, coEvent, CONFLICT_PRIORITY_SOFT_COLLISION);
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
