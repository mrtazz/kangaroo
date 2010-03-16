package com.mobiletsm.osmosis.core.domain.v0_6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.common.TimestampContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.CommonEntityData;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.OsmUser;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.store.StoreClassRegister;
import org.openstreetmap.osmosis.core.store.StoreReader;

import com.mobiletsm.osm.OsmHelper;

/**
 * 
 */

/**
 * @author andreaswalz
 *
 */
public class MobileWay extends Way {

	
	/* constructors from super class */ 
	
	public MobileWay(CommonEntityData entityData, List<WayNode> wayNodes) {
		super(entityData, wayNodes);
	}

	
	public MobileWay(CommonEntityData entityData) {
		super(entityData);
	}

	
	public MobileWay(long id, int version, Date timestamp, OsmUser user, long changesetId) {
		super(id, version, timestamp, user, changesetId);
	}

	
	public MobileWay(long id, int version, TimestampContainer timestampContainer, OsmUser user,
			long changesetId, Collection<Tag> tags, List<WayNode> wayNodes) {
		super(id, version, timestampContainer, user, changesetId, tags, wayNodes);
	}

	
	public MobileWay(long id, int version, TimestampContainer timestampContainer, OsmUser user,
			long changesetId) {
		super(id, version, timestampContainer, user, changesetId);
	}

	
	public MobileWay(StoreReader arg0, StoreClassRegister arg1) {
		super(arg0, arg1);
	}
	
	
	public MobileWay(long id, int version, Date timestamp, OsmUser user,
			long changesetId, Collection<Tag> tags, List<WayNode> wayNodes) {
		super(id, version, timestamp, user, changesetId, tags, wayNodes);
	}

	
	/* methods implemented by MobileWay */
	
	
	private Double maxSpeed = null;
	
	
	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = new Double(maxSpeed);
	}
	
	
	public boolean hasMaxSpeed() {
		return maxSpeed != null;
	}
	
	
	public double getMaxSpeed() {
		return maxSpeed.doubleValue();
	}	
	
	
	private String wayNodes = null;
	
	
	public MobileWay(long id) {
		super(id, 0, (Date)null, null, 0);
	}
	
	
	/**
	 * 
	 */
	public MobileWay(long id, String tags, String wayNodes) {
		this(id);		
		this.getTags().addAll(OsmHelper.unpackStringToTags(tags));
		this.wayNodes = wayNodes;
	}
	
	
	@Override
	public List<WayNode> getWayNodes() {
		List<WayNode> list = super.getWayNodes();
		if (list.size() == 0 && wayNodes != null) {
			list.addAll(OsmHelper.unpackStringToWayNodes(wayNodes));
		}
		return list;
	}
	
	
	/**
	 * 
	 * @param way
	 * @param map
	 */
	public MobileWay(Way way, IDataSet map) {
		super(way.getId(), way.getVersion(), way.getTimestamp(), way.getUser(), way.getChangesetId());
		this.getTags().addAll(way.getTags());
		
		WayNode lastWayNode = null;
		for (WayNode wayNode : way.getWayNodes()) {
			if (wayNode instanceof MobileWayNode) {
				this.getWayNodes().add(wayNode);
			} else {
				if (lastWayNode == null) {
					this.addWayNode(wayNode.getNodeId(), 0);
				} else {
					this.addWayNode(wayNode.getNodeId(), LatLon.distanceInMeters(map.getNodeByID(lastWayNode.getNodeId()), 
							map.getNodeByID(wayNode.getNodeId())));
				}
			}
			lastWayNode = wayNode;
		}
	}
	
	
	/**
	 * 
	 * @param wayNodeId
	 * @param distanceToPredecessor
	 */
	public void addWayNode(long wayNodeId, double distanceToPredecessor) {
		super.getWayNodes().add(new MobileWayNode(wayNodeId, distanceToPredecessor));
	}
	
		
	public String getWayNodeInfo() {
		StringBuffer buf = new StringBuffer("[");
		Iterator<WayNode> itr = this.getWayNodes().iterator();
		while (itr.hasNext()) {
			MobileWayNode wayNode = (MobileWayNode)itr.next();
			buf.append(String.format(Locale.US, "--%.4fm--> %d ", 
					wayNode.getDistanceToPredecessor(), wayNode.getNodeId()));
		}
		buf.append("]");
		return buf.toString();
	}	
	
	
	/**
	 * 
	 * @param fromNodeId
	 * @param toNodeId
	 * @return
	 */
	public double getPathLength(long fromNodeId, long toNodeId) {
		
		double length = 0;
		List<WayNode> wayNodes = super.getWayNodes();
			
		
		/* find index of nodes to start from and to go to */
		int fromNodeIndex = -1;
		int toNodeIndex = -1;
		for (int i = 0; i < wayNodes.size(); i++) {
			if (wayNodes.get(i).getNodeId() == fromNodeId)
				fromNodeIndex = i;
			if (wayNodes.get(i).getNodeId() == toNodeId)
				toNodeIndex = i; 
		}
		/* exit if start and end nodes are the same or one of them is not on the way */
		if (fromNodeIndex == -1 || toNodeIndex == -1 || fromNodeId == toNodeId) {
			System.out.println("wayId = " + this.getId());
			System.out.println("fromNodeIndex = " + fromNodeIndex + ", toNodeIndex = " + toNodeIndex);
			System.out.println("fromNodeId = " + fromNodeId + ", toNodeId = " + toNodeId);
			return -1;		
		}
		
		if (isClosed()) {
			
			int index = fromNodeIndex;			
			double lengthForward = Double.MAX_VALUE;
			double lengthReverse = Double.MAX_VALUE;
			
			if (!WayHelper.isReverseOneway(this)) {
				lengthForward = 0;
				while (wayNodes.get(index).getNodeId() != toNodeId) {
					if (++index == wayNodes.size()) index = 1;
					MobileWayNode mobileWayNode = (MobileWayNode)wayNodes.get(index);
					lengthForward += mobileWayNode.getDistanceToPredecessor();
				}
			}
			
			index = fromNodeIndex;
			if (!WayHelper.isOneway(this)) {
				lengthReverse = 0;
				while (wayNodes.get(index).getNodeId() != toNodeId) {
					MobileWayNode mobileWayNode = (MobileWayNode)wayNodes.get(index);
					lengthReverse += mobileWayNode.getDistanceToPredecessor();				
					if (--index == 0) index = wayNodes.size() - 1; 				
				}
			}
			
			length = Math.min(lengthForward, lengthReverse);
			
		} else {
			
			if (toNodeIndex < fromNodeIndex) {
				int temp = toNodeIndex;
				toNodeIndex = fromNodeIndex;
				fromNodeIndex = temp;
			}
			
			for (int i = fromNodeIndex + 1; i <= toNodeIndex; i++) {
				WayNode wayNode = wayNodes.get(i);
				if (wayNode instanceof MobileWayNode) {
					length += ((MobileWayNode)wayNode).getDistanceToPredecessor();
				} else {
					return -1;
				}
			}			
		}
		
		return length;
		
	}

}
