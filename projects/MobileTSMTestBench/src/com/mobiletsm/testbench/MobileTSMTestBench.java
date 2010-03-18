package com.mobiletsm.testbench;


import java.util.Date;
import java.util.List;

import com.kangaroo.ActiveDayPlan;
import com.kangaroo.DayPlan;
import com.kangaroo.DayPlanConsistency;
import com.kangaroo.DayPlanOptimizer;
import com.kangaroo.GreedyTaskInsertionOptimizer;
import com.kangaroo.calendar.CalendarAccessAdapter;
import com.kangaroo.calendar.CalendarAccessAdapterMemory;
import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintDate;
import com.kangaroo.task.TaskConstraintDayTime;
import com.kangaroo.task.TaskConstraintDuration;
import com.kangaroo.task.TaskConstraintPOI;
import com.mobiletsm.osm.data.searching.POICode;
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
	Place home = null;
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
        
        CalendarAccessAdapter adapter = new CalendarAccessAdapterMemory();
        activeDayPlan = new ActiveDayPlan();
        activeDayPlan.setCalendarAccessAdapter(adapter);
        
        now = new Date(2010 - 1900, 3, 10, 19, 00);
        home = new Place(48.0064241, 7.8521991);
        vehicle = new AllStreetVehicle(50.0);
        
        
        CalendarEvent event1 = new CalendarEvent();
        event1.setStartDate(new Date(2010 - 1900, 3, 10, 19, 30));
        event1.setEndDate(new Date(2010 - 1900, 3, 10, 20, 00));
        event1.setLocationLatitude(48.00);
        event1.setLocationLongitude(7.852);

        CalendarEvent event2 = new CalendarEvent();
        event2.setStartDate(new Date(2010 - 1900, 3, 10, 20, 45));
        event2.setEndDate(new Date(2010 - 1900, 3, 10, 21, 00));
        event2.setLocationLatitude(48.000);
        event2.setLocationLongitude(7.852);

        CalendarEvent event3 = new CalendarEvent();
        event3.setStartDate(new Date(2010 - 1900, 3, 10, 21, 20));
        event3.setEndDate(new Date(2010 - 1900, 3, 10, 21, 40));
        event3.setLocationLatitude(47.987);
        event3.setLocationLongitude(7.852);

        CalendarEvent event4 = new CalendarEvent();
        event4.setStartDate(new Date(2010 - 1900, 3, 10, 21, 45));
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
        
        CalendarEvent event7 = new CalendarEvent();
        event7.setStartDate(new Date(2010 - 1900, 3, 10, 23, 45));
        event7.setEndDate(new Date(2010 - 1900, 3, 10, 23, 50));
        event7.setLocationLatitude(47.983);
        event7.setLocationLongitude(7.852); 
        
        activeDayPlan.addEvent(event1);
        activeDayPlan.addEvent(event2);
        activeDayPlan.addEvent(event3);
        activeDayPlan.addEvent(event4);
        activeDayPlan.addEvent(event5);
        activeDayPlan.addEvent(event6);
        activeDayPlan.addEvent(event7);        
                
        
        /* add and create some tasks */
        
		Task task1 = new Task();
		task1.setName("Schnell was essen");
		task1.addConstraint(new TaskConstraintDuration(5));
		task1.addConstraint(new TaskConstraintPOI(new POICode(POICode.AMENITY_FAST_FOOD)));
		task1.addConstraint(new TaskConstraintDayTime(new Date(0, 0, 0, 19, 00), new Date(0, 0, 0, 20, 01)));
		
		Task task2 = new Task();
		task2.setName("Frisšr");
		task2.addConstraint(new TaskConstraintDuration(3));
		task2.addConstraint(new TaskConstraintPOI(new POICode(POICode.SHOP_HAIRDRESSER)));
		task2.addConstraint(new TaskConstraintDayTime(18, 00, 23, 00));
		
		Task task3 = new Task();
		task3.setName("Oma anrufen");
		task3.addConstraint(new TaskConstraintDuration(3));
		task3.addConstraint(new TaskConstraintDate(new Date(2010 - 1900, 5, 2)));
		
		Task task4 = new Task();
		task4.setName("Brštchen kaufen");
		task4.addConstraint(new TaskConstraintDuration(3));
		task4.addConstraint(new TaskConstraintPOI(new POICode(POICode.SHOP_BAKERY)));		
		//task4.addConstraint(new TaskConstraintDayTime(18, 00, 19, 10));
		
		Task task5 = new Task();
		task5.setName("Blumen kaufen");
		task5.addConstraint(new TaskConstraintDuration(30));
		task5.addConstraint(new TaskConstraintPOI(new POICode(POICode.SHOP_FLORIST)));	
		task5.addConstraint(new TaskConstraintDayTime(18, 00, 23, 00));

		Task task6 = new Task();
		task6.setName("Buch kaufen");
		task6.addConstraint(new TaskConstraintDuration(30));
		task6.addConstraint(new TaskConstraintPOI(new POICode(POICode.SHOP_BOOKS)));	
		task6.addConstraint(new TaskConstraintDayTime(18, 00, 23, 00));
		
		
		activeDayPlan.addTask(task1);
		activeDayPlan.addTask(task2);
		activeDayPlan.addTask(task3);
		activeDayPlan.addTask(task4);        
		activeDayPlan.addTask(task5);		
		activeDayPlan.addTask(task6);		
        
        
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
			        
			        //System.out.println("---> " + activeDayPlan.toString());
					//System.out.println("---> " + activeDayPlan.checkConsistency(vehicle, now).toString());
					
					DayPlanOptimizer optimizer = new GreedyTaskInsertionOptimizer();
					activeDayPlan.setOptimizer(optimizer);
					DayPlan optimizedDayPlan = activeDayPlan.optimize(now, home, vehicle);

					System.out.println("---> " + optimizedDayPlan.toString());
					//System.out.println("---> " + optimizedDayPlan.checkConsistency(vehicle, now).toString());
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