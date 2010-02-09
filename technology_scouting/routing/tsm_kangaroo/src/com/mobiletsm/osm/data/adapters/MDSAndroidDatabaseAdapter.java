package com.mobiletsm.osm.data.adapters;

import java.util.Date;
import java.util.List;

import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobiletsm.osm.OsmHelper;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileNode;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;

public class MDSAndroidDatabaseAdapter extends MDSDatabaseAdapter {

	SQLiteDatabase database = null;
	
	
	@Override
	public void close() {
		if (database != null) {
			database.close();
		}
	}
	

	@Override
	public void loadAllStreetNodesAround(LatLon center) {
		Cursor cursor = database.rawQuery(SQL_loadAllStreetNodesAround(center), null);
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
	public void loadRoutingStreetNodesIncluding(long nodeId1, long nodeId2) {
		Cursor cursor = database.rawQuery(SQL_loadRoutingStreetNodesIncluding(nodeId1, nodeId2), null);
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
	public boolean open(String source) {
		try {
			database = SQLiteDatabase.openOrCreateDatabase(source, null);
			return database.isOpen();
		} catch (Exception e) {
			return false;
		}
	}

}
