package pl.edu.agh.kt;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.topology.NodePortTuple;

import java.util.ArrayList;

import org.sdnplatform.sync.internal.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdnLabListener implements IFloodlightModule, IOFMessageListener {

	protected IFloodlightProviderService floodlightProvider;
	protected static Logger logger;
	protected ITopologyService topologyService;
	protected IOFSwitchService switchService;
	protected IRoutingService routingService;
	protected static Routing routing;
	protected IRestApiService restApiService;

	@Override
	public String getName() {
		return SdnLabListener.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {

		logger.info("************* NEW PACKET IN *************");

		OFPacketIn pin = (OFPacketIn) msg;

		Ethernet eth = new Ethernet();
		eth.deserialize(pin.getData(), 0, pin.getData().length);

		IPv4Address dstIp;
		logger.info("Type = {}", eth.getPayload().getClass().getSimpleName());
		if (eth.getPayload() instanceof IPv4) {
			IPv4 ipv4 = (IPv4) eth.getPayload();
			dstIp = ipv4.getDestinationAddress();
		} else if (eth.getPayload() instanceof ARP) {
			ARP arp = (ARP) eth.getPayload();
			dstIp = arp.getTargetProtocolAddress();
		} else {
			logger.debug("Protocol not supported");
			return Command.STOP;
		}
		logger.info("SRC sw = {}", sw);
		logger.info("IP = {}", dstIp);
		SingleHostInfo info = Flows.hosts.getHostInfo(dstIp);
		if (info == null) {
			logger.debug("Host not found");
			return Command.STOP;
		}
		int dstSwId = info.getSw();
		logger.info("host sw = {}", dstSwId);
		DatapathId srcSw = sw.getId();
		DatapathId dstSw = DatapathId.of(dstSwId);
		if (dstSw.equals(srcSw)) {
			Flows.simpleAdd(sw, pin, cntx, pin.getInPort(), pin.getInPort(),
					true);
		} else {
			Route route = SdnLabListener.getRouting().calculateSpfTree(srcSw,
					dstSw);
			List<Pair<List<DatapathId>, Integer>> paths = SdnLabListener
					.getRouting().getKPaths(srcSw, dstSw, 3);

			logger.info("route {}", route.getPath());
			Map<OFPort, Integer> outPorts = new HashMap<>();
			for (Pair<List<DatapathId>, Integer> p : paths) {
				List<NodePortTuple> path = new ArrayList<>();
				for (int i = 0; i < p.getKey().size() - 1; i++) {
					path.add(new NodePortTuple(p.getKey().get(i),
							Flows.portAdjacencyMap.get(p.getKey().get(i)).get(
									p.getKey().get(i + 1))));
					path.add(new NodePortTuple(p.getKey().get(i + 1),
							Flows.portAdjacencyMap.get(p.getKey().get(i + 1))
									.get(p.getKey().get(i))));
				}
				logger.info("path = {}", path);

				DatapathId previousId = dstSw;
				OFPort previousPort = OFPort.of(info.getPort());

				for (int i = path.size() - 1; i >= 0; i--) {
					NodePortTuple npt = path.get(i);
					if (npt.getNodeId().equals(previousId)) {
						IOFSwitch s = switchService.getSwitch(npt.getNodeId());
						logger.info("Test ID {}", npt.getNodeId());
						logger.info("Test PORT IN {}", npt.getPortId());
						logger.info("Test PORT OUT {}", previousPort);
						Flows.simpleAdd(s, pin, cntx, npt.getPortId(),
								previousPort, false);

					}
					previousId = npt.getNodeId();
					previousPort = npt.getPortId();
				}
				logger.info("Test ID {}", srcSw);
				logger.info("Test PORT IN {}", pin.getInPort());
				logger.info("Test PORT OUT {}", previousPort);
				outPorts.put(previousPort, p.getSecond());
			}
			Flows.bucketAdd(sw, pin, cntx, pin.getInPort(), outPorts, true);

			// List<NodePortTuple> path = route.getPath();
		}

		return Command.STOP;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRoutingService.class);
		l.add(IRestApiService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider = context
				.getServiceImpl(IFloodlightProviderService.class);
		logger = LoggerFactory.getLogger(SdnLabListener.class);

		// Rest API
		restApiService = context.getServiceImpl(IRestApiService.class);

		// Topology listener
		topologyService = context.getServiceImpl(ITopologyService.class);
		switchService = context.getServiceImpl(IOFSwitchService.class);
		routingService = context.getServiceImpl(IRoutingService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);

		restApiService.addRestletRoutable(new RestLab());

		switchService.addOFSwitchListener(new SdnLabTopologyListener());
		topologyService.addListener(new SdnLabTopologyListener());
		routing = new Routing(routingService);
		logger.info("******************* START **************************");

	}

	public static Routing getRouting() {
		return routing;
	}

}
