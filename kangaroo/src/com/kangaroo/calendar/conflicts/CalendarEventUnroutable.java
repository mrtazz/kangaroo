package com.kangaroo.calendar.conflicts;

import com.kangaroo.calendar.CalendarEvent;

public class CalendarEventUnroutable extends CalendarEventConflict {

	
	public CalendarEventUnroutable(CalendarEvent event, CalendarEvent coEvent) {
		super(event, coEvent, CONFLICT_PRIORITY_WARNING);
	}
	
	
	@Override
	public String toString() {
		return "CalendarEventUnroutable: {}";
	}
	

}
