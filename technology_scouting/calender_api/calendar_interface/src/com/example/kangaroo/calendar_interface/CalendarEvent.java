/**
 * 
 */
package com.example.kangaroo.calendar_interface;

import java.util.Date;
/**
 * @author mrtazz
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
	private Float locationLongitude;
	/** latitude of the event location */
	private Float locationLatitude;
	/** Date when the event starts */
	private Date startDate;
	/** Date when the event stops */
	private Date endDate;
	/** whether or not the event was a task before */
	private Boolean wasTask;
	/** whether or not the event was a link before */
	private Boolean taskLink;
	
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
	 */
	public CalendarEvent(String id, String title, String location,
			Float locationLongitude, Float locationLatitude, Date startDate,
			Date endDate, Boolean wasTask, Boolean taskLink) {
		this.id = id;
		this.title = title;
		this.location = location;
		this.locationLongitude = locationLongitude;
		this.locationLatitude = locationLatitude;
		this.startDate = startDate;
		this.endDate = endDate;
		this.wasTask = wasTask;
		this.taskLink = taskLink;
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
	public Float getLocationLongitude() {
		return locationLongitude;
	}

	/**
	 * @param locationLongitude the locationLongitude to set
	 */
	public void setLocationLongitude(Float locationLongitude) {
		this.locationLongitude = locationLongitude;
	}

	/**
	 * @return the locationLatitude
	 */
	public Float getLocationLatitude() {
		return locationLatitude;
	}

	/**
	 * @param locationLatitude the locationLatitude to set
	 */
	public void setLocationLatitude(Float locationLatitude) {
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
	 * @return the taskLink
	 */
	public Boolean getTaskLink() {
		return taskLink;
	}

	/**
	 * @param taskLink the taskLink to set
	 */
	public void setTaskLink(Boolean taskLink) {
		this.taskLink = taskLink;
	}
	
	
}