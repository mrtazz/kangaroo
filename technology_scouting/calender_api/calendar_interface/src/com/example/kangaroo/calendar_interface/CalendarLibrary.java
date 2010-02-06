/**
 *
 */
package com.example.kangaroo.calendar_interface;

import java.util.Date;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
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
									 "calendar_id"};
	/** the dictionary containing calendar objects */
	private HashMap<String, Calendar> dictCalendars;

	/** object constructor */
	public CalendarLibrary()
	{
		calendarCursor = contentResolver.query(calendarURI, calendarFields,
											   null, null, null);
	}

    /**
     * @brief method to read calendar data from the content provider into the
     * dictionary object
     *
     * @return 0 is okay everything else is wrong
     */
	public int readCalendars()
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

        return 0;
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
    public String[] getAvailableCalendars()
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
    public HashMap<String, CalendarEvent> getEventsFromProvider(String id)
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

	            CalendarEvent event = new CalendarEvent(eventid, title, eventLocation,
	            										null, null, dtstart, dtend,
	            										null, null, allDay, description);
	            events.put(calendarCursor.getString(1),event);
	        }

		return events;
    }
}
