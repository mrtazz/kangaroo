/**
 *
 */
package com.example.kangaroo.calendar_interface;

import java.util.HashMap;

import android.content.ContentResolver;
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
		calendarCursor = contentResolver.query(Uri.parse(contentCalendarUri),
											  calendarFields, null,
											  null, null);
		eventsCursor = contentResolver.query(Uri.parse(contentEventsUri),
				  							 eventsFields, null, null, null);
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




}
