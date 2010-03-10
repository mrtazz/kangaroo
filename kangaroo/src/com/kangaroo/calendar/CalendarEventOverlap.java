package com.kangaroo.calendar;

public class CalendarEventOverlap extends CalendarEventConflict {

	
	private int overlap;
	
	
	public CalendarEventOverlap(CalendarEvent event1, CalendarEvent event2, int overlap) {
		super(event1, event2, CONFLICT_PRIORITY_HARD_COLLISION);
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
