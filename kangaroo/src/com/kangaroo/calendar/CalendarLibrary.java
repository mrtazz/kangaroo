/**
 *
 */
package com.kangaroo.calendar;

import java.util.Date;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * @author mrtazz
 * Library for accessing calendar data via the content provider
 *
 */
public class CalendarLibrary {

	/** content provider URIs should not be changed */
	final private String contentCalendarUri = "content://calendar/calendars";
	final private String contentEventsUri = "content://calendar/events";
	/** URI objects for the provider URIs */
	final private Uri calendarURI = Uri.parse(contentCalendarUri);
	final private Uri eventsURI = Uri.parse(contentEventsUri);
	/** the content resolver object */
	private ContentResolver contentResolver;
	/** db cursor to get data from the content provider*/
	private Cursor calendarCursor;
	private Cursor eventsCursor;
	/** which calendar information do you want today? */
	private String[] calendarFields = {"_id", "name", "displayname",
									   "color", "selected", "timezone"};
	private String[] eventsFields = {"_id", "title", "allDay", "dtstart",
									 "dtend", "description", "eventLocation",
									 "calendar_id", "eventTimezone"};
	/** the dictionary containing calendar objects */
	private HashMap<String, Calendar> dictCalendars;

	/** object constructor */
	public CalendarLibrary(Context ctx)
	{
		contentResolver = ctx.getContentResolver();
		calendarCursor = contentResolver.query(calendarURI, calendarFields,
											   null, null, null);
		dictCalendars = new HashMap<String, Calendar>();
		readCalendars();
	}

    /**
     * @brief method to read calendar data from the content provider into the
     * dictionary object
     *
     * @return 0 is okay everything else is wrong
     */
	public void readCalendars()
	{
        // read calendars from db cursor
        while (calendarCursor.moveToNext())
        {
            Calendar cal = new Calendar(calendarCursor.getString(1),
                                        calendarCursor.getString(2),
                                        calendarCursor.getString(5),
                                        null);
            dictCalendars.put(calendarCursor.getString(1),cal);
        }
	}

    /**
     * @brief method for getting a specific calendar
     *
     * @param name of the calendar
     *
     * @return the specified calendar object
     */
    public Calendar getCalendar(String name)
    {
        return dictCalendars.get(name);
    }

    /**
     * @brief get all the calendar names
     *
     * @return String[] with all calendar names
     */
    public String[] getAllCalendarNames()
    {
    	// create array from dictionary keys
    	String[] ret = dictCalendars.keySet().toArray(new String[0]);
    	return ret;
    }

    /**
     * @brief method to get all events from specified calendar
     *
     * @param calendarName of the calendar
     *
     * @return CalendarEvent[] with all events from calendar
     */
    public HashMap<String, CalendarEvent> getEvents(String calendarName)
    {
        // initialize data variables
        HashMap<String, CalendarEvent> events;
        Calendar cal = dictCalendars.get(calendarName);
        // get all events from calendar
        events = cal.getEvents();
        // return
        return events;
    }

    /**
     * @brief method for getting events for a specific calendar
     * @param id for the calendar to get events from
     * @return HashMap with events
     */
    public HashMap<String, CalendarEvent> getEventsFromBackend(String id)
    {
    	HashMap<String, CalendarEvent> events = new HashMap<String, CalendarEvent>();
    	String selection = "calendar_id=?";
    	String[] selection_args = {id};

		eventsCursor = contentResolver.query(eventsURI, eventsFields,
										 	 selection, selection_args, null);

		 while (eventsCursor.moveToNext())
	        {
				final String eventid = eventsCursor.getString(0);
				final String title = eventsCursor.getString(1);
				final Boolean allDay = Boolean.parseBoolean(eventsCursor.getString(2));
				final Date dtstart = new Date(Long.parseLong(eventsCursor.getString(3)));
				final Date dtend = new Date(Long.parseLong(eventsCursor.getString(4)));
				final String description = eventsCursor.getString(5);
				final String eventLocation = eventsCursor.getString(6);
				final int calendar = Integer.parseInt(eventsCursor.getString(7));
				final String timezone = eventsCursor.getString(8);

	            CalendarEvent event = new CalendarEvent(eventid, title, eventLocation,
	            										null, null, dtstart, dtend,
	            										null, null, allDay, description,
	            										calendar,timezone);
	            events.put(title,event);
	        }

		return events;
    }


    /**
     * @brief update method to enter events to backend
     * @param event object to add to backend
     */
    public void addEventToBackend(CalendarEvent event)
    {
    	ContentValues values = new ContentValues();
    	/** build values */
    	values.put("calendar_id", event.getCalendar());
    	values.put("eventTimezone", event.getTimezone());
    	values.put("title", event.getTitle());
    	values.put("allDay", event.getAllDay());
    	values.put("dtstart", event.getStartDate().getTime());
    	values.put("dtend", event.getEndDate().getTime());
    	values.put("description", event.getDescription());
    	values.put("eventLocation", event.getLocation());
    	values.put("transparency", 0);
    	values.put("visibility", 0);
    	values.put("hasAlarm", 0);

    	/** enter into content provider backend */
    	contentResolver.insert(eventsURI, values);
    }
}
