package com.mobiletsm.osm.data.searching;

import java.util.Map;

import org.openstreetmap.osm.Tags;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.NodeHelper;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.WayHelper;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

public class POINodeSelector implements Selector {

	
	private Map<String, Integer> poiCodeMap = null;
	
	
	private POICode poiNode = null;
	
	
	public POINodeSelector(String type) {
		this(new POICode(type));
	}
	
	
	public POINodeSelector() {
		super();
	}
	
	
	public POINodeSelector(POICode poiNode) {
		super();
		this.poiNode = poiNode;
	}
	
	
	public POICode getPOINode() {
		return poiNode;
	}
	
	
	@Override
	public boolean isAllowed(IDataSet map, Node node) {
		/* iterate over all tags of given node */
		for (Tag tag : node.getTags()) {
			String tagString = POICode.getTagString(tag);
			if (poiNode == null) {
				/* allow every POI node,
				 * load POI code map unless already done */
				if (poiCodeMap == null) {
					poiCodeMap = POICode.getPOICodeMap();
				}
				if (poiCodeMap.containsKey(tagString)) {
					return true;
				}
			} else {
				/* only allow POI nodes of specified type */
				if (tagString.equals(poiNode.getType())) {
					return true;
				}
			}
		}
		return false;
	}

	
	@Override
	public boolean equals(Object object) {
		boolean isPOINodeSelector = (object instanceof POINodeSelector);
		if (isPOINodeSelector) {
			POINodeSelector selector = (POINodeSelector)object;
			return poiNode.equals(selector.getPOINode());
		} else {
			return false;
		}
	}


	@Override
	public boolean isAllowed(IDataSet arg0, Way arg1) {
		return false;
	}


	@Override
	public boolean isAllowed(IDataSet arg0, Relation arg1) {
		return false;
	}

}
