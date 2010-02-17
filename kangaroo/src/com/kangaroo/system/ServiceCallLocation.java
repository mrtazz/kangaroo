package com.kangaroo.system;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class ServiceCallLocation extends Service
{

	private LocationManager locationManager;
	
	@Override
	public void onCreate() 
	{
		 locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		 locationManager.requestLocationUpdates("gps", 0, 0, locationListener);
	}
	
	//TODO register at LocatonProvider, so taht we are called when the location has changed X
	
	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) 
		{
			
		}

		@Override
		public void onProviderDisabled(String provider) 
		{
			
		}

		@Override
		public void onProviderEnabled(String provider) 
		{
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) 
		{
			
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	
	//TODO implement Thread that is executed on location events
	
}
