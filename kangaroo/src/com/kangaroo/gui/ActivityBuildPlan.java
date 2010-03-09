package com.kangaroo.gui;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.android.kangaroo.R;
import com.kangaroo.system.ServiceCallLocation;
import com.kangaroo.system.ServiceCallTick;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintPOI;
import com.kangaroo.task.TaskConstraintDate;
import com.kangaroo.task.TaskConstraintDayTime;
import com.kangaroo.task.TaskConstraintInterface;
import com.kangaroo.task.TaskConstraintLocation;
import com.kangaroo.task.TaskManager;
import com.mobiletsm.osm.data.searching.POICode;
import com.mobiletsm.routing.Place;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityBuildPlan extends Activity
{
    private TextView myText; 
    
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
	         
	  }   
	  
	   
	    private OnClickListener mStartAlarmListener = new OnClickListener() {
	        public void onClick(View v) {
	            // We want the alarm to go off 30 seconds from now.
	            //long firstTime = SystemClock.elapsedRealtime();

	            // Schedule the alarm!
	            //AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	            //am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
	            //                firstTime, 30*1000, mAlarmSender);
	            
	        	ComponentName comp = new ComponentName(getPackageName(), ServiceCallTick.class.getName());
				ComponentName service = startService(new Intent().setComponent(comp));
				ComponentName comp2 = new ComponentName(getPackageName(), ServiceCallLocation.class.getName());
				ComponentName service2 = startService(new Intent().setComponent(comp2));
				
				// Tell the user about what we did.
	            Toast.makeText(ActivityBuildPlan.this, "scheduled service started",
	                    Toast.LENGTH_LONG).show();
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
	    
	    private OnClickListener mStopAlarmListener = new OnClickListener() {
	        public void onClick(View v) {
	            // And cancel the alarm.
	            //AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	            //am.cancel(mAlarmSender);
	        	//stopService(new Intent().setComponent(service));
	        	TaskManager tm = new TaskManager(getApplicationContext());
	        	
	        	Task myTask = new Task();
<<<<<<< HEAD
	        	myTask.setName("Name");
	        	myTask.setDescription("Description");
	        	myTask.addConstraint(new TaskConstraintLocation(1));
	        	myTask.addConstraint(new TaskConstraintPOI(new POICode(POICode.AMENITY_ARCHITECT_OFFICE)));
	        	myTask.addConstraint(new TaskConstraintDate(new Date(110,1,24)));
	        	printTask(myTask);
	        	
	        	String temp = myTask.serialize();
	        	System.out.println(temp);
	        	Task myTask2 = Task.deserialize(temp);
	        	printTask(myTask2);
	        	System.out.println(myTask2.serialize());
=======
	        	myTask.setName("Name1");
	        	myTask.setDescription("Description1");
	        	myTask.addConstraint(new TaskConstraintLocation(new Place(0,0)));
	        	myTask.addConstraint(new TaskConstraintAmenity(new POICode(POICode.AMENITY_ARCHITECT_OFFICE)));
	        	myTask.addConstraint(new TaskConstraintDate(new Date(110,2,4)));
	        	tm.addTask(myTask);
>>>>>>> origin/horizon
	        	
	        	myTask = new Task();
	        	myTask.setName("Name2");
	        	myTask.setDescription("Description2");
	        	myTask.addConstraint(new TaskConstraintLocation(new Place(0,0)));
	        	myTask.addConstraint(new TaskConstraintAmenity(new POICode(POICode.SHOP_HAIRDRESSER)));
	        	myTask.addConstraint(new TaskConstraintDate(new Date(110,2,4)));
	        	myTask.addConstraint(new TaskConstraintDayTime(new Date(0,0,0,8,0), new Date(0,0,0,17,0)));
	        	tm.addTask(myTask);
	         	
	        	ArrayList<Task> myList = tm.getTasks();
	        	Iterator<Task> it = myList.iterator();
	        	printTask(it.next());
	        	printTask(it.next());
	        
	            // Tell the user about what we did.
	            Toast.makeText(ActivityBuildPlan.this, "done.",
	                    Toast.LENGTH_LONG).show();

	        }
	    };
	  
}
