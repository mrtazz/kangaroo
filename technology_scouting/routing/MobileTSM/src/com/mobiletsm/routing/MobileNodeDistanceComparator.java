package com.mobiletsm.routing;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.coordinates.Coordinate;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;

public class MobileNodeDistanceComparator implements Comparator<Node> {

    /**
     * The node to compare the distance to.
     */
    private Node targetNode;
    
    
    private Map<Long, Double> bestDistanceFrom;

    /**
     * The map we operate on.
     */
    //private IDataSet myMap;

    /**
     * @param aTargetNode The node to compare the distance to.
     * @param aMap The map we operate on.
     * one for comparison.
     */
    public MobileNodeDistanceComparator(final IDataSet aMap, final Node aTargetNode, Map<Long, Double> bestDistancesFrom) {
        super();
        //this.myMap = aMap;
        this.targetNode = aTargetNode;
        this.bestDistanceFrom = bestDistancesFrom;
    }



    /**
     * @param stepA first node to compare
     * @param stepB second node to compare
     * @return -1 0 or 1 depending on the metrics of both
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(final Node stepA, final Node stepB) {

    	if (stepA.getId() == stepB.getId()) {
    		return 0;
    	}
    	
    	double a = getMetric(stepA);
        double b = getMetric(stepB);
        
        if (a < b) {
        	return -1;
        }
        if (b < a) {
            return +1;
        }
        return 0;
    }

    /**
     * @param nodeA the node to compure the distance for
     * @return the distance
     */
    private double getMetric(final Node nodeA) {
        double dist =  LatLon.distanceInMeters(nodeA, this.targetNode);	//  (nodeA.getLatitude(), nodeA.getLongitude(),
                                           //this.targetNode.getLatitude(), this.targetNode.getLongitude());
        
        if (bestDistanceFrom.containsKey(nodeA.getId())) {
        	dist += bestDistanceFrom.get(nodeA.getId());
        } else {
        	throw new RuntimeException("MobileNodeDistanceComparator.getMetric(nodeid:" + nodeA.getId() + 
        			"): node not found in bestDistanceFrom data set");
        }
        
        //System.out.println("MobileNodeDistanceComparator.getMetric(nodeid:" + nodeA.getId() + 
        //			"): dist = " + dist);
        
        return dist;
    }
	
}
