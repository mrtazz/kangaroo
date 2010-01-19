/**
 * 
 */
package com.kangaroo.techscout.routing;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.osm.data.IDataSet;

import com.kangaroo.routing.Place;
import com.kangaroo.routing.Vehicle;

import android.location.Location;
import android.location.LocationListener;
import android.util.Log;

/**
 * This class is used to simulate the movement of a potential
 * user using a specified vehicle. 
 * @author Andreas Walz
 *
 */
public class MovementSimulator implements Runnable {
	
	/**
	 * distance within which a turning point is considered to be reached.
	 * If distance between current position and this turning point is below
	 * this constant, movement is turned towards the next turning point.
	 */
	final double MIN_DIST = 100;
	
	/**
	 * list of additional turning points
	 */
	private List<Place> turningPoints = null;
	
	/**
	 * Iterator over all turning points
	 */
	Iterator<Place> iterator = null;
	
	/**
	 * starting point
	 */
	private Place startingPoint = null;
	
	/**
	 * destination
	 */
	private Place destinationPoint = null;
	
	
	/**
	 * specifies the latitude of the lower bound of movement 
	 */
	private double minLat = 0;
	
	/**
	 * specifies the latitude of the upper bound of movement
	 */
	private double maxLat = 0;
	
	/**
	 * specifies the longitude of the left bound of movement
	 */
	private double minLon = 0;
	
	/**
	 * specifies the longitude of the right bound of movement
	 */
	private double maxLon = 0;
	
	/**
	 * specifies the vehicle that is used for the movement
	 */
	private Vehicle vehicle = null;
	
	/**
	 * the map that covers the area of movement
	 */
	private IDataSet map = null;
	
	/**
	 * the location listener that is set informed of location changes
	 */
	private LocationListener locationListener = null;
	
	/**
	 * 
	 */
	private String provider = null;
	
	/**
	 * the minimum time that has to elapse between two messages
	 * to the location listener
	 */
	private long minUpdateTime = 0;
	
	/**
	 * the minimum distance that has to be passed between two messages
	 * to the location listener
	 */
	private float minUpdateDistance = 0;
	
	/** 
	 * latitude of the current position
	 */
	private double latitude = 0;
	
	/** 
	 * longitude of the current position
	 */
	private double longitude = 0;
	
	/**
	 * current time
	 */
	private long time = 0;
	
	
	/**
	 * 
	 */
	private long lastUpdateTime = 0;
	private double lastUpdateLat = 0;
	private double lastUpdateLon = 0;
	
	
	/**
	 * next place in time and space to direct to
	 */
	private Place nextPlace = null;
	
	
	/**
	 * get current system time in milliseconds
	 * @return
	 */
	public long getTime() {
		return System.currentTimeMillis();
	}
	
	
	
	/**
	 * set the bounds of the movement
	 * @param aMinLat the latitude of the lower bound of movement 
	 * @param aMaxLat the latitude of the upper bound of movement
	 * @param aMinLon the longitude of the left bound of movement
	 * @param aMaxLon the longitude of the right bound of movement
	 */
	public void setBoundings(double aMinLat, double aMaxLat, double aMinLon, double aMaxLon) {
		minLat = aMinLat;
		maxLat = aMaxLat;
		minLon = aMinLon;
		maxLon = aMaxLon;
	}

	
	/**
	 * set the map that covers the area of movement
	 * @param aMap the map that covers the area of movement
	 */
	public void setMap(IDataSet aMap) {
		map = aMap;
	}
	
	
	/**
	 * return the map that covers the area of movement 
	 * @return the map that covers the area of movement
	 */
	public IDataSet getMap() {		
		return map;
	}
	
	
	/**
	 * Set the vehicle that will be simulated. In particular the maximum
	 * speed specified by this vehicle won't be exceeded.
	 * @param aVehicle
	 */
	public void setVehicle(Vehicle aVehicle) {
		vehicle = aVehicle;
	}
	
	
	/**
	 * 
	 * @param provider
	 * @param minTime
	 * @param minDistance
	 * @param listener
	 */
	public void requestLocationUpdates(String aProvider, long minTime, float minDistance, LocationListener listener) {
		provider = aProvider;
		minUpdateTime = minTime;
		minUpdateDistance = minDistance;
		locationListener = listener;
	}


