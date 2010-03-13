package com.mobiletsm.testbench;


import java.util.Date;
import java.util.List;

import com.kangaroo.ActiveDayPlan;
import com.kangaroo.DayPlanConsistency;
import com.kangaroo.MemoryCalendarAccessAdapter;
import com.kangaroo.calendar.CalendarAccessAdapter;
import com.kangaroo.calendar.CalendarEvent;
import com.mobiletsm.routing.AllStreetVehicle;
import com.mobiletsm.routing.MobileTSMRoutingEngine;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RouteParameter;
import com.mobiletsm.routing.RoutingEngine;
import com.mobiletsm.routing.Vehicle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MobileTSMTestBench extends Activity {
    
	private Button doStuffButton;	
	private Button exitButton;	
	private TextView outputText;	
	private EditText latEdit;	
	private EditText lonEdit;
	private EditText speedEdit;
	
	private RoutingEngine engine = null;
	
	
	private ActiveDayPlan activeDayPlan = null;
	
	
	Place place1 = null;
	Place place2 = null;
	Vehicle vehicle = null;
	Date now = null;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        doStuffButton = (Button) findViewById(R.id.doStuffButton);        
        exitButton = (Button) findViewById(R.id.exitButton);        
        outputText = (TextView) findViewById(R.id.outputText);
        latEdit = (EditText) findViewById(R.id.latEdit);        
        lonEdit = (EditText) findViewById(R.id.lonEdit);        
        speedEdit = (EditText) findViewById(R.id.speedEdit);        
        
        
        /* create and add some events */
        
        CalendarAccessAdapter adapter = new MemoryCalendarAccessAdapter();
        adapter.setContext(getApplicationContext());
        activeDayPlan = new ActiveDayPlan();
        activeDayPlan.setCalendarAccessAdapter(adapter);
        
        now = new Date(2010 - 1900, 3, 10, 19, 00);
        
        CalendarEvent event1 = new CalendarEvent();
        event1.setStartDate(new Date(2010 - 1900, 3, 10, 19, 30));
        event1.setEndDate(new Date(2010 - 1900, 3, 10, 20, 00));
        event1.setLocationLatitude(48.0064241);
        event1.setLocationLongitude(7.8521991);

        CalendarEvent event2 = new CalendarEvent();
        event2.setStartDate(new Date(2010 - 1900, 3, 10, 20, 10));
        event2.setEndDate(new Date(2010 - 1900, 3, 10, 21, 00));
        event2.setLocationLatitude(48.000);
        event2.setLocationLongitude(7.852);

        CalendarEvent event3 = new CalendarEvent();
        event3.setStartDate(new Date(2010 - 1900, 3, 10, 21, 20));
        event3.setEndDate(new Date(2010 - 1900, 3, 10, 21, 40));
        event3.setLocationLatitude(47.987);
        event3.setLocationLongitude(7.852);

        CalendarEvent event4 = new CalendarEvent();
        event4.setStartDate(new Date(2010 - 1900, 3, 10, 21, 30));
        event4.setEndDate(new Date(2010 - 1900, 3, 10, 21, 50));
        event4.setLocationLatitude(47.987);
        event4.setLocationLongitude(7.852);        

        CalendarEvent event5 = new CalendarEvent();
        event5.setStartDate(new Date(2010 - 1900, 3, 10, 22, 0));
        event5.setEndDate(new Date(2010 - 1900, 3, 10, 22, 40));
        event5.setLocationLatitude(47.983);
        event5.setLocationLongitude(7.852);        

        CalendarEvent event6 = new CalendarEvent();
        event6.setStartDate(new Date(2010 - 1900, 3, 10, 23, 0));
        event6.setEndDate(new Date(2010 - 1900, 3, 10, 23, 40));
        event6.setLocationLatitude(48.983);
        event6.setLocationLongitude(7.852);         
        
        List<CalendarEvent> events = activeDayPlan.getEvents();
        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        events.add(event5);
        events.add(event6);
        activeDayPlan.setEvents(events);
        
        
        doStuffButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				if (engine == null || !engine.initialized()) {
					engine = new MobileTSMRoutingEngine();
					engine.init("/sdcard/map-fr.db");
					engine.enableRoutingCache();
				}
				
				
				if (engine.initialized()) {
			        activeDayPlan.setRoutingEngine(engine);
			        
			        System.out.println("# events in calendar = " + activeDayPlan.getEvents().size());			        
					
			        DayPlanConsistency consistency = 
						activeDayPlan.checkConsistency(new AllStreetVehicle(5.0), now);
					if (consistency != null) {
						System.out.println("consistency = " + consistency.toString());					
						outputText.setText(consistency.toString());
					}
				}
				
				/*
				System.out.println("engine.initialized() = " + engine.initialized());
				
				if (engine.initialized()) {					
					if (place1 == null) {
						System.out.println("create place1");
						place1 = new Place(48.0064241, 7.8521991);				
					}
					
					double lat = Double.parseDouble(latEdit.getText().toString());
					double lon = Double.parseDouble(lonEdit.getText().toString());
					
					if (place2 == null || place2.getLatitude() != lat || place2.getLongitude() != lon) {
						System.out.println("create place2");
						place2 = new Place(lat, lon);
					}
					
					double maxSpeed = Double.parseDouble(speedEdit.getText().toString());
					
					if (vehicle == null || vehicle.getMaxSpeed() != maxSpeed) {
						System.out.println("create vehicle");
						vehicle = new AllStreetVehicle(maxSpeed);
					}
					
					
					RouteParameter route = engine.routeFromTo(place1, place2, vehicle);
					
					System.out.println("route = " + route.toString());					
					outputText.setText(route.toString());
				}
				*/
				
			}
		});
        
        
        exitButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (engine != null && engine.initialized()) {
					engine.shutdown();
				}
				finish();
			}
		});
        
        
    }
}