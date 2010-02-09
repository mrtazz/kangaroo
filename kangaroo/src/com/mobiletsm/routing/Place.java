/**
 * 
 */
package com.mobiletsm.routing;

import org.openstreetmap.osmosis.core.domain.v0_6.Node;


/**
 * This class represents an entity that can be used to specify 
 * a geographical place. This can be a single point given by 
 * longitude/latitude, an openstreetmap node or a street. 
 * @author Andreas Walz
 *
 */
public class Place {
	
	/**
	 * 
	 */
	private long osmNodeId = -1;
	
	/**
	 * 
	 */
	private double latitude = 0;
	
	/**
	 * 
	 */
	private double longitude = 0;
		
	/**
	 * additional field that can be used to specify an extra time 
	 */
	public long time = 0;
	
	
	/**
	 * create a place using an openstreetmap node
	 * @param aNode
	 */
	public Place(Node node) {
		super();
		setFromOsmNode(node);
	}
	
	
	/**
	 * create a place using its coordinates
	 * @param lat
	 * @param lon
	 */
	public Place(double lat, double lon) {
		super();
		setCoordinates(lat, lon);
	}
	
	
	/**
	 * return the openstreetmap node specifying the place
	 * @return
	 */
	public long getOsmNodeId() {
		return osmNodeId;
	}
	
	
	/**
	 * 
	 * @param id
	 */
	public void setOsmNodeId(long id) {
		osmNodeId = id;
	}
	
	
	/**
	 * fix the place by specifying an openstreetmap node
	 * @param aNode the object representing the openstreetmap node
	 */
	public void setFromOsmNode(Node aNode) {
		setCoordinates(aNode.getLatitude(), aNode.getLongitude());
		osmNodeId = aNode.getId();
	}
	
	
	
	
	/**
	 * Returns true, if the place is specified by an openstreetmap node
	 * @return
	 */
	public boolean isOsmNode() {
		return (osmNodeId != -1);
	}
	
	
	/**
	 * Set coordinates of the place. An openstreetmap node 
	 * reference will be deleted
	 * @param lat
	 * @param lon
	 */
	public void setCoordinates(double lat, double lon) {
		osmNodeId = -1;
		latitude = lat;
		longitude = lon;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getLatitude() {
		return latitude;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getLongitude() {
		return longitude;
	}
	
	
	/**
	 * calculate the distance in meters between this place and an
	 * other target place
	 * @param place target place
	 * @return distance between this place and the place specified by place
	 */
	public double distanceTo(Place place) {		
		return distanceTo(place.getLatitude(), place.getLongitude());
	}
	
	
	/**
	 * calculate the distance in meters between this place an the 
	 * position specified by latitude lat and longitude lon
	 * @param lat
	 * @param lon
	 * @return
	 */
	public double distanceTo(double lat, double lon) {		
		return Place.distance(latitude, longitude, lat, lon);
	}
	
	
	
	/**
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	public static double distance(double lat1, double lon1, double lat2, double lon2) {
		// earth radius
		final double radius = 6378140;
		
		// scale factor between root-squared-lat-lon distance and distance in meters
		double latScaleFactor = 2 * Math.PI * radius / 360;
		double lonScaleFactor = 2 * Math.PI * (radius * Math.cos(Math.toRadians(lat1))) / 360;
		
		return Math.sqrt(
				Math.pow((lat1 - lat2) * latScaleFactor, 2) + 
				Math.pow((lon1 - lon2) * lonScaleFactor, 2));
	}
	
	
	
	/**
	 * check if this place lies within the bounding box given by the parameters
	 * @param aMinLat
	 * @param aMaxLat
	 * @param aMinLon
	 * @param aMaxLon
	 * @return
	 */
	public boolean isIn(double aMinLat, double aMaxLat, double aMinLon, double aMaxLon) {
		if ((latitude > aMaxLat) || (latitude < aMinLat)) 
			return false;
		
		if ((longitude > aMaxLon) || (longitude < aMinLon)) 
			return false;		
		
		return true;
	}

}
