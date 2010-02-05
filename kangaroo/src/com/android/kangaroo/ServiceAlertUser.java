package com.android.kangaroo;

import com.android.kangaroo.ServiceAlertUser;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ServiceAlertUser extends Service
{

	  /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder 
    {
        ServiceAlertUser getService() 
        {
            return ServiceAlertUser.this;
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) 
    {
        return mBinder;
    }
    
    @Override
    public void onCreate() 
    {

    }

    @Override
    public void onDestroy() 
    {
       
    }

    // This is the object that receives interactions from clients.
    private final IBinder mBinder = new LocalBinder();
    
    
    //implement User Notification here
    public String routingTest(String text)
    {
    	return "Service Calendar responds "+text;
    }

}
