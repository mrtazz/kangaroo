/**
 * 
 */
package com.mobiletsm.routing;

import java.util.Locale;

import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;

import com.google.gson.Gson;


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
	public static long ID_UNDEFINED = -1;
	
	
	/**
	 * id of corresponding open street map node 
	 */
	protected long osmNodeId = ID_UNDEFINED;
	
	
	/**
	 * true if corresponding open street map node is a street node
	 */
	protected boolean isOsmStreetNode = false;
	
	
	/**
	 * 
	 */
	protected long nearestOsmStreetNodeId = ID_UNDEFINED;
	
	
	/**
	 * 
	 */
	protected String name = null;
	
	
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
	
	
	public Place() {
		super();
		update(ID_UNDEFINED, ID_UNDEFINED);
	}
	
	
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
	
	
	/**
	 * create a place using given latitude and longitude
	 * @param latitude
	 * @param longitude
	 */
	public Place(double latitude, double longitude) {
		super();
		update(latitude, longitude);
	}
	
	
	/**
	 * create a place using the parameter of a Node
	 * @param node
	 */
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
	
	
	public String serialize()
	{
		Gson myJson = new Gson();
		return myJson.toJson(this);	
	}
	
	
	public static Place deserialize(String text)
	{
		Gson myJson = new Gson();
		return myJson.fromJson(text, Place.class);
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
		this.osmNodeId = ID_UNDEFINED;
		this.isOsmStreetNode = false;		
	}
	
		
	/**
	 * 
	 * @param id
	 */
	public void setNearestOsmStreetNodeId(long id) {
		nearestOsmStreetNodeId = id;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public long getNearestOsmStreetNodeId() {
		return nearestOsmStreetNodeId;
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
		return (osmNodeId != ID_UNDEFINED);
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
	 * returns the name associated with this place
	 * @return the name associated with this place
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * sets the name associated with this place
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	@Override
	public String toString() {
		if (name != null) {
			return name;
		} else {
			return String.format(Locale.US, "Place: {lat=%.7f, lon=%.7f}", latitude, longitude);
		}
	}
	
	
	/* TODO: remove this method to be independent from TSM */
	/**
	 * returns the position of this place as an TSM LatLon object
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
