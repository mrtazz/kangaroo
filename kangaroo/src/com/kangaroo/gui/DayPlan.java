/**
 * 
 */
package com.kangaroo.gui;

import java.util.ArrayList;
import java.util.HashMap;

import com.android.kangaroo.R;
import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.calendar.CalendarLibrary;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author mrtazz
 *
 */
public class DayPlan extends Activity {

	  @Override
	  public void onCreate(Bundle savedInstanceState) 
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.dayplan);
	        ArrayList<String> eventList;
	        ArrayAdapter<String> eventListAdapter;
	        CalendarLibrary cl = new CalendarLibrary(this);
	        HashMap<String, CalendarEvent> events;
	        TextView tv = (TextView)findViewById(R.id.DayTitle);
	        ListView lv = (ListView)findViewById(R.id.EventList);
	        
	        tv.setText("Today");
	        events = cl.getTodaysEvents("1");
	        eventList = new ArrayList<String>(events.keySet());
		    // Bind the ListView to an ArrayList of strings.
		  	eventListAdapter = new ArrayAdapter<String>(getApplicationContext(), 
		  	                             		android.R.layout.simple_list_item_1,
		  	                             		eventList);
		  	lv.setAdapter(eventListAdapter);	        
	  } 

}
