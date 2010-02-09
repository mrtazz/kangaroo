package com.mobiletsm.osm.data;

public class MapTile {

	
	public static MapTile WHOLE_MAP = new MapTile(true);
	
	
	private boolean coversWholeMap = false; 
	
	
	public MapTile() {
		super();
	}
	
	
	public MapTile(boolean coversAll) {
		this.coversWholeMap = coversAll;
	}
	
	
	public boolean coversWholeMap() {
		return this.coversWholeMap;
	}
	
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MapTile)) {
			return false;
		} else {
			MapTile mapTile = (MapTile)object;
			return (this.coversWholeMap() == mapTile.coversWholeMap()); 
		}
	}
	
}
