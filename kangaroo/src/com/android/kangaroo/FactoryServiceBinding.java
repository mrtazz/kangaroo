package com.android.kangaroo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class FactoryServiceBinding extends Activity
{
	public FactoryServiceBinding()
	{
		//Constructor

	}
	
	private ServiceRoute myServiceRoute = null;
	private ServiceCalendar myServiceCalendar = null;
	private ServiceAlertUser myServiceAlertUser = null;
	
    private ServiceConnection serviceRouteCon = new ServiceConnection() 
    {
        public void onServiceConnected(ComponentName className, IBinder service) 
        {
        	myServiceRoute = ((ServiceRoute.LocalBinder)service).getService();
            //Toast.makeText(ActivityBuildPlan.this, "BAM - service bound",Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) 
        {
        	myServiceRoute = null;
            //Toast.makeText(ActivityBuildPlan.this, "BAM - service un-bound", Toast.LENGTH_SHORT).show();
        }
    };
    
    private ServiceConnection serviceCalendarCon = new ServiceConnection() 
    {
        public void onServiceConnected(ComponentName className, IBinder service) 
        {
        	myServiceCalendar = ((ServiceCalendar.LocalBinder)service).getService();
            //Toast.makeText(ActivityBuildPlan.this, "BAM - service bound",Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) 
        {
        	myServiceCalendar = null;
            //Toast.makeText(ActivityBuildPlan.this, "BAM - service un-bound", Toast.LENGTH_SHORT).show();
        }
    };
    
    private ServiceConnection serviceAlertUserCon = new ServiceConnection() 
    {
        public void onServiceConnected(ComponentName className, IBinder service) 
        {
        	myServiceAlertUser = ((ServiceAlertUser.LocalBinder)service).getService();
            //Toast.makeText(ActivityBuildPlan.this, "BAM - service bound",Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) 
        {
        	myServiceAlertUser = null;
            //Toast.makeText(ActivityBuildPlan.this, "BAM - service un-bound", Toast.LENGTH_SHORT).show();
        }
    };
	
	public ServiceRoute getServiceRoute(Activity self)
	{
		bindService(new Intent(FactoryServiceBinding.this, ServiceRoute.class), serviceRouteCon, Context.BIND_AUTO_CREATE);
		//while(myServiceRoute == null);
		return myServiceRoute;
	}
	
	public ServiceCalendar getServiceCalendar(Activity self)
	{
		bindService(new Intent(self, ServiceCalendar.class), serviceCalendarCon, Context.BIND_AUTO_CREATE);
		while(myServiceCalendar == null);
		return myServiceCalendar;
	}
	
	public ServiceAlertUser getServiceAlertUser(Activity self)
	{
		bindService(new Intent(self, ServiceAlertUser.class), serviceAlertUserCon, Context.BIND_AUTO_CREATE);
		while(myServiceAlertUser == null);
		return myServiceAlertUser;
	}
	
	public int releaseServiceRoute()
	{
		unbindService(serviceRouteCon);
		return 0;
	}
	
	public int releaseServiceCalendar()
	{
		unbindService(serviceCalendarCon);
		return 0;
	}
	
	public int releaseServiceAlertUser()
	{
		unbindService(serviceAlertUserCon);
		return 0;
	}
}
