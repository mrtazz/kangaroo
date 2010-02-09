package com.kangaroo.gui;


import com.android.kangaroo.R;
import com.kangaroo.system.ServiceCallTick;

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
	            
	        	ComponentName comp = new ComponentName("com.android.kangaroo", ServiceCallTick.class.getName());
				service = startService(new Intent().setComponent(comp));
	            
				// Tell the user about what we did.
	            Toast.makeText(ActivityBuildPlan.this, "scheduled service started",
	                    Toast.LENGTH_LONG).show();
	        }
	    };

	    private OnClickListener mStopAlarmListener = new OnClickListener() {
	        public void onClick(View v) {
	            // And cancel the alarm.
	            //AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	            //am.cancel(mAlarmSender);

	        	stopService(new Intent().setComponent(service));
	        	
	            // Tell the user about what we did.
	            Toast.makeText(ActivityBuildPlan.this, "scheduled service stopped",
	                    Toast.LENGTH_LONG).show();

	        }
	    };
	  
}
