package com.kangaroo.calendar;


public abstract class CalendarEventConflict {


	public static int CONFLICT_PRIORITY_UNDEFINED = -1;
	
	
	public static int CONFLICT_PRIORITY_HARD_COLLISION = 1;
	
	
	public static int CONFLICT_PRIORITY_SOFT_COLLISION = 2;
	
	
	public static int CONFLICT_PRIORITY_WARNING = 3;
	
	
	protected CalendarEvent event1;
	

	protected CalendarEvent event2;
	
	
	protected int conflictPriority = CONFLICT_PRIORITY_UNDEFINED;
	
		
	public CalendarEvent getEvent1() {
		return event1;
	}
	
	
	public CalendarEvent getEvent2() {
		return event2;
	}
	
	
	public CalendarEventConflict(CalendarEvent event1, CalendarEvent event2, int conflictPriority) {
		super();
		this.event1 = event1;
		this.event2 = event2;
		this.conflictPriority = conflictPriority;
	}
	
	
	public abstract String toString();
	
	
}
