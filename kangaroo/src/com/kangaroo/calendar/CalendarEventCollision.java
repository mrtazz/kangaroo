package com.kangaroo.calendar;

public class CalendarEventCollision {

	
	private CalendarEvent event;
	
	
	private CalendarEvent predecessor;
	
	
	private double timeLeft;
	
		
	public CalendarEvent getEvent() {
		return event;
	}
	
	
	public CalendarEvent getPredecessor() {
		return predecessor;
	}
	
	
	public double getTimeLeft() {
		return timeLeft;
	}
	
	
	public CalendarEventCollision(CalendarEvent event, CalendarEvent predecessor, double timeLeft) {
		super();
		this.event = event;
		this.predecessor = predecessor;
		this.timeLeft = timeLeft;
	}
	
	
}
