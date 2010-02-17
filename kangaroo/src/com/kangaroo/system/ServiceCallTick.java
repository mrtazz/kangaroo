package com.kangaroo.system;

import com.kangaroo.gui.AlertUserInteraction;
import com.kangaroo.system.ServiceCallTick;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
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
	private int callIntervall = 10;
	private AlertUserInteraction myAlertService; 
	
	/**
	 * This method is part of the Service-interface. It is called when an instance of the service
	 * is created. In this instance the registration of the service with the AlarmManager for periodical
	 * re-calling is done here.
	 */
    @Override
    public void onCreate() 
    {
        	myAlertService = new AlertUserInteraction(this);
        	mAlarmSender = PendingIntent.getService(ServiceCallTick.this, 0, new Intent(ServiceCallTick.this, ServiceCallTick.class), 0);
        	 // call us again every callIntervall seconds
            long firstTime = SystemClock.elapsedRealtime();
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, callIntervall*1000, mAlarmSender);
            
        Thread thr = new Thread(null, mTask, "ServiceCallTick Worker Thread");
        thr.start();
    }

    /**
     * When the Service is called by the AlarmManager, this function is executed in a new thread. 
     * (so that we don't disturb the GUI or other things running in the main thread)
     */
    Runnable mTask = new Runnable() 
    {
        public void run() 
        {
            //TODO add work here
        	Looper.prepare();
        	myAlertService.showToast("text");
        	
        	//TODO remove sleep-cycle
            long endTime = System.currentTimeMillis() + 5*1000;
            while (System.currentTimeMillis() < endTime) 
            {
                synchronized (mBinder) 
                {
                    try 
                    {
                        mBinder.wait(endTime - System.currentTimeMillis());
                    } 
                    catch (Exception e) 
                    {}
                }
            }

            // Done with our work...  stop this instance of the service!
            ServiceCallTick.this.stopSelf();
        }
    };
    
    
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
     * Call this when the service is terminated. (part of the Service interface)
     */
	@Override
    public void onDestroy() 
    {

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
