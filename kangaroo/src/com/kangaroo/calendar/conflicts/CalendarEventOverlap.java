package com.kangaroo.calendar.conflicts;

import com.kangaroo.calendar.CalendarEvent;

public class CalendarEventOverlap extends CalendarEventConflict {

	
	private int overlap;
	
	
	public CalendarEventOverlap(CalendarEvent event, CalendarEvent coEvent, int overlap) {
		super(event, coEvent, CONFLICT_PRIORITY_HARD_COLLISION);
		this.overlap = overlap;
	}
	
	
	public int getOverlap() {
		return overlap;
	}
	
	
	@Override
	public String toString() {
		return "CalendarEventOverlap: {overlap of "+ overlap + " minutes}";
	}

}
