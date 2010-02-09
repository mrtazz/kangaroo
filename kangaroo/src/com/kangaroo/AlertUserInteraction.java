package com.kangaroo;

import com.android.kangaroo.R;
import com.kangaroo.AlertUserInteraction;
import com.kangaroo.gui.ActivityBuildPlan;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;
import android.content.Context;

public class AlertUserInteraction
{
	 	private NotificationManager mNM;
	 	private Context myContext;
	 	
	    public  AlertUserInteraction(Context con) 
	    {     
	    	myContext = con;
	    	mNM = (NotificationManager)myContext.getSystemService("notification");
	    }
	 	    
	    public void showToast(String text)
	    {
            Toast.makeText(myContext, text, Toast.LENGTH_LONG).show();
	    }
	    
	    public void killNotification()
	    {
	    	 mNM.cancel(12345678);
	    }
	    
	    /**
	     * Show a notification while this service is running.
	     */
	    public void showNotification(String title, String text_message) 
	    {
	        // In this sample, we'll use the same text for the ticker and the expanded notification
	        CharSequence text = title;

	        // Set the icon, scrolling text and timestamp
	        Notification notification = new Notification(R.drawable.stat_happy, text,
	                System.currentTimeMillis());

	        // The PendingIntent to launch our activity if the user selects this notification
	        PendingIntent contentIntent = PendingIntent.getActivity(myContext, 0,
	                new Intent(myContext, ActivityBuildPlan.class), 0);

	        // Set the info for the views that show in the notification panel.
	       notification.setLatestEventInfo(myContext, text_message,
	                       text, contentIntent);

	        // Send the notification.
	        // We use a layout id because it is a unique number.  We use it later to cancel.
	        mNM.notify(12345678, notification);
	    }

}
