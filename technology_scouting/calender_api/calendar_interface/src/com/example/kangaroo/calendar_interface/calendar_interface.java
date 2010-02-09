package com.example.kangaroo.calendar_interface;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class calendar_interface extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ArrayList<String> al; // = new ArrayList<String>();
        ArrayAdapter<String> aa;
        TextView tv = (TextView)findViewById(R.id.foo_text);
        ListView lv = (ListView)findViewById(R.id.foo_list);

        tv.setText("who dat?");
        CalendarLibrary cl = new CalendarLibrary(this);

        HashMap<String, CalendarEvent> events;
        
        CalendarEvent ev = new CalendarEvent("3", "InsertTest", "here", null, null,
        									new Date(), new Date(), false,
        									false, false, "I am a description", 1, "GMT");
        
        cl.addEventToBackend(ev);
        
        tv.setText("who dat?");
        // get events
        events = cl.getEventsFromBackend(new String("1"));
        al = new ArrayList<String>(events.keySet()); 
        
	    // Bind the ListView to an ArrayList of strings.
	  	aa = new ArrayAdapter<String>(getApplicationContext(), 
	  	                              android.R.layout.simple_list_item_1,
 	                                  al);
	  	lv.setAdapter(aa);
    }
}