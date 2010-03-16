package com.mobiletsm.osm.data.searching;

import java.util.Map;

import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import com.mobiletsm.osmosis.core.domain.v0_6.MobileNode;

public class POINodeSelector implements Selector {

	
	private Map<String, Integer> poiCodeMap = null;
	
	
	private POICode poiCode = null;
	
	
	/**
	 * creates a new POINodeSelector allowing only POI nodes of the given type
	 * @param id
	 */
	public POINodeSelector(int id) {
		this(new POICode(id));
	}
	
	
	/**
	 * creates a new POINodeSelector allowing only POI nodes of the given type
	 * @param type
	 */
	public POINodeSelector(String type) {
		this(new POICode(type));
	}
	
	
	/**
	 * creates a new POINodeSelector allowing every type of POI node
	 */
	public POINodeSelector() {
		super();
	}
	
	
	/**
	 * creates a new POINodeSelector allowing only POI nodes of the given type
	 * @param poiCode
	 */
	public POINodeSelector(POICode poiCode) {
		super();
		this.poiCode = poiCode;
	}
	
	
	public POICode getPOICode() {
		return poiCode;
	}
	
	
	@Override
	public boolean isAllowed(IDataSet map, Node node) {
		
		if (node instanceof MobileNode) {
			MobileNode mobileNode = (MobileNode)node;
			if (mobileNode.getPOICode() != null) {
				if (poiCode == null || poiCode.equals(mobileNode.getPOICode())) {
					return true;
				}
			}
		}
		
		/* iterate over all tags of given node */		
		for (Tag tag : node.getTags()) {
			String tagString = POICode.getTagString(tag);
			if (poiCode == null) {
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
				if (tagString.equals(poiCode.getType())) {
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
			return poiCode.equals(selector.getPOICode());
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
