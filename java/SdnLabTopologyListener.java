package pl.edu.agh.kt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.google.common.collect.Maps;

import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.PortChangeType;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery.LDUpdate;
import net.floodlightcontroller.topology.ITopologyListener;

public class SdnLabTopologyListener implements ITopologyListener,
		IOFSwitchListener {
	protected static final Logger logger = LoggerFactory
			.getLogger(SdnLabTopologyListener.class);

	@Override
	public void topologyChanged(List<LDUpdate> linkUpdates) {
		for (ILinkDiscovery.LDUpdate update : linkUpdates) {
			switch (update.getOperation()) {
			case LINK_UPDATED:
				logger.debug("Link updated. Update {}", update.toString());
				addLink(update.getSrc(), update.getDst(), update.getSrcPort(),
						update.getDstPort());
				break;
			case LINK_REMOVED:
				logger.debug("Link removed. Update {}", update.toString());
				removeLink(update.getSrc(), update.getDst());
				break;
			case SWITCH_UPDATED:
				logger.debug("Switch updated. Update {}", update.toString());
				Flows.swList.add(update.getSrc());
				TopologyManager.addSwitch(update.getSrc());
				addSwitch(update.getSrc());
				// SdnLabListener.getRouting().calculateSpfTree(swList);
				break;
			case SWITCH_REMOVED:
				logger.debug("Switch removed. Update {}", update.toString());
				TopologyManager.removeSwitch(update.getSrc());
				removeSwitch(update.getSrc());
				break;
			default:
				break;
			}
		}
		//logger.info("Map = {}", Flows.adjacencyMap);
	}

	private void addLink(DatapathId src, DatapathId dst, OFPort srcPort,
			OFPort dstPort) {
		TopologyManager.addLink(src, dst, srcPort, dstPort);
		//
		if (!Flows.adjacencyMap.containsKey(src)) {
			Flows.adjacencyMap.put(src, new ArrayList<DatapathId>());
		}
		if (!Flows.adjacencyMap.containsKey(dst)) {
			Flows.adjacencyMap.put(dst, new ArrayList<DatapathId>());
		}
		if (!Flows.adjacencyMap.get(src).contains(dst)) {
			Flows.adjacencyMap.get(src).add(dst);
		}
		if (!Flows.adjacencyMap.get(dst).contains(src)) {
			Flows.adjacencyMap.get(dst).add(src);
		}
		//
		if (!Flows.portAdjacencyMap.containsKey(src)) {
			Flows.portAdjacencyMap.put(src, new HashMap<DatapathId, OFPort>());
		}
		if (!Flows.portAdjacencyMap.containsKey(dst)) {
			Flows.portAdjacencyMap.put(dst, new HashMap<DatapathId, OFPort>());
		}
		if (!Flows.portAdjacencyMap.get(src).containsKey(dst)) {
			Flows.portAdjacencyMap.get(src).put(dst, srcPort);
		}
		if (!Flows.portAdjacencyMap.get(dst).containsKey(src)) {
			Flows.portAdjacencyMap.get(dst).put(src, dstPort);
		}
		//logger.debug("Added link: {} -> {}", src, dst);
	}

	private void removeLink(DatapathId src, DatapathId dst) {
		List<DatapathId> srcNeighbors = Flows.adjacencyMap.get(src);
		if (srcNeighbors != null) {
			srcNeighbors.remove(dst);
			if (srcNeighbors.isEmpty()) {
				Flows.adjacencyMap.remove(src);
			}
		}

		List<DatapathId> dstNeighbors = Flows.adjacencyMap.get(dst);
		if (dstNeighbors != null) {
			dstNeighbors.remove(src);
			if (dstNeighbors.isEmpty()) {
				Flows.adjacencyMap.remove(dst);
			}
		}
		//logger.debug("Removed link: {} -> {}", src, dst);
		TopologyManager.removeLink(src, dst);
	}

	private void addSwitch(DatapathId switchId) {
		if (!Flows.adjacencyMap.containsKey(switchId)) {
			Flows.adjacencyMap.put(switchId, new ArrayList<DatapathId>());
			//logger.debug("Added switch: {}", switchId);
		}
	}

	private void removeSwitch(DatapathId switchId) {
		List<DatapathId> neighbors = Flows.adjacencyMap.remove(switchId);
		if (neighbors != null) {
			for (DatapathId neighbor : neighbors) {
				List<DatapathId> neighborList = Flows.adjacencyMap
						.get(neighbor);
				if (neighborList != null) {
					neighborList.remove(switchId);
					if (neighborList.isEmpty()) {
						Flows.adjacencyMap.remove(neighbor);
					}
				}
			}
		}
		logger.debug("Removed switch: {}", switchId);
	}

	@Override
	public void switchPortChanged(DatapathId switchId, OFPortDesc portDesc,
			PortChangeType type) {
		switch (type) {
		case UP:
			logger.debug("Port enabled. Switch: {}, Port: {}", switchId,
					portDesc.getPortNo());
			break;
		case DOWN:
			logger.debug("Port disabled. Switch: {}, Port: {}", switchId,
					portDesc.getPortNo());
			break;
		case OTHER_UPDATE:
			logger.debug("Port updated. Switch: {}, Port: {}", switchId,
					portDesc.getPortNo());
			break;
		default:
			logger.warn(
					"Unknown port status change detected. Switch: {}, Port: {}",
					switchId, portDesc.getPortNo());
			break;
		}
	}

	@Override
	public void switchAdded(DatapathId switchId) {
		logger.debug("Switch added: {}", switchId);
	}

	@Override
	public void switchRemoved(DatapathId switchId) {
		logger.debug("Switch removed: {}", switchId);
	}

	@Override
	public void switchActivated(DatapathId switchId) {
		logger.debug("Switch activated: {}", switchId);
	}

	@Override
	public void switchChanged(DatapathId switchId) {
		logger.debug("Switch changed: {}", switchId);
	}
	
	public void addLinkNew(DatapathId src, DatapathId dst, OFPort srcPort,
			OFPort dstPort){
		
	}
}
