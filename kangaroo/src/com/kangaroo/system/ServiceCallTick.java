package com.kangaroo.system;

import com.kangaroo.system.ServiceCallTick;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;

/**
 * This class provides a means to execute tasks that need to be called periodically. 
 * This service is started by "BroadcastReceiverBootTime" after the OS has finished loading.
 * 
 * @author alex
 */
public class ServiceCallTick extends android.app.Service
{
	private PendingIntent mAlarmSender = null;
	private int callIntervall;
	private SharedPreferences prefsPrivate = null;
	private String preferencesName = "Kagaroo_ServiceCallTick_Pref";
	
	/**
	 * This method is part of the Service-interface. It is called when an instance of the service
	 * is created. In this instance the registration of the service with the AlarmManager for periodical
	 * re-calling is done here.
	 */
    @Override
    public void onCreate() 
    {
    	System.out.println("ServiceCallTick onCreate called");	
    	prefsPrivate = getSharedPreferences(preferencesName, MODE_PRIVATE);
    	callIntervall = prefsPrivate.getInt("callIntervall", 60);
    	mAlarmSender = PendingIntent.getService(ServiceCallTick.this, 0, new Intent(ServiceCallTick.this, ServiceCallTick.class), 0);
    	startScheduled();
    }
	  
    /**
     * Call this when the service is terminated. (part of the Service interface)
     */
	@Override
    public void onDestroy() 
    {
		//safe our internal state for next time
		Editor prefsPrivateEditor = prefsPrivate.edit();
		prefsPrivateEditor.putInt("callIntervall", callIntervall);
		prefsPrivateEditor.commit();
    }
    
   /**
    * This is called when some ones sends a intent to us. 
    */
    @Override
	public void onStart(Intent intent, int startId)
	{
    	Intent callIntent = new Intent(ServiceCallTick.this, ServiceRecurringTask.class);
    	callIntent.putExtra("isLocation", false);
    	startService(callIntent);
	}
    
    /**
     * return the callIntervall variable. callIntervall specifies, that the service should be called every x seconds.
     * @return int: callIntervall
     */
    public int getCallIntervall() 
    {
		return callIntervall;
	}

    /**
     * set the callIntervall variable. callIntervall specifies, that the service should be called every x seconds.
     * @param int: callIntervall
     */
	public void setCallIntervall(int callIntervall) 
	{
		this.callIntervall = callIntervall;
		//safe our internal state for next time
		Editor prefsPrivateEditor = prefsPrivate.edit();
		prefsPrivateEditor.putInt("callIntervall", callIntervall);
		prefsPrivateEditor.commit();
		
		startScheduled();
	}

	/**
	 * Register the Service with the AlarmManager. Its onStartCommand() Method will be called
	 * every callIntervall seconds.
	 */
	public void startScheduled()
	{
		System.out.println("ServiceCallTick startScheduled called"); 
		// call us again every callIntervall seconds
        long firstTime = SystemClock.elapsedRealtime();
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, callIntervall*1000, mAlarmSender);
	}
	
	/**
	 * remove the service from the AlarmManager. If this is called, the service will no longer be started periodically.
	 */
    public void stopScheduled()
    {
    	//TODO test detaching from Alarm-manager
    	if(mAlarmSender != null)
    	{
        	AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
       	 	am.cancel(mAlarmSender);
    	}   
    }
	  
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
