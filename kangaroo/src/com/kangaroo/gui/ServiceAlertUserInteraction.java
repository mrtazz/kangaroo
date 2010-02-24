package com.kangaroo.gui;

import com.android.kangaroo.R;
import com.kangaroo.gui.ServiceAlertUserInteraction;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.Vibrator;
import android.widget.Toast;
import android.content.Context;

public class ServiceAlertUserInteraction extends Service
{
	 	private NotificationManager mNM;
	 	
	 	@Override
	    public  void onCreate() 
	    {     
	    	mNM = (NotificationManager)getSystemService("notification");
	    }
	 	
	    
	    public void killNotification()
	    {
	    	 mNM.cancel(12345678);
	    }
	    
	    /**
	     * Show a notification while this service is running.
	     */
	    public void showNotification(String title, String textMessage, Class okKlickActivity) 
	    {
	        Notification notification = new Notification(R.drawable.stat_happy, textMessage, System.currentTimeMillis());

	        // The PendingIntent to launch our activity if the user selects this notification
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ActivityBuildPlan.class), 0);

	        // Set the info for the views that show in the notification panel.
	       notification.setLatestEventInfo(this, title, textMessage, contentIntent);

	        // Send the notification.
	        mNM.notify(12345678, notification);
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
