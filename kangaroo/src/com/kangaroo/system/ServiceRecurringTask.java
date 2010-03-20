package com.kangaroo.system;

import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.RemoteException;

import com.kangaroo.ActiveDayPlan;
import com.kangaroo.DayPlanConsistency;
import com.kangaroo.calendar.CalendarAccessAdapter;
import com.kangaroo.calendar.CalendarAccessAdapterMemory;
import com.kangaroo.calendar.CalendarEvent;
import com.kangaroo.gui.ActivityBuildPlan;
import com.kangaroo.gui.UserNotification;
import com.kangaroo.task.Task;
import com.kangaroo.task.TaskConstraintDate;
import com.kangaroo.task.TaskConstraintDayTime;
import com.kangaroo.task.TaskConstraintDuration;
import com.kangaroo.task.TaskConstraintPOI;
import com.mobiletsm.osm.data.searching.POICode;
import com.mobiletsm.routing.AllStreetVehicle;
import com.mobiletsm.routing.MobileTSMRoutingEngine;
import com.mobiletsm.routing.NoRouteFoundException;
import com.mobiletsm.routing.Place;
import com.mobiletsm.routing.RouteParameter;
import com.mobiletsm.routing.RoutingEngine;
import com.mobiletsm.routing.Vehicle;

public class ServiceRecurringTask extends Service
{
	
	private Intent currentIntent;
	private PowerManager myPowerManager;
	private PowerManager.WakeLock myWakeLock;
	private SharedPreferences prefsPrivate = null;
	private String preferencesName = "kangaroo_config";
	private boolean semaphoreTaskAktive;
	private ActiveDayPlan currentDayPlan;
	private Vehicle currentVehicle;
	private UserNotification myUserNotification;
	private Integer consistencyMessageId = -1;
	private Integer complienceMessageId = -1;
	private int messageLevel = -1;
	
	/**
	 * Initialize the new Service-object here
	 */
	@Override
	public void onCreate() 
	{
		System.out.println("ServiceRecurringTask onCreate called");
		prefsPrivate = getSharedPreferences(preferencesName, MODE_PRIVATE);
		//variable = prefsPrivate.getInt("variable name", default_value);
		
		myPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
		semaphoreTaskAktive = false;
		
		//initialize the Dayplan
		currentDayPlan = new ActiveDayPlan();
		RoutingEngine re = new MobileTSMRoutingEngine();
		re.enableRoutingCache();
    	re.init("/sdcard/map-fr.db");
    	currentDayPlan.setRoutingEngine(re);
    	
        //CalendarAccessAdapter caa = new CalendarAccessAdapterAndroid(this);
    	CalendarAccessAdapter caa = new CalendarAccessAdapterMemory();
    	caa.setContext(getApplicationContext());
		currentDayPlan.setCalendarAccessAdapter(caa);
		
		currentVehicle = new AllStreetVehicle(5.0);
		
		myUserNotification = new UserNotification(getApplicationContext());
		
		fill_stuff();
	}
	
