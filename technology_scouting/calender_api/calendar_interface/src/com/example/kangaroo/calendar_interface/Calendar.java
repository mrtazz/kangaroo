/**
 * 
 */
package com.example.kangaroo.calendar_interface;

import com.example.kangaroo.calendar_interface.CalendarEvent;

/**
 * @author mrtazz
 * 
 * class representing a whole calendar with basic data
 *
 */
public class Calendar {
	
	/**
	 * class variables for calendar data 
	 */
	/** name of the calendar */
	private String name;
	/** name of the calendar to display */
	private String displayName;
	/** used timezone */
	private String timeZone;
	/** array of the events contained in the calendar */
	private CalendarEvent[] events;

	/**
	 * @param name
	 * @param displayName
	 * @param timeZone
	 * @param events
	 */
	public Calendar(String name, String displayName, String timeZone,
			CalendarEvent[] events) {
		this.name = name;
		this.displayName = displayName;
		this.timeZone = timeZone;
		this.events = events;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the events
	 */
	public CalendarEvent[] getEvents() {
		return events;
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(CalendarEvent[] events) {
		this.events = events;
	}

	/**
	 * @param event to add
	 */
	public void addEvent(CalendarEvent event)
	{
		this.events.put(event.getId(), event);
	}

	/**
	 * @param id of the event
	 * @return
	 */
	public CalendarEvent getEvent(String id)
	{
		return events.get(id);
	}
}
