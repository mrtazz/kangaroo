/**
 *
 */
package com.kangaroo.calendar;

import java.util.Date;

import com.mobiletsm.routing.Place;
/**
 * @author mrtazz
 * 
 * class representing calendar events with basic data
 *
 */
public class CalendarEvent {

	/**
	 * simple event data
	 */
	/** the event id */
	private String id;
	/** event title */
	private String title;
	/** location name */
	private String location;
	/** longitude of the event location */
	private Double locationLongitude;
	/** latitude of the event location */
	private Double locationLatitude;
	/** Date when the event starts */
	private Date startDate;
	/** Date when the event stops */
	private Date endDate;
	/** whether or not the event was a task before */
	private Boolean wasTask;
	/** all day event */
	private Boolean allDay;
	/** description */
	private String description;
	/** calendar */
	private int calendar;
	/** timezone */
	private String timezone;
	/** mobileTSM places */
	private Place place;

	/**
	 * @brief Constructor for event object
	 *
	 * @param id
	 * @param title
	 * @param location
	 * @param locationLongitude
	 * @param locationLatitude
	 * @param startDate
	 * @param endDate
	 * @param wasTask
	 * @param taskLink
	 * @param allDay
	 * @param description
	 */
	public CalendarEvent(String id, String title, String location,
			Double locationLongitude, Double locationLatitude, Date startDate,
			Date endDate, Boolean wasTask, Boolean allDay,
			String description, int calendar, String timezone, Place place) {
		this.id = id;
		this.title = title;
		this.location = location;
		this.locationLongitude = locationLongitude;
		this.locationLatitude = locationLatitude;
		this.startDate = startDate;
		this.endDate = endDate;
		this.wasTask = wasTask;
		this.allDay = allDay;
		this.description = description;
		this.calendar = calendar;
		this.timezone = timezone;
	}
	
	public CalendarEvent() {
		this.id = null;
		this.title = null;
		this.location = null;
		this.locationLongitude = null;
		this.locationLatitude = null;
		this.startDate = null;
		this.endDate = null;
		this.wasTask = null;
		this.allDay = null;
		this.description = null;
		this.calendar = -1;
		this.timezone = null;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the locationLongitude
	 */
	public Double getLocationLongitude() {
		return locationLongitude;
	}

	/**
	 * @param locationLongitude the locationLongitude to set
	 */
	public void setLocationLongitude(Double locationLongitude) {
		this.locationLongitude = locationLongitude;
	}

	/**
	 * @return the locationLatitude
	 */
	public Double getLocationLatitude() {
		return locationLatitude;
	}

	/**
	 * @param locationLatitude the locationLatitude to set
	 */
	public void setLocationLatitude(Double locationLatitude) {
		this.locationLatitude = locationLatitude;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the wasTask
	 */
	public Boolean getWasTask() {
		return wasTask;
	}

	/**
	 * @param wasTask the wasTask to set
	 */
	public void setWasTask(Boolean wasTask) {
		this.wasTask = wasTask;
	}

	/**
	 * @return the allDay
	 */
	public Boolean getAllDay() {
		return allDay;
	}

	/**
	 * @param allDay the allDay to set
	 */
	public void setAllDay(Boolean allDay) {
		this.allDay = allDay;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the calendar
	 */
	public int getCalendar() {
		return calendar;
	}

	/**
	 * @param calendar the calendar to set
	 */
	public void setCalendar(int calendar) {
		this.calendar = calendar;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	/**
	 * @return the place
	 */
	public Place getPlace() {
		/* never return null */
		if (place == null) {
			place = new Place(getLocationLatitude(), getLocationLongitude());		
		}
		return place;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(Place place) {
		this.place = place;
	}


}
