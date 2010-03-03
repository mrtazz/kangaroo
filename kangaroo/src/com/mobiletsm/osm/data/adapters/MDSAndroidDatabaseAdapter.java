package com.mobiletsm.osm.data.adapters;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobiletsm.osm.OsmHelper;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileNode;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;
import com.mobiletsm.routing.Place;

public class MDSAndroidDatabaseAdapter extends MDSDatabaseAdapter {

	SQLiteDatabase database = null;	
	
	
	@Override
	public boolean open(String source) {
		try {
			if (!isOpen()) {
				database = SQLiteDatabase.openOrCreateDatabase(source, null);
				return isOpen();
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	
	@Override
	public boolean isOpen() {
		return (database != null && database.isOpen());
	}
	
	
	@Override
	public void close() {
		if (isOpen()) {
			database.close();
		}
	}
	
	
	@Override
	public void loadAllStreetNodesAround(Place center, double radius) {
		Cursor cursor = database.rawQuery(SQL_loadAllStreetNodesAround(center, radius), null);		
		if (cursor.getCount() > 0) {
			int col_id = cursor.getColumnIndex("id");
			int col_lat = cursor.getColumnIndex("lat");
			int col_lon = cursor.getColumnIndex("lon");
			
			System.out.println("MDSAndroidDatabaseAdapter.loadAllStreetNodesAround(): " +
					"cursor.getCount() = " + cursor.getCount());
			
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {	
				long id = cursor.getLong(col_id);
				double lat = cursor.getDouble(col_lat);
				double lon = cursor.getDouble(col_lon);
				Node node = new MobileNode(id, lat, lon);
				if (!streetNodes.containsKey(node.getId())) {
					streetNodes.put(node.getId(), node);
				}
			}			
		}
		cursor.close();
	}

	
	@Override
	public void loadAllStreetNodesForWays(long fromWayId, long toWayId) {
		String sql = SQL_loadAllStreetNodesForWays(fromWayId, toWayId);			
		if (sql != null) {
			Cursor cursor = database.rawQuery(sql, null);
			if (cursor.getCount() > 0) {
				int col_id = cursor.getColumnIndex("id");
				int col_lat = cursor.getColumnIndex("lat");
				int col_lon = cursor.getColumnIndex("lon");

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
						.moveToNext()) {
					long id = cursor.getLong(col_id);
					double lat = cursor.getDouble(col_lat);
					double lon = cursor.getDouble(col_lon);
					Node node = new MobileNode(id, lat, lon);
					if (!streetNodes.containsKey(node.getId())) {
						streetNodes.put(node.getId(), node);
					}
				}
			}
			cursor.close();
		}
	}

	
	@Override
	public void loadCompleteWaysForNodes(long fromNodeId, long toNodeId) {
		Cursor cursor = database.rawQuery(SQL_loadCompleteWaysForNodes(fromNodeId, toNodeId), null);
		if (cursor.getCount() > 0) {
			int col_ways = cursor.getColumnIndex("ways");			
			
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {	
				String ways = cursor.getString(col_ways);
				List<Long> wayIds = OsmHelper.unpackStringToLongs(ways);
				if (wayIds.size() == 1) {
					loadCompleteWay(wayIds.get(0));
				}				
			}			
		}
		cursor.close();
	}

	
	@Override
	public void loadCompleteWay(long wayId) {
		Cursor cursor = database.rawQuery(SQL_loadFullWay(wayId), null);
		if (cursor.getCount() > 0) {			
			int col_id = cursor.getColumnIndex("id");
			int col_name = cursor.getColumnIndex("name");
			int col_highway = cursor.getColumnIndex("highway");
			int col_tags = cursor.getColumnIndex("tags");
			int col_wn = cursor.getColumnIndex("wn");
			
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {	
				long id = cursor.getLong(col_id);				
				String name = cursor.getString(col_name);
				String highway = cursor.getString(col_highway);
				String tags = cursor.getString(col_tags);
				String wn = cursor.getString(col_wn);	
				
				Way way = new MobileWay(id, tags, wn);	
				OsmHelper.addSpecificTags(way, name, highway);
				addWayToMap(completeWays, way);
			}			
		}	
		cursor.close();
	}

	
	@Override
	public void loadReducedWays() {		
		Cursor cursor = database.rawQuery(SQL_loadReducedWays(), null);
		if (cursor.getCount() > 0) {
			int col_id = cursor.getColumnIndex("id");
			int col_name = cursor.getColumnIndex("name");
			int col_highway = cursor.getColumnIndex("highway");
			int col_tags = cursor.getColumnIndex("tags");
			int col_wn_red = cursor.getColumnIndex("wn_red");
			
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {	
				long id = cursor.getLong(col_id);				
				String name = cursor.getString(col_name);
				String highway = cursor.getString(col_highway);
				String tags = cursor.getString(col_tags);
				String wn_red = cursor.getString(col_wn_red);	
				
				Way way = new MobileWay(id, tags, wn_red);
				OsmHelper.addSpecificTags(way, name, highway);
				addWayToMap(reducedWays, way);
			}	
		}
		cursor.close();
	}

	
	@Override
	public void loadRoutingStreetNodes() {
		Cursor cursor = database.rawQuery(SQL_loadRoutingStreetNodes(), null);
		if (cursor.getCount() > 0) {
			int col_id = cursor.getColumnIndex("id");
			int col_lat = cursor.getColumnIndex("lat");
			int col_lon = cursor.getColumnIndex("lon");
			
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {	
				long id = cursor.getLong(col_id);
				double lat = cursor.getDouble(col_lat);
				double lon = cursor.getDouble(col_lon);
				Node node = new MobileNode(id, lat, lon);
				if (!streetNodes.containsKey(node.getId())) {
					streetNodes.put(node.getId(), node);
				}
			}			
		}
		cursor.close();
	}

	
	@Override
	public void loadNodes(long nodeId1, long nodeId2, boolean loadTags) {
		Cursor cursor = database.rawQuery(SQL_loadNodes(nodeId1, nodeId2, loadTags), null);
		if (cursor.getCount() > 0) {
			int col_id = cursor.getColumnIndex("id");
			int col_lat = cursor.getColumnIndex("lat");
			int col_lon = cursor.getColumnIndex("lon");
			int col_tags = -1;
			if (loadTags)
				col_tags = cursor.getColumnIndex("tags");
			
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {	
				long id = cursor.getLong(col_id);
				double lat = cursor.getDouble(col_lat);
				double lon = cursor.getDouble(col_lon);
				Node node = new MobileNode(id, lat, lon);

				String tags = null;
				if (loadTags) {
					tags = cursor.getString(col_tags);
					node.getTags().addAll(OsmHelper.unpackStringToTags(tags));
				}
				
				if (!streetNodes.containsKey(node.getId())) {
					streetNodes.put(node.getId(), node);
				}
			}			
		}
		cursor.close();
	}


}