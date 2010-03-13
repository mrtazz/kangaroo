package com.mobiletsm.osmosis.core.domain.v0_6;

import java.util.Collection;
import java.util.Date;

import org.openstreetmap.osmosis.core.domain.common.TimestampContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.store.StoreClassRegister;
import org.openstreetmap.osmosis.core.store.StoreReader;

public class MobileNode extends Node {

	/* additions introduced by MobileNode */
	
	public static final long ID_UNDEFINED = -1;
	
	
	private long nearestStreetNodeId = ID_UNDEFINED;
	
	
	public void setNearestStreetNodeId(long id) {
		nearestStreetNodeId = id;
	}
	
	
	public long getNearestStreetNodeId() {
		return nearestStreetNodeId;
	}
	
	
	
	/* constructors of superclass */
	
	public MobileNode(CommonEntityData entityData, double latitude,
			double longitude) {
		super(entityData, latitude, longitude);
		// TODO Auto-generated constructor stub
	}

	public MobileNode(long id, int version, Date timestamp, OsmUser user,
			long changesetId, Collection<Tag> tags, double latitude,
			double longitude) {
		super(id, version, timestamp, user, changesetId, tags, latitude, longitude);
		// TODO Auto-generated constructor stub
	}

	public MobileNode(long id, int version, Date timestamp, OsmUser user,
			long changesetId, double latitude, double longitude) {
		super(id, version, timestamp, user, changesetId, latitude, longitude);
		// TODO Auto-generated constructor stub
	}

	public MobileNode(long id, int version,
			TimestampContainer timestampContainer, OsmUser user,
			long changesetId, Collection<Tag> tags, double latitude,
			double longitude) {
		super(id, version, timestampContainer, user, changesetId, tags, latitude,
				longitude);
		// TODO Auto-generated constructor stub
	}

	public MobileNode(long id, int version,
			TimestampContainer timestampContainer, OsmUser user,
			long changesetId, double latitude, double longitude) {
		super(id, version, timestampContainer, user, changesetId, latitude, longitude);
		// TODO Auto-generated constructor stub
	}

	public MobileNode(StoreReader sr, StoreClassRegister scr) {
		super(sr, scr);
		// TODO Auto-generated constructor stub
	}
	
	
	
	/* simple constructor introduced by MobileNode */
	
	public MobileNode(long id, double lat, double lon) {
		super(id, 0, (Date)null, null, 0, lat, lon);
	}

}
