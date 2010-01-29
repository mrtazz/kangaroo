/**
 * 
 */
package com.kangaroo.tsm.osm.io;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kangaroo.osmosis.core.domain.v0_6.KangarooOSMNode;
import com.kangaroo.routing.KangarooProjection;
import com.kangaroo.tsm.osm.data.KangarooTSMMemoryDataSet;

/**
 * @author andreaswalz
 *
 */
public class KangarooTSMFileLoader extends FileLoader {	
	
	public KangarooTSMFileLoader(File aFileName) {
		super(aFileName);
	}
		
	public KangarooTSMMemoryDataSet parseOsmKangarooTSM() {
		KangarooTSMDataSetSink sink = new KangarooTSMDataSetSink();

        parseOsm(sink);
        
        KangarooTSMMemoryDataSet dataSet = (KangarooTSMMemoryDataSet) sink.getDataSet();
        
        return dataSet;
    }
	
	public KangarooTSMMemoryDataSet readOsmDatabase(String filename) {
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(filename, null);
		
		KangarooTSMMemoryDataSet dataSet = new KangarooTSMMemoryDataSet();
		
		/* add nodes */
		Cursor nodeCursor = database.query("nodes", new String[] {"node_id", "lat", "lon", "tags"}, 
				null, null, null, null, null);
		if (nodeCursor.getCount() > 0) {
			
			int col_id = nodeCursor.getColumnIndex("node_id");
			int col_lat = nodeCursor.getColumnIndex("lat");
			int col_lon = nodeCursor.getColumnIndex("lon");
			int col_tags = nodeCursor.getColumnIndex("tags");
			
			for(nodeCursor.moveToFirst(); !nodeCursor.isAfterLast(); nodeCursor.moveToNext()) {				
				long nodeId = nodeCursor.getLong(col_id);		
				Node node = new Node(nodeId, 0, (Date)null, 
						null, 0, tagUnpacker(nodeCursor.getString(col_tags)), 
						nodeCursor.getDouble(col_lat), nodeCursor.getDouble(col_lon));
				
				dataSet.addNode(node);
			}
			
		}	
		nodeCursor.close();		
		Log.v("MyTag", "number of nodes = " + dataSet.getNodesCount());
		
			
		/* add ways */
		Cursor wayCursor = database.query("ways", new String[] {"way_id", "tags", "way_nodes"}, 
				null, null, null, null, null);
		if (wayCursor.getCount() > 0) {
			
			int col_way_id = wayCursor.getColumnIndex("way_id");
			int col_tags= wayCursor.getColumnIndex("tags");
			int col_way_nodes = wayCursor.getColumnIndex("way_nodes");
			
			for (wayCursor.moveToFirst(); !wayCursor.isAfterLast(); wayCursor.moveToNext()) {				
				long way_id = wayCursor.getLong(col_way_id);				
				String str = wayCursor.getString(col_way_nodes);
				List<WayNode> wayNodes = new LinkedList<WayNode>();
				
				for (int i = 0; i < str.length(); i+=8) {
		    		String long_str = str.substring(i, i+8);
		    		wayNodes.add(new WayNode(Long.decode("0x" + long_str)));  
		    	}
				
				Way way = new Way(way_id, 0, (Date)null, null, 0, tagUnpacker(wayCursor.getString(col_tags)), wayNodes);	
				dataSet.addWay(way);
			}			
		}
		wayCursor.close();
		Log.v("MyTag", "number of ways = " + dataSet.getWaysCount());
		
		database.close();
				
		return dataSet;
	}

	
	
	
	private Collection<Tag> tagUnpacker(String tags) {
		Collection<Tag> result = new LinkedList<Tag>();		
		
		String tag;
		
		while(tags.startsWith("|")) {
			int index = tags.indexOf("|", 1);			
			if (index > 1) {
				tag = tags.substring(0, index);
				tags = tags.substring(index);
			} else {
				tag = tags;
				tags = "";
			}			
			index = tag.indexOf("==");
			result.add(new Tag(tag.substring(1, index), tag.substring(index + 2)));
		}
		
		return result;
	}
	
}
