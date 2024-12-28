package pl.edu.agh.kt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopologyManager {

	protected static final Logger logger = LoggerFactory
			.getLogger(SdnLabTopologyListener.class);

	private static List<DatapathId> swList = new ArrayList<>();
	private static Map<DatapathId, AdjacentNodeInfo> neigh = new HashMap<>();

	public static List<DatapathId> getSwList() {
		return swList;
	}

	public static Map<DatapathId, AdjacentNodeInfo> getNeigh() {
		return neigh;
	}

	public static void addSwitch(DatapathId sw) {
		if (!swList.contains(sw)) {
			swList.add(sw);
			neigh.put(sw, new AdjacentNodeInfo(sw));
			logger.info(
					"New switch {} added to topology manager without adjacency.",
					sw.toString());
		} else {
			logger.info("Switch {} already added.", sw.toString());
		}
	}

	public static void removeSwitch(DatapathId swToRemove) {
		if (swList.contains(swToRemove)) {
			neigh.remove(swToRemove);
			swList.remove(swToRemove);
			logger.info("Switch {} removed.", swToRemove.toString());

			// remove links where node used to be as destination
			for (DatapathId node : swList) {
				AdjacentNodeInfo adjecentNodes = neigh.get(node);
				if (adjecentNodes == null) {
					logger.info("No adjacency found from switch {} to {}.",
							swToRemove.toString(), node.toString());
					continue;
				}
				for (LinkPortsInfo link : adjecentNodes.getAdjecentList()) {
					if (link.getDestPort().equals(swToRemove)) {
						adjecentNodes.getAdjecentList().remove(link);
						logger.info("Adjacency from swtich {} to {} removed.",
								swToRemove.toString(), link.getDestPort()
										.toString());
						break;
					}
				}
			}
		} else {
			logger.info("No switch {} found to be removed.", swToRemove);
		}
	}

	public static void addLink(DatapathId srcNode, DatapathId destNode,
			OFPort srcPort, OFPort destPort) {

		boolean newSrcEntry = false, newDestEntry = false;

		// check if node already registered
		if (!swList.contains(srcNode)) {
			addSwitch(srcNode);
			newSrcEntry = true;
		}

		if (!swList.contains(destNode)) {
			addSwitch(destNode);
			newDestEntry = true;
		}

		// srcNode -> destLink -> destNode
		// srcNode <- srcLink <- destNode

		// links processing
		processAddingLink(newDestEntry, srcNode, destNode, srcPort, destPort);
		processAddingLink(newSrcEntry, destNode, srcNode, destPort, srcPort);

	}

	private static void processAddingLink(boolean isNewEntry,
			DatapathId srcNode, DatapathId destNode, OFPort srcPort,
			OFPort destPort) {

		LinkPortsInfo linkToAdd = new LinkPortsInfo(destNode, srcPort, destPort);

		// node didn't existed before adding link operation
		if (isNewEntry) {
			neigh.get(srcNode).addNewLink(linkToAdd);

		} else {
			// check if link already exist, if so, overwrite it ports
			boolean linkExisted = false;
			AdjacentNodeInfo adjacency = neigh.get(srcNode);
			for (LinkPortsInfo link : adjacency.getAdjecentList()) {
				if (link.getDestNode().equals(destNode)) {
					link.setSrcPort(srcPort);
					link.setDestPort(destPort);
					linkExisted = true;
				}
			}
			if (!linkExisted) {
				adjacency.addNewLink(linkToAdd);
			}
		}
		String msg = srcNode.toString() + "_port=" + srcPort.toString()
				+ "_--->_" + destPort.toString() + "=port_"
				+ destNode.toString();
		logger.info("New link {} added.", msg);
	}

	public static void removeLink(DatapathId srcNode, DatapathId destNode) {

		// checks if nodes already registered
		boolean srcNodeExist = swList.contains(srcNode);
		boolean destNodeExist = swList.contains(destNode);
		
		proccesRemovingLink(srcNodeExist, srcNode, destNode);
		proccesRemovingLink(destNodeExist, destNode, srcNode);

	}

	public static void proccesRemovingLink(boolean nodeExists,
			DatapathId srcNode, DatapathId destNode) {
		if (nodeExists) {
			AdjacentNodeInfo adjacent = neigh.get(srcNode);
			for (LinkPortsInfo link : adjacent.getAdjecentList()) {
				if (link.getDestNode().equals(destNode)) {
					adjacent.removeLink(link);
					String msg = "[" + srcNode.toString() + "<-->" + destNode.toString() + "]";
					logger.info("Link {} removed.", msg);
					break;
				}
			}
		}else {
			logger.info("No adjacency found for {}", srcNode.toString());
		}
	}

	public String printAdjacency() {
		StringBuilder toReturn = new StringBuilder();
		toReturn.append("Adjacency maping:\n");
		for (DatapathId node : neigh.keySet()) {
			toReturn.append("[" + neigh.get(node).toString() + "]\n");
		}
		return toReturn.toString();

	}

}
