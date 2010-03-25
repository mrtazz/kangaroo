package com.kangaroo.system;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;

public class ServiceCallLocation extends Service
{

	private LocationManager locationManager;
	private Location currentLocation = null;
	private PendingIntent mLocationIntent;
	private int minUpdateTime;
	private float minUpdateDistance;
	private SharedPreferences prefsPrivate;
	private String preferencesName = "kangaroo_config";
	
	/*
	 * This is called when the Service is called and no running copy of it exists.
	 * The state of the internal variables is loaded from the preferences-store and system-services
	 * we need are fetched. The Service is registered with the LocationManager.
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() 
	{
		 System.out.println("ServiceCallLocation on Create called");
		 prefsPrivate = getSharedPreferences(preferencesName, MODE_PRIVATE);
		 if(prefsPrivate.getBoolean("background_call_enable" , true))
	     {
			 minUpdateTime = prefsPrivate.getInt("background_call_time_difference", 60);
			 minUpdateDistance = prefsPrivate.getFloat("background_call_position", 100);
		 
			 mLocationIntent = PendingIntent.getService(ServiceCallLocation.this, 0, new Intent(ServiceCallLocation.this, ServiceCallLocation.class), 0);
			 locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			 registerLocationManager();
	     }
	}
	
	/**
	 * This is called when the service is destroyed. Safe the internal state to the preferences-store.
	 */
	@Override
	public void onDestroy()
	{
		Editor prefsPrivateEditor = prefsPrivate.edit();
		prefsPrivateEditor.putInt("background_call_time_difference", minUpdateTime);
		prefsPrivateEditor.putFloat("background_call_position", minUpdateDistance);
		prefsPrivateEditor.commit();
	}
	
	/**
	 * The Service is registered with the LocationManager to get updated when the location of the device is changing.
	 */
	public void registerLocationManager()
	{
		locationManager.requestLocationUpdates("gps", minUpdateTime*1000, minUpdateDistance, mLocationIntent);
	}
	
	/**
	 * The Service is unregistered from the LocationManager. We no longer get location updates.
	 */
	public void unregisterLocationManager()
	{
		locationManager.removeUpdates(mLocationIntent);
	}
	
	/**
	 * This is called when some ones sends a intent to us. We fetch the current location (if available)
	 * and call the ServiceRecurringTask.
	 */
	@Override
	public void onStart(Intent intent, int startId)
	{
		System.out.println("ServiceCallLocation onStart called");
		
		Intent callIntent = new Intent(ServiceCallLocation.this, ServiceRecurringTask.class);
		if(intent.getExtras() != null)
		{
			currentLocation = null;
			currentLocation = (Location)intent.getExtras().get("location");
			if(currentLocation != null)
			{
				callIntent.putExtra("location", currentLocation);
			}
		}		
    	callIntent.putExtra("isLocation", true);
    	startService(callIntent);
	}
		
	
	/**
	 * GET-Method for the min. time between two location updates(in seconds)
	 * @return
	 */
	public int getMinUpdateTime() 
	{
		return minUpdateTime;
	}

	/**
	 * SET-Method for the min. time between two location updates(in seconds)
	 * @param minUpdateTime
	 */
	public void setMinUpdateTime(int minUpdateTime) 
	{
		this.minUpdateTime = minUpdateTime;
		Editor prefsPrivateEditor = prefsPrivate.edit();
		prefsPrivateEditor.putInt("background_call_time_difference", minUpdateTime);
		prefsPrivateEditor.commit();
		
		unregisterLocationManager();
		registerLocationManager();
	}

	/**
	 * GET-method for the min. distance between two location updates. (in meters)
	 * @return
	 */
	public float getMinUpdateDistance() 
	{
		return minUpdateDistance;
	}

	/**
	 * SET-method for the min. distance between two location updates. (in meters)
	 * @param minUpdateDistance
	 */
	public void setMinUpdateDistance(float minUpdateDistance) 
	{
		this.minUpdateDistance = minUpdateDistance;
		Editor prefsPrivateEditor = prefsPrivate.edit();
		prefsPrivateEditor.putFloat("background_call_position", minUpdateDistance);
		prefsPrivateEditor.commit();
		
		unregisterLocationManager();
		registerLocationManager();
	}

	/**
	 * When someone binds to this service, he receives this stub to call public methods
	 */
	@Override
	public IBinder onBind(Intent intent) 
	{
		// TODO Auto-generated method stub
		return null;
	}
    
	
}
