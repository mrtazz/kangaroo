package com.kangaroo.system;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.RemoteException;

public class ServiceRecurringTask extends Service
{
	
	private Intent currentIntent;
	private PowerManager myPowerManager;
	private PowerManager.WakeLock myWakeLock;
	private SharedPreferences prefsPrivate = null;
	private String preferencesName = "kangaroo_config";
	private boolean semaphoreTaskAktive;
	
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
		 myWakeLock = myPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Kangaroo calculation lock");
		 myWakeLock.acquire();
		 
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
        	Location currentLocation = null;
    		if(currentIntent.getBooleanExtra("isLocation", false) == true)
    		{
    			System.out.println("ServiceRecurringTask: location");
    			//call from ServiceCallLocation, get Location info
    			currentLocation = (Location)currentIntent.getExtras().get("location");
    			//TODO do stuff here that we need to do when the location has changed
    			
    			}
    		else
    		{
    			//call from ServiceCallTick, no new Location provided
    			System.out.println("ServiceRecurringTask: time");
    			//TODO do stuff here that we need to do when the time has changed
    			
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
