package com.kangaroo.gui;


import java.util.Date;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.kangaroo.R;
import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.calendar.CalendarLibrary;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintInterface;
import com.kangaroo.task.TaskLibrary;
import com.mobiletsm.routing.Place;

public class ActivityBuildPlan extends Activity
{
    private TextView myText; 
    private Context ctx;
    
    private ComponentName service;
    
	  @Override
	  public void onCreate(Bundle savedInstanceState) 
	  {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activitybuildplan); 
	        
	    //    mAlarmSender = PendingIntent.getService(ActivityBuildPlan.this,
	    //            0, new Intent(ActivityBuildPlan.this, ServiceCallTick.class), 0);
	        
	        
	        // Watch for button clicks.
	        Button button = (Button)findViewById(R.id.bind);
	        button.setOnClickListener(mStartAlarmListener);
	        button = (Button)findViewById(R.id.unbind);
	        button.setOnClickListener(mStopAlarmListener);
	        
	        myText = (TextView)findViewById(R.id.text);
	        ctx = this.getApplicationContext(); 
	  }   
	  
	   
	    private OnClickListener mStartAlarmListener = new OnClickListener() {
	        public void onClick(View v) {
	            // We want the alarm to go off 30 seconds from now.
	            //long firstTime = SystemClock.elapsedRealtime();

	            // Schedule the alarm!
	            //AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	            //am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
	            //                firstTime, 30*1000, mAlarmSender);
	            
	        	//ComponentName comp = new ComponentName(getPackageName(), ServiceCallTick.class.getName());
				//ComponentName service = startService(new Intent().setComponent(comp));
				//ComponentName comp2 = new ComponentName(getPackageName(), ServiceCallLocation.class.getName());
				//ComponentName service2 = startService(new Intent().setComponent(comp2));
				
	        	//UserNotification un = new UserNotification(ctx);
	        	//un.showNotification("Title", "textMessage", true, MainWindow.class);
	        	
	        	/*
	        	ActiveDayPlan adp = new ActiveDayPlan();
	        	adp.setContext(getApplicationContext());
	        	RoutingEngine re = new MobileTSMRoutingEngine();
	        	re.init("file:/sdcard/map-fr.db");
	        	adp.setRoutingEngine(re);
	        	Vehicle ve = new AllStreetVehicle();
	        	ve.setMaxSpeed(50.0);
	        	DayPlanConsistency dpc = adp.checkConsistency(ve);
	        	System.out.println(dpc.isConsistent());
	        	re.shutdown();
	        	
				// Tell the user about what we did.
	            Toast.makeText(ActivityBuildPlan.this, "scheduled service started",
	                    Toast.LENGTH_LONG).show();
	                    
	                    */
	        }
	    };

	    private void printTask(Task tt)
	    {
	    	System.out.println(tt.getName());
	    	System.out.println(tt.getDescription());
	    	TaskConstraintInterface temp[] = tt.getConstraints();
	    	for(int i=0; i<temp.length; i++)
	    	{
	    		System.out.println(temp[i].getType());
	    	}
	    	System.out.println("");
	    	//System.out.println(tt.serialize());
	    	System.out.println("");
	    }
	    
	    private void generateEvents()
	    {
	    	CalendarLibrary cl = new CalendarLibrary(ctx);
			int calendarId = cl.getCalendar("kangaroo@lordofhosts.de").getId();
			CalendarEvent ce = new CalendarEvent("", "Termin1", "", 47.9948308, 7.8497112, new Date(110,2,9,14,0), new Date(110,2,9,15,0), false, false, "Description1", calendarId, "GMT", (Place)null);	
			cl.insertEventToBackend(ce);
			ce = new CalendarEvent("", "Termin2", "", 47.9950469, 7.8326674, new Date(110,2,9,15,0), new Date(110,2,9,16,0), false, false, "Description2", calendarId, "GMT", (Place)null);	
			cl.insertEventToBackend(ce);
	    }
	    
	    private OnClickListener mStopAlarmListener = new OnClickListener() {
	        public void onClick(View v) {
	            // And cancel the alarm.
	            //AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	            //am.cancel(mAlarmSender);
	        	//stopService(new Intent().setComponent(service));
	        	TaskLibrary tm = new TaskLibrary(getApplicationContext(), "kangaroo@lordofhosts.de");
	        	
	        	generateEvents();
	        	/*
	        	Task myTask = new Task();

	        	myTask.setName("Name1");
	        	myTask.setDescription("Description1");
	        	myTask.addConstraint(new TaskConstraintLocation(new Place(0,0)));
	        	myTask.addConstraint(new TaskConstraintPOI(new POICode(POICode.AMENITY_ARCHITECT_OFFICE)));
	        	myTask.addConstraint(new TaskConstraintDate(new Date(110,2,4)));
	        	tm.addTask(myTask);

	        	myTask = new Task();
	        	myTask.setName("Name2");
	        	myTask.setDescription("Description2");
	        	myTask.addConstraint(new TaskConstraintLocation(new Place(0,0)));
	        	myTask.addConstraint(new TaskConstraintPOI(new POICode(POICode.SHOP_HAIRDRESSER)));
	        	myTask.addConstraint(new TaskConstraintDate(new Date(110,2,4)));
	        	myTask.addConstraint(new TaskConstraintDayTime(new Date(0,0,0,8,0), new Date(0,0,0,17,0)));
	        	tm.addTask(myTask);
	         	
	        	ArrayList<Task> myList = tm.getTasks();
	        	Iterator<Task> it = myList.iterator();
	        	printTask(it.next());
	        	printTask(it.next());
	        */
	            // Tell the user about what we did.
	            Toast.makeText(ActivityBuildPlan.this, "done.",
	                    Toast.LENGTH_LONG).show();

	        }
	    };
	  
}
