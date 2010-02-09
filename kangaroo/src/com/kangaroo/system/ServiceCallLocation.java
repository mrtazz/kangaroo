package com.kangaroo.system;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServiceCallLocation extends Service
{

	//TODO register at LocatonProvider, so taht we are called when the location has changed X
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	//TODO implement Thread that is executed on location events
	
}
