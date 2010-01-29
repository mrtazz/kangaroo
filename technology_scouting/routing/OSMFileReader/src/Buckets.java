import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;


public class Buckets {

	private IDataSet map;
	
	private double bucketSize_lat;
	private double bucketSize_lon;

	private List<Bucket> buckets = new Vector<Bucket>();
	
	
	public List<Bucket> getBuckets() {
		return buckets;
	}
	
	
	public Buckets(IDataSet map, double bucketSizeLat, double bucketSizeLon) {
		super();
		this.map = map;
		bucketSize_lat = bucketSizeLat;
		bucketSize_lon = bucketSizeLon;
	}	
	
	
	private static String repeat(String pattern, int count) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < count; i++)
			buf.append(pattern);
		return buf.toString();
	}
	
	public void drawBucketMatrix(PrintStream out) {
		int minLat = 0;
		int maxLat = 0;
		int minLon = 0;
		int maxLon = 0;
				
		if (buckets.size() > 0) {
			
			Bucket first = buckets.get(0);
			
			minLat = first.minLat;
			maxLat = first.maxLat;
			minLon = first.minLon;
			maxLon = first.maxLon;
			
			for (Bucket bucket : buckets) {
				if (bucket.minLat < minLat)
					minLat = bucket.minLat;
				if (bucket.maxLat > maxLat)
					maxLat = bucket.maxLat;
				if (bucket.minLon < minLon)
					minLon = bucket.minLon;
				if (bucket.maxLon > maxLon)
					maxLon = bucket.maxLon;
			}
			
			StringBuffer matrix = new StringBuffer();
			for (int lat = minLat; lat <= maxLat; lat++) {				
				StringBuffer line = new StringBuffer();
				for (int lon = minLon; lon <= maxLon; lon++) {
					int value = 0;
					
					for (Bucket bucket : buckets) {
						if (bucket.minLat == lat && bucket.minLon == lon) {
							value = bucket.getNumberOfNodes();
						}
					}

					if (value == 0)
						line.append("   ");
					else if (value > 0 && value < 10)
						line.append(" . ");
					else if (value >= 10 && value < 100)
						line.append(" * ");
					else if (value >= 100)
						line.append("***");
				}

				matrix.append(line.toString());
				matrix.append("\n");
			}
			out.println(matrix.toString());
		}
	}
	
	
	public void add(Node node) throws Exception {
		Iterator<Bucket> bucket_itr = buckets.iterator();
		while (bucket_itr.hasNext()) {
			Bucket bucket = bucket_itr.next();
			if (bucket.addIf(node))
				return;
		}		
		
		Bucket newBucket = new Bucket(map, bucketSize_lat, bucketSize_lon, node.getLatitude(), node.getLongitude());
		
		buckets.add(newBucket);
		if (!newBucket.addIf(node))
			throw new Exception("Node does not find any bucket (node_id = " + node.getId() + ")");
	}
	
	
	public void add(Way way) throws Exception {
		boolean fit = false;
		Iterator<Bucket> bucket_itr = buckets.iterator();
		while (bucket_itr.hasNext()) {
			Bucket bucket = bucket_itr.next();
			fit = bucket.addIf(way) || fit;
		}
		
		if (!fit)
			throw new Exception("Way does not find any bucket.");		
	}
}