	private Place getNextTurningPoint() {
		if (turningPoints == null)
			return destinationPoint;
		else {
			if (iterator == null) {
				iterator = turningPoints.iterator();
			}
			
			if (iterator.hasNext()) {
				return iterator.next();
			}
			
			return destinationPoint;
		}		
	}
	
	
	@Override
	public void run() {
		//TODO define specific exceptions
		if ((startingPoint == null) || (destinationPoint == null))
			return;
		
		/* set starting position and the next point to move to */
		latitude = startingPoint.getLatitude();
		longitude = startingPoint.getLongitude();
		nextPlace = getNextTurningPoint();
		
		
		// time offset between system time and time where simulation started
		long timeOffset = getTime();
		
		// time to next turning point in ms
		long dT;
		
		// time to next turning point in m
		double ds;
		
		// speed to next turning point in km/s
		double v;
		
		double speedScaleFactor = 1;
		
		double stepLatitude = 0;
		double stepLongitude = 0;
		boolean calcStepSize = true;		
		
		/* repeat until the destination point is reached and there are no more turning points to be visited */
		while(nextPlace != destinationPoint || destinationPoint.distanceTo(latitude, longitude) > MIN_DIST) {
			
			/* calculate the step size in latitude and longitude that are made every second */
			if (calcStepSize) {
				
				/* time left to reach the next turning point */
				dT = nextPlace.time + timeOffset - getTime();
				
				/* if it is already to late, give one second to get there */
				if (dT <= 0)
					dT = 1000;
				
				/* distance to the next turning point that has to be covered */
				ds = nextPlace.distanceTo(latitude, longitude);
				
				/* theoretical speed that is needed to get there in the given time span */
				v = ds / (double) dT * (1000 * 3.6);
				
					Log.v("MyTag", "------ dT =" + dT + ", ds = " + ds);
				
				/* do not exceed maximum speed of vehicle */
				if (v > vehicle.getMaxSpeed()) {
					speedScaleFactor = vehicle.getMaxSpeed() / v;
				}
				
					Log.v("MyTag", "v = " + v + ", speedScaleFactor = " + speedScaleFactor);
				
				stepLatitude = (nextPlace.getLatitude() - latitude) / ((double)dT / 1000) * speedScaleFactor;
				stepLongitude = (nextPlace.getLongitude() - longitude) / ((double)dT / 1000) * speedScaleFactor;
				
				calcStepSize = false;
			}
			
			/* make one step */
			latitude += stepLatitude;
			longitude += stepLongitude;	
			
			/* check if the location listener has to be informed */
			if ((lastUpdateTime == 0) || (((getTime() - lastUpdateTime) > minUpdateTime) && 
					(Place.distance(lastUpdateLat, lastUpdateLon, latitude, longitude)) > minUpdateDistance)) {
				
				if (locationListener != null) {					
					Location location = new Location(provider);
					location.setLatitude(latitude);
					location.setLongitude(longitude);
					
					lastUpdateTime = getTime();
					lastUpdateLat = latitude;
					lastUpdateLon = longitude;
					
					locationListener.onLocationChanged(location);
				}
			}			
			
			/* check if we reached this turning point */
			if (nextPlace.distanceTo(latitude, longitude) <= MIN_DIST) {							
				
				/* if so, turn towards the next one */
				nextPlace = getNextTurningPoint();
				calcStepSize = true;				
			}
			
			/* wait for one second */
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
		}		
	}

		
	
	/** 
	 * Set the point where the simulated movement will start from
	 * @param place The place specifying the start
	 * @param time 
	 */
	public void setStartingPoint(Place place, long time) {
		place.time = time;
		startingPoint = place;
	}
	
	
	/**
	 * Set the point where the simulated movement will stop
	 * @param place
	 * @param time
	 */
	public void setDestinationPoint(Place place, long time) {
		place.time = time;
		destinationPoint = place;
	}


	/**
	 * Specify an additional turning point	
	 * @param place
	 * @param time
	 */
	public void addTurningPoint(Place place, long time) {
		if (turningPoints == null) {
			turningPoints = new LinkedList<Place>();
		}
		
		place.time = time;
		turningPoints.add(place);
	}

	
	/**
	 * Create additional turning points using a parameter.
	 * To create non trivial turning points in a reproducible
	 * pattern, only the parameter will be used as an input 
	 * @param parameter
	 */
	public void createTurningPoints(int parameter) {
		
	}


	
} 