	private void fill_stuff()
	{
        CalendarEvent event1 = new CalendarEvent();
        event1.setStartDate(new Date(2010 - 1900, 2, 20, 19, 20));
        event1.setEndDate(new Date(2010 - 1900, 2, 20, 19, 30));
        event1.setLocationLatitude(48.00);
        event1.setLocationLongitude(7.852);
        event1.setTitle("BAM Title");

        CalendarEvent event2 = new CalendarEvent();
        event2.setStartDate(new Date(2010 - 1900, 2, 20, 20, 45));
        event2.setEndDate(new Date(2010 - 1900, 2, 20, 21, 00));
        event2.setLocationLatitude(48.000);
        event2.setLocationLongitude(7.852);

        CalendarEvent event3 = new CalendarEvent();
        event3.setStartDate(new Date(2010 - 1900, 2, 20, 21, 20));
        event3.setEndDate(new Date(2010 - 1900, 2, 20, 21, 40));
        event3.setLocationLatitude(47.987);
        event3.setLocationLongitude(7.852);

        CalendarEvent event4 = new CalendarEvent();
        event4.setStartDate(new Date(2010 - 1900, 2, 20, 21, 45));
        event4.setEndDate(new Date(2010 - 1900, 2, 20, 21, 50));
        event4.setLocationLatitude(47.987);
        event4.setLocationLongitude(7.852);        

        CalendarEvent event5 = new CalendarEvent();
        event5.setStartDate(new Date(2010 - 1900, 2, 20, 22, 0));
        event5.setEndDate(new Date(2010 - 1900, 2, 20, 22, 40));
        event5.setLocationLatitude(47.983);
        event5.setLocationLongitude(7.852);        

        CalendarEvent event6 = new CalendarEvent();
        event6.setStartDate(new Date(2010 - 1900, 2, 20, 23, 0));
        event6.setEndDate(new Date(2010 - 1900, 2, 20, 23, 40));
        event6.setLocationLatitude(48.983);
        event6.setLocationLongitude(7.852);  
        
        CalendarEvent event7 = new CalendarEvent();
        event7.setStartDate(new Date(2010 - 1900, 2, 20, 23, 45));
        event7.setEndDate(new Date(2010 - 1900, 2, 20, 23, 50));
        event7.setLocationLatitude(47.983);
        event7.setLocationLongitude(7.852); 
        
        currentDayPlan.addEvent(event1);
        currentDayPlan.addEvent(event2);
        currentDayPlan.addEvent(event3);
        currentDayPlan.addEvent(event4);
        currentDayPlan.addEvent(event5);
        currentDayPlan.addEvent(event6);
        currentDayPlan.addEvent(event7);        
                
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
		
		
		currentDayPlan.addTask(task1);
		currentDayPlan.addTask(task2);
		currentDayPlan.addTask(task3);
		currentDayPlan.addTask(task4);        
		currentDayPlan.addTask(task5);		
		currentDayPlan.addTask(task6);	
	}
	
	/**
	 * Safe the state of this object here, because it will be destroyed soon
	 */
	@Override
	public void onDestroy()
	{
		Editor prefsPrivateEditor = prefsPrivate.edit();
		//prefsPrivateEditor.putInt("variable name", variable);
		prefsPrivateEditor.commit();
	}
	
	
	/**
	 * This is called when the service is started via Context.startService(). This is the way we
	 * call it in this project. Do the recurring work here. This service is only then invoked, when the 
	 * ServiceCallTick and ServiceCallLocation think it is necessary to execute recurring task now.
	 * @param intent
	 * @param flags
	 * @param startId
	 * @return
	 */
	@Override
	public void onStart(Intent intent, int startId)
	{
		 
		 //only one Thread that checks/optimizes the plan is allowed at any time!
		 if(!semaphoreTaskAktive)
		 {
			semaphoreTaskAktive = true;
			currentIntent = intent;
	    	Thread thr = new Thread(null, backgroundTask, "ServiceRecurringTask Worker Thread");
	        thr.start();
		 }
	}
	
