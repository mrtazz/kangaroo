package com.kangaroo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;

   private OnClickListener mBindListener = new OnClickListener() {
	        public void onClick(View v) {
	            // Establish a connection with the service.  We use an explicit
	            // class name because we want a specific service implementation that
	            // we know will be running in our own process (and thus won't be
	            // supporting component replacement by other applications).
	            
	        	 Intent i = new Intent();
	     	    i.setClassName( "com.android.kangaroo", "com.android.kangaroo.ServiceRoute" );
	        	
	     	    //bindService(new Intent(ActivityBuildPlan.this, ServiceRoute.class), mConnection, Context.BIND_AUTO_CREATE);
	        	bindService(i, mConnection, Context.BIND_AUTO_CREATE);
	        	//serviceRouteBound = myServiceFactory.getServiceRoute(ActivityBuildPlan.this);
	        	
	        	while(serviceRouteBound == null);
	        	
	        	mIsBound = true;
	            if(serviceRouteBound != null)
	            {
	            	myText.setText(serviceRouteBound.routingTest("test"));
	            }
	            else
	            {
	            	myText.setText("did not work");
	            }
	        }
	    };
	
	    private OnClickListener mUnbindListener = new OnClickListener() {
	        public void onClick(View v) {
	            if (mIsBound) {
	                // Detach our existing connection.
	                unbindService(mConnection);
	                mIsBound = false;
		            mIsBound = true;
		            if(serviceRouteBound != null)
		            {
		            	myText.setText("not ok");
		            }
		            else
		            {
		            	myText.setText("ok");
		            }
	            }
	        }
	    };
	
	    
		  //private FactoryServiceBinding myServiceFactory = new FactoryServiceBinding();
		  
	    private ServiceConnection mConnection = new ServiceConnection() 
	    {
	        public void onServiceConnected(ComponentName className, IBinder service) 
	        {
	        	serviceRouteBound = ((RoutingInteraction.LocalBinder)service).getService();
	            //Toast.makeText(ActivityBuildPlan.this, "BAM - service bound",Toast.LENGTH_SHORT).show();
	        }
	
	        public void onServiceDisconnected(ComponentName className) 
	        {
	        	serviceRouteBound = null;
	            //Toast.makeText(ActivityBuildPlan.this, "BAM - service un-bound", Toast.LENGTH_SHORT).show();
	        }
	    };
	    
	    
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
	        	
	        	Task myTask = new Task();
	        	myTask.setName("Name");
	        	myTask.setDescription("Description");
	        	myTask.addConstraint(new TaskConstraintLocation(1));
	        	myTask.addConstraint(new TaskConstraintAmenity(5));
	        	myTask.addConstraint(new TaskConstraintDate(new Date(110,1,24)));
	        	printTask(myTask);
	        	
	        	String temp = myTask.serialize();
	        	System.out.println(temp);
	        	Task myTask2 = Task.deserialize(temp);
	        	printTask(myTask2);
	        	System.out.println(myTask2.serialize());
	        	
	            // Tell the user about what we did.
	            Toast.makeText(ActivityBuildPlan.this, "done.",
	                    Toast.LENGTH_LONG).show();

	        }
	    };