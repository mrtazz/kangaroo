package com.mobiletsm.osm.data.adapters;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobiletsm.osm.OsmHelper;
import com.mobiletsm.osm.data.searching.POICode;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileNode;
import com.mobiletsm.osmosis.core.domain.v0_6.MobileWay;
import com.mobiletsm.routing.Place;

public class RoutingAndroidSQLiteAdapter extends RoutingDBAdapter {

	SQLiteDatabase database = null;	
	
	
	@Override
	public boolean open(String source) {
		try {
			if (!isOpen()) {
				//database = SQLiteDatabase.openOrCreateDatabase(source, null);
				
				database = SQLiteDatabase.openDatabase(source, null, SQLiteDatabase.OPEN_READONLY);
				
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
	public int loadAllStreetNodesAround(Place center, double radius) {
		Cursor cursor = database.rawQuery(SQL_loadAllStreetNodesAround(center, radius), null);		
		int counter = cursor.getCount();
		if (counter > 0) {
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
		return counter;
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
	public List<Long> loadCompleteWaysForNodes(long fromNodeId, long toNodeId) {
		List<Long> allWays = new ArrayList<Long>();
		Cursor cursor = database.rawQuery(SQL_getWaysForNodes(fromNodeId, toNodeId), null);
		if (cursor.getCount() > 0) {
			int col_ways = cursor.getColumnIndex("ways");			
			
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {	
				String ways = cursor.getString(col_ways);
				List<Long> wayIds = OsmHelper.unpackStringToLongs(ways);
				allWays.addAll(wayIds);
				
				if (wayIds.size() == 1) {
					loadCompleteWay(wayIds.get(0));
				}				
			}			
		}
		cursor.close();
		return allWays;
	}

	
	@Override
	public void loadCompleteWay(long wayId) {
		Cursor cursor = database.rawQuery(SQL_loadCompleteWay(wayId), null);
		if (cursor.getCount() > 0) {			
			int col_id = cursor.getColumnIndex("id");
			int col_name = cursor.getColumnIndex("name");
			int col_highway = cursor.getColumnIndex("highway");
			int col_tags = cursor.getColumnIndex("tags");
			/*int col_wn = cursor.getColumnIndex("wn");*/
			int col_wn = cursor.getColumnIndex("waynodes");
			
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
	public void loadReducedWays(List<Long> ways) {		
		Cursor cursor = database.rawQuery(SQL_loadReducedWays(ways), null);
		if (cursor.getCount() > 0) {
			int col_id = cursor.getColumnIndex("id");
			int col_name = cursor.getColumnIndex("name");
			int col_highway = cursor.getColumnIndex("highway");
			int col_tags = cursor.getColumnIndex("tags");
			/*int col_wn_red = cursor.getColumnIndex("wn_red");*/
			int col_wn_red = cursor.getColumnIndex("waynodes_red");
			
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
	public void loadReducedWays() {
		loadReducedWays(null);
	}
	
	
	@Override
	public void loadAllEssentialStreetNodes() {
		Cursor cursor = database.rawQuery(sql_loadAllEssentialStreetNodes(), null);
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
	public void loadStreetNodes(long nodeId1, long nodeId2, boolean loadTags) {
		Cursor cursor = database.rawQuery(sql_loadStreetNodes(nodeId1, nodeId2, loadTags), null);
		if (cursor.getCount() > 0) {
			int col_id = cursor.getColumnIndex("id");
			int col_lat = cursor.getColumnIndex("lat");
			int col_lon = cursor.getColumnIndex("lon");
			int col_tags = -1;
			if (loadTags) {
				col_tags = cursor.getColumnIndex("tags");
			}
			
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


	@Override
	public int loadPOINodes(POICode poiCode) {
		Cursor cursor = database.rawQuery(sql_loadPOINodes(poiCode), null);
		int counter = cursor.getCount();
		if (cursor.getCount() > 0) {
			int col_id = cursor.getColumnIndex("id");
			int col_lat = cursor.getColumnIndex("lat");
			int col_lon = cursor.getColumnIndex("lon");
			int col_tags = cursor.getColumnIndex("tags");
			int col_nst = cursor.getColumnIndex("nst");
			
			for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {	
				long id = cursor.getLong(col_id);
				double lat = cursor.getDouble(col_lat);
				double lon = cursor.getDouble(col_lon);
				long nst = cursor.getLong(col_nst);
				MobileNode node = new MobileNode(id, lat, lon);

				String tags = cursor.getString(col_tags);
				node.getTags().addAll(OsmHelper.unpackStringToTags(tags));
				
				node.setNearestStreetNodeId(nst);
				node.setPOICode(poiCode);
				
				if (!poiNodes.containsKey(node.getId())) {
					poiNodes.put(node.getId(), node);
				}
			}	
		}
		cursor.close();
		return counter;
	}


}
