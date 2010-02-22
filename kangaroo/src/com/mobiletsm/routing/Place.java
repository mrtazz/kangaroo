/**
 * 
 */
package com.mobiletsm.routing;

import org.openstreetmap.osm.data.coordinates.LatLon;
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
	 * id of corresponding open street map node 
	 */
	protected long osmNodeId;
	
	
	/**
	 * true if corresponding open street map node is a street node
	 */
	protected boolean isOsmStreetNode;
	
	
	/**
	 * latitude 
	 */
	protected double latitude;
	
	
	/**
	 * longitude
	 */
	protected double longitude;
		
	
	/**
	 * additional field that can be used to specify an extra time 
	 */
	public long time = 0;
	
	
	/**
	 * creates a new place object taking its parameters from an open street map node
	 * 
	 * @param node
	 * @param isOsmStreetNode
	 */
	public Place(Node node, boolean isOsmStreetNode) {
		super();
		update(node, isOsmStreetNode);
	}
	
	
	public Place(double latitude, double longitude) {
		super();
		update(latitude, longitude);
	}
	
	
	public Place(Node node) {
		this(node, false);
	}
	
	
	public Place(Place place) {
		super();
		this.latitude = place.latitude;
		this.longitude = place.longitude;
		this.osmNodeId = place.osmNodeId;
		this.isOsmStreetNode = place.isOsmStreetNode;
	}
	
		
	public void update(Node node, boolean isOsmStreetNode) {
		this.latitude = node.getLatitude();
		this.longitude = node.getLongitude();
		this.osmNodeId = node.getId();
		this.isOsmStreetNode = isOsmStreetNode;	
	}
	
	
	public void update(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.osmNodeId = -1;
		this.isOsmStreetNode = false;		
	}
	
		
	/**
	 * returns the open street map node id
	 * 
	 * @return
	 */
	public long getOsmNodeId() {
		return osmNodeId;
	}
	
	
	/**
	 * returns true if this place is an open street map node
	 * 
	 * @return
	 */
	public boolean isOsmNode() {
		return (osmNodeId != -1);
	}

	
	/**
	 * returns true if this place is an openstreetmap street node
	 * @return
	 */
	public boolean isOsmStreetNode() {
		return isOsmStreetNode;
	}
	
	/**
	 * returns the latitude of this place
	 * 
	 * @return
	 */
	public double getLatitude() {
		return latitude;
	}
	
	
	/**
	 * returns the longitude of this place
	 * 
	 * @return
	 */
	public double getLongitude() {
		return longitude;
	}
	
	
	/**
	 * returns the position of this place as an LatLon object
	 * 
	 * @return
	 */
	public LatLon getLatLon() {
		return new LatLon(getLatitude(), getLongitude());
	}
	
	
	/**
	 * returns the (approximate) distance in meters between this place
	 * and the one given by the parameter place
	 * 
	 * @param place target place
	 * @return distance between this place and the place specified by place
	 */
	public double distanceTo(Place place) {		
		return distanceTo(place.getLatitude(), place.getLongitude());
	}
	
	
	/**
	 * returns the (approximate) distance in meters between this place an the 
	 * position on earth specified by latitude lat and longitude lon
	 * 
	 * @param lat
	 * @param lon
	 * @return
	 */
	public double distanceTo(double lat, double lon) {		
		return Place.distance(latitude, longitude, lat, lon);
	}
		
	
	/**
	 * returns the (approximate) distance between the two positions on earth
	 * given by (lat1, lon1) and (lat2, lon2)
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
	 * returns true if latitude and longitude of this place are within the
	 * intervals (aMinLat, aMaxLat) and (aMinLon, aMaxLon)
	 * 
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
