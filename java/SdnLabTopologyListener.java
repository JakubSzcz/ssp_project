package pl.edu.agh.kt;

import java.util.ArrayList;
import java.util.List;

import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.types.DatapathId;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
				break;
			case LINK_REMOVED:
				logger.debug("Link removed. Update {}", update.toString());
				break;
			case SWITCH_UPDATED:
				logger.debug("Switch updated. Update {}", update.toString());
				Flows.swList.add(update.getSrc());
				//SdnLabListener.getRouting().calculateSpfTree(swList);
				break;
			case SWITCH_REMOVED:
				logger.debug("Switch removed. Update {}", update.toString());
				break;
			default:
				break;
			}
		}
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
}
