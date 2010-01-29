/**
 * 
 */
package com.kangaroo.routing;

import android.util.Log;

import com.kangaroo.osmosis.core.domain.v0_6.KangarooOSMNode;

/**
 * @author andreaswalz
 *
 */
public class KangarooProjection {

	/**
	 * 
	 */
	final double radius = 6378140;
	
	
	/**
	 * 
	 */
	private KangarooOSMNode center = null;
	
	
	/**
	 * 
	 * @param center
	 */
	public void setCenter(KangarooOSMNode center) {
		Log.v("MyTag", "KangarooOSMNode.center = " + center.toString());
		this.center = center;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public boolean hasCenter() {
		return (center != null);
	}
	
	
	/**
	 * 
	 * @param center
	 */
	public void project(KangarooOSMNode node) {
		node.long_lat = getLongLatitude(node.getLatitude());	//(new Double(((node.getLatitude() - center.getLatitude()) * latScaleFactor))).longValue();
		node.long_lon = getLongLongitude(node.getLongitude());	//(new Double(((node.getLongitude() - center.getLongitude()) * lonScaleFactor))).longValue();
	}

	
	public long getDistanceSquared(KangarooOSMNode node) {
		return 
			(node.long_lat - center.long_lat) * (node.long_lat - center.long_lat) + 
			(node.long_lon - center.long_lon) * (node.long_lon - center.long_lon);
	}
	
	
	public long getDistanceSquared(long long_lat, long long_lon) {
		return 
			(long_lat - center.long_lat) * (long_lat - center.long_lat) + 
			(long_lon - center.long_lon) * (long_lon - center.long_lon);
	}
	
	
	public long getDistanceSquared(long long_lat_1, long long_lon_1, long long_lat_2, long long_lon_2) {
		return 
			(long_lat_1 - long_lat_2) * (long_lat_1 - long_lat_2) + 
			(long_lon_1 - long_lon_2) * (long_lon_1 - long_lon_2);
	}
	
	
	public long getLongLatitude(double lat) {		
		double latScaleFactor = 2 * Math.PI * radius / 360;				
		return (new Double(((lat - center.getLatitude()) * latScaleFactor))).longValue();	
	}
	
	
	public long getLongLongitude(double lon) {
		double lonScaleFactor = 2 * Math.PI * (radius * Math.cos(Math.toRadians(center.getLatitude()))) / 360;		
		return (new Double(((lon - center.getLongitude()) * lonScaleFactor))).longValue();
	}
	
	
}
