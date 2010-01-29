/**
 * 
 */
package com.kangaroo.osm.data.searching;

import java.util.Iterator;

import org.openstreetmap.osm.Tags;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osm.data.searching.NearestStreetSelector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import android.util.Log;

/**
 * @author andreaswalz
 *
 */
public class LogNearestStreetSelector extends NearestStreetSelector {

	public String toString() {
        return "NearestStreetSelector";
    }

    /**
     * ${@inheritDoc}.
     */
    @Override
    public boolean isAllowed(final IDataSet aMap, final Node aNode) {
    	Log.v("MyTag", "isAllowed(node_id = " + aNode.getId() + ")");
        Iterator<Way> ways = aMap.getWaysForNode(aNode.getId());
        if (ways != null) {
            while (ways.hasNext()) {
            	Log.v("MyTag", "...hasNext()");
            	if (isAllowed(aMap, ways.next())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ${@inheritDoc}.
     */
    @Override
    public boolean isAllowed(final IDataSet aMap, final Way aWay) {
    	Log.v("MyTag", "isAllowed(way_id = " + aWay.getId() + ")");
        return (WayHelper.getTag(aWay, Tags.TAG_HIGHWAY) != null)
            && (WayHelper.getTag(aWay, Tags.TAG_NAME) != null);
    }

	

}
