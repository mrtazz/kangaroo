package com.example.AndroidNearestStreet;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osm.data.searching.NearestStreetSelector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AndroidNearestStreet extends Activity {
    
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;
	
	private String mapFileName = "/sdcard/map-em.osm";
	private File mapFile = null;
	private MemoryDataSet map = null;
	
	private Node homeNode = null;
	private boolean calculationStarted = false;
	
	String msg = "";
	private int changes = 0;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);          
        
        // LocationManager holen
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        // add our gps status listener
		GpsListener gpsListener = new GpsListener();

		// add a locationlistener, just to enable the GPS
		locationListener = (LocationListener) gpsListener;
		
        
        TextView mTextView = (TextView) findViewById(R.id.textview);     
        
        mapFile = new File(mapFileName);        
        
        if (mapFile.exists()) {        	
        	Log.v("MyTag.main", "parsing map file \"" + mapFileName + "\"...");
        	
        	try {
        		map = (new FileLoader(mapFile)).parseOsm();
    			
    			Log.v("MyTag.main", "waiting for position...");
    			Log.v("MyTag.main", "# nodes = " + map.getNodesCount());
    			Log.v("MyTag.main", "# ways = " + map.getWaysCount());
    			
    			mTextView.setText(String.format("waiting for position...\n#nodes = %d\n#ways = %d", 
    					map.getNodesCount(), map.getWaysCount()));
    			
    		} catch (Exception e) {
    			mTextView.setText("exception = " + e.getMessage().toString() + "\n --> " + e.toString());
    		}        	
        } else {
        	mTextView.setText("map file (" + mapFileName + ") not found!");
        }   

    }
    
    
    @Override
	public void onPause() {
		super.onPause();

		// remove listeners
		locationManager.removeUpdates(locationListener);
	}
    
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	locationManager.requestLocationUpdates("gps", 0, 0, locationListener);
    }
    
    
    private class GpsListener implements LocationListener {    	
    	
    	private double lastUpdateLat = 0;
    	private double lastUpdateLon = 0;
    	private long lastUpdateTime = 0;
    	private boolean calculationStarted = false;

		private double distanceToLastUpdate(double lat, double lon) {
			
			final double radius = 6378140;
			
			double latScaleFactor = 2 * Math.PI * radius / 360;
			double lonScaleFactor = 2 * Math.PI * (radius * Math.cos(Math.toRadians(lat))) / 360;
			
			return Math.sqrt(
					Math.pow((lat - lastUpdateLat) * latScaleFactor, 2) + 
					Math.pow((lon - lastUpdateLon) * lonScaleFactor, 2));
		}
		
    	
    	private boolean newPositionRequired(double lat, double lon, long time) {
				
			if (!calculationStarted)
				return true;
			
			if (time - lastUpdateTime > 30000)
				return true;
			
			if ((distanceToLastUpdate(lat, lon) > 300) && (time - lastUpdateTime > 15000))
				return true;		
			
			return false;
		}
		
    	
    	// LocationListener implements
		public void onLocationChanged(Location location) {
						
			if(newPositionRequired(location.getLatitude(), location.getLongitude(), System.currentTimeMillis()))
			{				
				if (!calculationStarted) {
					lastUpdateLat = location.getLatitude();
					lastUpdateLon = location.getLongitude();
					lastUpdateTime = System.currentTimeMillis();
				}
				
				calculationStarted = true;
			}
			else
			{
				return;
			}
			
			long dt = System.currentTimeMillis();
			double timeToLastUpdate = (System.currentTimeMillis() - lastUpdateTime) / 1000;
			double distToLastUpdate = distanceToLastUpdate(location.getLatitude(), location.getLongitude());
						
			TextView mTextView = (TextView) findViewById(R.id.textview);
						
			if (map != null) {
				//if (homeNode == null) {					
				
					Node nearestStreetNode = map.getNearestNode(new LatLon(
							location.getLatitude(), location.getLongitude()),
							new NearestStreetSelector());

					Iterator<Way> wayitr;

					if (nearestStreetNode != null) {
						
						homeNode = nearestStreetNode;
						
						wayitr = map.getWaysForNode(nearestStreetNode.getId());

						StringBuffer result = new StringBuffer();
						
						while(wayitr.hasNext()) {						

							Way way = wayitr.next();						

							Iterator<Tag> itr = way.getTags().iterator();

							if (itr.hasNext())
								result.append(">");
							
							while (itr.hasNext()) {
								Tag tag = itr.next();
								result.append(" " + tag.getKey() + " = " + tag.getValue() + "\n");
							}
						}
						
						msg = result.toString();

					} else {
						msg = "found position, but no nearest street node";
					}

					lastUpdateLat = location.getLatitude();
					lastUpdateLon = location.getLongitude();
					lastUpdateTime = System.currentTimeMillis();
					
				//}
			} else {
				msg = "map not loaded";
			}			
						
			dt = System.currentTimeMillis() - dt;
			
			mTextView.setText(String.format("your position (update # %d):\nlat = %.7f\nlon = %.7f\n" +
				"provider = %s\ntime for calculation = %d ms\ndist to last upd = %.1f m\n" +
				"time to last upd = %.1f s\nresult:\n %s", 
				changes++, location.getLatitude(), location.getLongitude(), location.getProvider(), 
				dt, distToLastUpdate, timeToLastUpdate, msg));
			
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}