	/**
	 * Thread, in which the main work for the background task is done.
	 */
	Runnable backgroundTask = new Runnable()
    {
        public void run() 
        {
   		 	//obtain CPU-Lock to prevent the device from going to sleep while we are still routing
        	myWakeLock = myPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Kangaroo calculation lock");
   		 	myWakeLock.acquire();
        	
   		 	Location currentLocation = null;
        	Place currentPlace = null;
        	int minutes_left = -1;
    		if(currentIntent.getBooleanExtra("isLocation", false) == true)
    		{
    			System.out.println("ServiceRecurringTask: location");
    			//call from ServiceCallLocation, get Location info
    			currentLocation = (Location)currentIntent.getExtras().get("location");    			
    		}
    		else
    		{
    			//call from ServiceCallTick, no new Location provided
    			System.out.println("ServiceRecurringTask: time");
    			LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    			currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    		}
    		
    		if(!(currentLocation == null))
    		{
    			//check the dayplan here and deal with consistency and compliance problems
    			currentPlace = new Place(currentLocation.getLatitude(), currentLocation.getLongitude());
    			System.out.println("SRT: currentPlace: "+currentPlace.toString());
    			System.out.println(currentDayPlan.toString());
    			
    			//check consistency of the dayplan
    			DayPlanConsistency dpc = currentDayPlan.checkConsistency(currentVehicle, new Date());
    			if(consistencyMessageId != -1 && !dpc.hasNoConflicts())
    			{
    				myUserNotification.killNotification(consistencyMessageId);
    				consistencyMessageId = -1;
    			}
    			if(!dpc.hasNoConflicts())
    			{
    				consistencyMessageId = myUserNotification.showNotification("Inconsistent Events", dpc.toString(), false, ActivityBuildPlan.class);
        			System.out.println(dpc.toString());
    			}
    			
    			//check complience here
    			try 
    			{
    				minutes_left = currentDayPlan.checkComplianceWith(new Date(), currentPlace, currentVehicle);
    			} catch (NoRouteFoundException e) 
    			{
    				e.printStackTrace();
    			}
        		
    			boolean message_show = false;
    			boolean message_ping = false;
    			String message_text = "";
    			String message_title = "";
    			if(minutes_left < 0)
    			{
    				//its too late. tell the user anyway
    				message_show = true;
    				message_title = "too late";
    				message_text = RouteParameter.durationToString(minutes_left) + " too late for event " + currentDayPlan.getNextEvent(new Date()).getTitle();
    				if(messageLevel != 4)
    				{
    					message_ping = true;
    					messageLevel = 4;
    				}
    				
    			}
    			else if(minutes_left < 5)
    			{
    				//last warning
    				message_show = true;
    				message_title = "get going";
    				message_text = RouteParameter.durationToString(minutes_left) + " remaining for event " + currentDayPlan.getNextEvent(new Date()).getTitle();
    				if(messageLevel != 3)
    				{
    					message_ping = true;
    					messageLevel = 3;
    				}
    			}
    			else if(minutes_left < 15)
    			{
    				//second message
    				message_show = true;
    				message_title = "upcomming event";
    				message_text = RouteParameter.durationToString(minutes_left) + " remaining for event " + currentDayPlan.getNextEvent(new Date()).getTitle();
    				if(messageLevel != 2)
    				{
    					message_ping = true;
    					messageLevel = 2;
    				}
    			}
    			else if(minutes_left < 30)
    			{
    				//first message, <30
    				message_show = true;
    				message_title = "event reminder";
    				message_text = RouteParameter.durationToString(minutes_left) + " remaining for event" + currentDayPlan.getNextEvent(new Date()).getTitle();
    				if(messageLevel != 1)
    				{
    					message_ping = true;
    					messageLevel = 1;
    				}
    			}
    			
    			if(message_show)
    			{
        			if(message_ping == true)
        			{
        				if(complienceMessageId != -1)
            			{
            				myUserNotification.killNotification(complienceMessageId);
            				complienceMessageId = -1;
            			}
            			complienceMessageId = myUserNotification.showNotification(message_title, message_text, false, ActivityBuildPlan.class);    
        			}
        			else
        			{
        				if(complienceMessageId != -1)
            			{
            				myUserNotification.updateNotification(complienceMessageId, message_title, message_text, false, ActivityBuildPlan.class);
            			}
        				 
        			}
    								
    			}

    		}
    		else
    		{
    			System.out.println("currentLocation is null");
    		}
        
       		//it is really important to release the WakeLock after we are done!
    		semaphoreTaskAktive = false;
   		    myWakeLock.release();	
        }
    };	
	
	/**
	 * Return this object to interact with the service.
	 */
    @Override
    public IBinder onBind(Intent intent) 
    {
        return mBinder;
    }

    /**
     * This is the object that receives interactions from clients.  See RemoteService
     * for a more complete example.
     */
    private final IBinder mBinder = new Binder() 
    {
        @Override
		protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException 
		{
            return super.onTransact(code, data, reply, flags);
        }
    };
}
