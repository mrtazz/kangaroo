package com.kangaroo;

import com.kangaroo.RoutingInteraction;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class RoutingInteraction extends Service
{

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder 
    {
        RoutingInteraction getService() 
        {
            return RoutingInteraction.this;
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
    
    
    //implement Routing here
    public String routingTest(String text)
    {
    	return "Service Routing responds "+text;
    }

}
