import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.data.coordinates.Bounds;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.travelingsalesman.routing.IVehicle;

/**
 * 
 */

/**
 * @author andreaswalz
 *
 */
public class OsmHelper {
	
	public static boolean compareRouting(MemoryDataSet map1, MemoryDataSet map2) {
		return false;
	}
	

	/**
	 * reduce the graph (nodes and ways) in map to the minimum
	 * graph needed for routing
	 * @param map
	 */
	public static MemoryDataSet compressForRouting(MemoryDataSet map) {
		System.out.println("compressForRouting: input: # nodes = " + map.getNodesCount());
		System.out.println("compressForRouting: input: # ways = " + map.getWaysCount());
		
		Set<Long> unusedNodes = new HashSet<Long>();		
		MemoryDataSet routingMap = new MemoryDataSet();
		
		/* the selector used to filter ways that can't be traversed */
		IVehicle selector = new Car();
				
		/* find nodes that are irrelevant for routing by iterating over 
		 * all nodes and add the ones needed for routing to the routing map */
		Iterator<Node> nodes = map.getNodes(null);
		while(nodes.hasNext()) {
			Node node = nodes.next();
			int nNodeWays = 0;
			Way way = null;
			boolean unused = false;
			
			/* determine the number of ways connected to this node */ 
			Iterator<Way> nodeWays = map.getWaysForNode(node.getId());			
			while(nodeWays.hasNext()) {
				Way nextWay = nodeWays.next();
				/* consider only ways allowed by the selector */
				if (selector.isAllowed(map, nextWay)) {
					nNodeWays++;
					way = nextWay;
				}
			}
			
			if (nNodeWays == 0) {
				/* no way is connected to this node,
				 * this node will be useless for routing */
				unusedNodes.add(node.getId());
				unused = true;
			} else if (nNodeWays == 1) {
				/* exactly one way is connected to this node,
				 * check if this node is at the beginning or the end of this way */				
				int index = getNodeIndex(node, way);
				if (index >= 1 && index <= way.getWayNodes().size() - 2) {
					/* node is neither beginning nor end of the way
					 * and thus useless for routing */
					unusedNodes.add(node.getId());
					unused = true;
				}
			}
			
			/* add this node to the routing map if and only if it
			 * is not useless for routing */
			if (!unused) {
				routingMap.addNode(node);
			}
		}
			
		/*  */
		Iterator<Way> ways = map.getWays(Bounds.WORLD);
		while (ways.hasNext()) {
			Way way = ways.next();
			if (selector.isAllowed(map, way)) {				
				/* this way is allowed by the selector and thus
				 * needed for routing */
				Way newWay = new Way(way.getId(), way.getVersion(), way
						.getTimestamp(), way.getUser(), way.getChangesetId());
				newWay.getTags().addAll(way.getTags());
				for (WayNode wayNode : way.getWayNodes()) {
					if (!unusedNodes.contains(wayNode.getNodeId())) {
						newWay.getWayNodes().add(
								new WayNode(wayNode.getNodeId()));
					}
				}
				routingMap.addWay(newWay);
			}
		}
		
		System.out.println("compressForRouting: output: # nodes = " + routingMap.getNodesCount() + 
				String.format(Locale.US, " (ratio = %.2f percent)", 
						(double)routingMap.getNodesCount()*100 / map.getNodesCount()));
		System.out.println("compressForRouting: output: # ways = " + routingMap.getWaysCount() +
				String.format(Locale.US, " (ratio = %.2f percent)", 
						(double)routingMap.getWaysCount()*100 / map.getWaysCount()));
		
		return routingMap;
	}
	
	
	public static int getNodeIndex(Node node, Way way) {
		int index = 0;
		Collection<WayNode> wayNodes = way.getWayNodes();
        for (WayNode wayNode : wayNodes) {
            if (wayNode.getNodeId() == node.getId())
                break;
            index++;
        }
        return index;
	}
	
	
	public static String packTagsToString(Collection<Tag> tags) {
		StringBuffer buf = new StringBuffer();		
		Iterator<Tag> tag_itr = tags.iterator();
		while(tag_itr.hasNext()) {
			Tag tag = tag_itr.next();
			buf.append("|" + tag.getKey() + "==" + tag.getValue());
		}		
		return buf.toString();
	}

	
	/**
	 * unpack a string containing the entity tags to a collection of tags
	 * @param tags
	 * @return
	 */
	public static Collection<Tag> unpackStringToTags(String tags) {
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
	
	
	public static String packLongsToString(Collection<Long> longs) {
		StringBuffer buf = new StringBuffer();
		Iterator<Long> long_itr = longs.iterator();
		while (long_itr.hasNext()) {
			String longString = Long.toHexString(long_itr.next().longValue());
			for (int i = 8; i > longString.length(); i--)
				buf.append("0");
			buf.append(longString);
		}
		return buf.toString();
	}
	
	
	public static Collection<Long> getWayNodes(Way way) {
		Collection<Long> longs = new Vector<Long>();
		Iterator<WayNode> waynode_itr = way.getWayNodes().iterator();		
		while(waynode_itr.hasNext()) {
			longs.add(waynode_itr.next().getNodeId());
		}
		return longs;
	}
	
}
