/**
 * 
 */
package com.kangaroo.calendar;

import java.util.HashMap;

import com.kangaroo.calendar.CalendarEvent;

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
	/** calendar id */
	private int id;
	/** name of the calendar to display */
	private String displayName;
	/** used timezone */
	private String timeZone;
	/** array of the events contained in the calendar */
	private HashMap<String, CalendarEvent> events;

	/**
	 * @param name
	 * @param displayName
	 * @param timeZone
	 * @param events
	 */
	public Calendar(int id, String name, String displayName, String timeZone,
					HashMap<String, CalendarEvent> events)
	{
		this.name = name;
		this.displayName = displayName;
		this.timeZone = timeZone;
		this.events = events;
		this.id = id;
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
	public HashMap<String, CalendarEvent> getEvents() {
		return events;
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(HashMap<String, CalendarEvent> events) {
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
