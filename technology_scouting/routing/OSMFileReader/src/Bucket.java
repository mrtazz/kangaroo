import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;


public class Bucket implements Comparable<Bucket> {

	private double bucketSize_lat;
	private double bucketSize_lon;
	
	public double getMinLat() {
		return minLat * bucketSize_lat;
	}
	
	public double getMaxLat() {
		return maxLat * bucketSize_lat;
	}
	
	public double getMinLon() {
		return minLon * bucketSize_lon;
	}
	
	public double getMaxLon() {
		return maxLon * bucketSize_lon;
	}
	
	public int minLat;
	public int maxLat;
	public int minLon;
	public int maxLon;
	
	private Collection<Node> nodes = new Vector<Node>();
	private Collection<Way> ways = new Vector<Way>();
	
	private IDataSet map;
	
	public Bucket(IDataSet map, double bucketSize_lat, double bucketSize_lon, double lat, double lon) {
		super();
		this.map = map;
		
		this.bucketSize_lat = bucketSize_lat;
		this.bucketSize_lon = bucketSize_lon;
		
		int lat_int = (int)(lat / bucketSize_lat);
		int lon_int = (int)(lon / bucketSize_lon);
		
		this.minLat = lat_int;
		this.maxLat = lat_int + 1;
		this.minLon = lon_int;
		this.maxLon = lon_int + 1;
	}
	
	
	public boolean isForBucket(Node node) {
		if (node.getLatitude() >= getMinLat() && node.getLatitude() <= getMaxLat() &&
				node.getLongitude() >= getMinLon() && node.getLongitude() <= getMaxLon())
			return true;
		else
			return false;
	}
	
	
	public boolean isForBucket(Way way) {
		Collection<WayNode> wayNodes = way.getWayNodes();
		Iterator<WayNode> wayNodes_itr = wayNodes.iterator();
		while (wayNodes_itr.hasNext()) {
			if (isForBucket(map.getNodeByID(wayNodes_itr.next().getNodeId())))
				return true;
		}
		return false;
	}

	

	public boolean addIf(Node node) {
		if (!isForBucket(node))
			return false;
		Iterator<Node> nodes_itr = nodes.iterator();
		while (nodes_itr.hasNext()) {
			if (nodes_itr.next().getId() == node.getId())
				return false;
		}
		nodes.add(node);
		return true;
	}
	
	
	public boolean addIf(Way way) {
		if (!isForBucket(way)) 
			return false;
		Iterator<Way> ways_itr = ways.iterator();
		while (ways_itr.hasNext()) {
			if (ways_itr.next().getId() == way.getId())
				return false;
		}
		ways.add(way);
		return true;
	}
	
	
	public int getNumberOfNodes() {
		return nodes.size();
	}
	
	
	public int getNumberOfWays() {
		return ways.size();
	}
	
	
	@Override
	public String toString() {
		return "Bucket(" + 
			String.format(Locale.US, "%.3f ² lat ² %.3f, %.3f ² lon ² %.3f", 
			getMinLat(), getMaxLat(), getMinLon(), getMaxLon()) +  
			", #nodes = " + getNumberOfNodes() + ", #ways = " + getNumberOfWays() + ")";
	}


	public int compareTo(Bucket o) {
		if (minLat < o.minLat)
			return -1;
		if (minLat > o.minLat)
			return 1;
		if (minLon < o.minLon)
			return -1;
		if (minLon > o.minLon)
			return 1;
		return 0;
	}
}
