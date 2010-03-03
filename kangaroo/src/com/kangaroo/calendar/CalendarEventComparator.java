package com.kangaroo.calendar;

import java.util.Comparator;


public class CalendarEventComparator implements	Comparator<CalendarEvent> {
	

	public static int START_DATE = 1;
	
	
	public static int END_DATE = 2;
	
	
	private int sortBy;
	
	
	
	public CalendarEventComparator(int sortBy) {
		super();
		this.sortBy = sortBy;
	}
	
	
	public CalendarEventComparator() {
		this(START_DATE);
	}
	
	
	@Override
	public int compare(CalendarEvent event1, CalendarEvent event2) {
		
		if (sortBy == START_DATE) {
			return event1.getStartDate().compareTo(event2.getStartDate());
		} else if (sortBy == END_DATE) {
			return event1.getEndDate().compareTo(event2.getEndDate());
		} else {
			throw new RuntimeException("CalendarEventComparator.compare(): unknown field to sort by");
		}
	}

}
