package com.kangaroo.system;

import com.kangaroo.AlertUserInteraction;
import com.kangaroo.system.ServiceCallTick;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;

public class ServiceCallTick extends Service
{
	private PendingIntent mAlarmSender = null;
	private int callIntervall = 10;
	private AlertUserInteraction myAlertService; 
	private int temp=0;
	
    @Override
    public void onCreate() 
    {
        //if(mAlarmSender == null) //only the first time!
        //{
        	temp = 1;
        	myAlertService = new AlertUserInteraction(this);
        	mAlarmSender = PendingIntent.getService(ServiceCallTick.this, 0, new Intent(ServiceCallTick.this, ServiceCallTick.class), 0);
        	 // call us again every callIntervall seconds
            long firstTime = SystemClock.elapsedRealtime();
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, callIntervall*1000, mAlarmSender);
        //}
        
        Thread thr = new Thread(null, mTask, "ServiceCallTick Worker Thread");
        thr.start();
    }

    @Override
    public void onDestroy() 
    {

    }

    /**
     * The function that runs in our worker thread
     */
    Runnable mTask = new Runnable() 
    {
        public void run() 
        {
            //TODO add work here
        	System.out.println("text"+String.valueOf(temp));
        	Looper.prepare();
        	myAlertService.showToast("text"+String.valueOf(temp));
        	temp++;
        	
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

    public void stopScheduled()
    {
    	//TODO implement detaching from Alarm-manager
    }
    
    
    
    
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
