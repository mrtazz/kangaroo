package com.kangaroo.gui;

import com.android.kangaroo.R;
import com.kangaroo.gui.UserNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

public class UserNotification
{
	 	private NotificationManager mNM;
	 	private Context ctx;
	 	
	    public  UserNotification(Context myC) 
	    {     
	    	ctx = myC;
	    	mNM = (NotificationManager)ctx.getSystemService("notification");
	    }
	 	
	    
	    public void killNotification()
	    {
	    	 mNM.cancel(12345678);
	    }
	    
	    /**
	     * Show a notification while this service is running.
	     */
	    public void showNotification(String title, String textMessage, boolean vibrate_sound, Class okKlickActivity) 
	    {
	        Notification notification = new Notification(R.drawable.stat_happy, textMessage, System.currentTimeMillis());

	        // The PendingIntent to launch our activity if the user selects this notification
	        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, new Intent(ctx, okKlickActivity), 0);

	        // Set the info for the views that show in the notification panel.
	       notification.setLatestEventInfo(ctx, title, textMessage, contentIntent);
	       notification.tickerText = title;
	       if(vibrate_sound)
	       {
	    	   notification.defaults = notification.DEFAULT_SOUND | notification.DEFAULT_VIBRATE;
	       }
	       
	        // Send the notification.
	        mNM.notify(12345678, notification);
	    }
}
