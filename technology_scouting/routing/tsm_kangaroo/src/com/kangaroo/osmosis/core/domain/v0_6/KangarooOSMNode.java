/**
 * 
 */
package com.kangaroo.osmosis.core.domain.v0_6;

/*
 * This file was modified for the kangaroo project to meet demands.
 * 
 * Former package definition was
 * package com.bretth.osmosis.core.xml.v0_6;
 * 
 */

import java.util.Collection;
import java.util.Date;

import org.openstreetmap.osmosis.core.domain.common.TimestampContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.store.StoreClassRegister;
import org.openstreetmap.osmosis.core.store.StoreReader;

/**
 * @author andreaswalz
 *
 */
public class KangarooOSMNode extends Node {

	public KangarooOSMNode(CommonEntityData entityData, double latitude,
			double longitude) {
		super(entityData, latitude, longitude);
		// TODO Auto-generated constructor stub
	}

	public KangarooOSMNode(long id, int version, Date timestamp, OsmUser user,
			long changesetId, Collection<Tag> tags, double latitude,
			double longitude) {
		super(id, version, timestamp, user, changesetId, tags, latitude, longitude);
		// TODO Auto-generated constructor stub
	}

	public KangarooOSMNode(long id, int version, Date timestamp, OsmUser user,
			long changesetId, double latitude, double longitude) {
		super(id, version, timestamp, user, changesetId, latitude, longitude);
		// TODO Auto-generated constructor stub
	}

	public KangarooOSMNode(long id, int version,
			TimestampContainer timestampContainer, OsmUser user,
			long changesetId, Collection<Tag> tags, double latitude,
			double longitude) {
		super(id, version, timestampContainer, user, changesetId, tags, latitude,
				longitude);
		// TODO Auto-generated constructor stub
	}

	public KangarooOSMNode(long id, int version,
			TimestampContainer timestampContainer, OsmUser user,
			long changesetId, double latitude, double longitude) {
		super(id, version, timestampContainer, user, changesetId, latitude, longitude);
		// TODO Auto-generated constructor stub
	}

	public KangarooOSMNode(StoreReader sr, StoreClassRegister scr) {
		super(sr, scr);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * 
	 */
	public long long_lat = 0;

	
	/**
	 * 
	 */
	public long long_lon = 0;
	
}
