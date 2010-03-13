package com.kangaroo.calendar.conflicts;

import com.kangaroo.calendar.CalendarEvent;


public abstract class CalendarEventConflict {


	public static int CONFLICT_PRIORITY_UNDEFINED = -1;
	
	
	public static int CONFLICT_PRIORITY_HARD_COLLISION = 1;
	
	
	public static int CONFLICT_PRIORITY_SOFT_COLLISION = 2;
	
	
	public static int CONFLICT_PRIORITY_WARNING = 3;
	
	
	protected CalendarEvent event;
	

	protected CalendarEvent coEvent = null;
	
	
	protected int conflictPriority = CONFLICT_PRIORITY_UNDEFINED;
	
		
	public CalendarEvent getEvent() {
		return event;
	}
	
	
	public CalendarEvent getCoEvent() {
		return coEvent;
	}
	
	
	public CalendarEventConflict(CalendarEvent event) {
		super();
		this.event = event;
	}
	
	
	public CalendarEventConflict(CalendarEvent event, int conflictPriority) {
		super();
		this.event = event;
		this.conflictPriority = conflictPriority;
	}
	
	
	public CalendarEventConflict(CalendarEvent event, CalendarEvent coEvent, int conflictPriority) {
		super();
		this.event = event;
		this.coEvent = coEvent;
		this.conflictPriority = conflictPriority;
	}
	
	
	public abstract String toString();
	
	
}
