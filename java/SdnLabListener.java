package pl.edu.agh.kt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;

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
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.topology.NodePortTuple;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.kt.rest.LinkPortsInfo;
import pl.edu.agh.kt.rest.RestLab;
import pl.edu.agh.kt.rest.SingleHostInfo;

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

		//logger.info("************* NEW PACKET IN *************");
		// TODO LAB 6
		OFPacketIn pin = (OFPacketIn) msg;

		Ethernet eth = new Ethernet();
		eth.deserialize(pin.getData(), 0, pin.getData().length);

		IPv4Address dstIp;
		IPv4Address srcIp;
		TransportPort srcPort = null;
		TransportPort dstPort = null;
		//logger.info("Type = {}", eth.getPayload().getClass().getSimpleName());
		if (eth.getPayload() instanceof IPv4) {
			IPv4 ipv4 = (IPv4) eth.getPayload();
			dstIp = ipv4.getDestinationAddress();
			srcIp = ipv4.getSourceAddress();
			if (ipv4.getPayload() instanceof TCP){
				TCP tcp = (TCP) ipv4.getPayload();
				srcPort = tcp.getSourcePort();
				dstPort = tcp.getDestinationPort();;
			}
		} else if (eth.getPayload() instanceof ARP) {
			ARP arp = (ARP) eth.getPayload();
			dstIp = arp.getTargetProtocolAddress();
			srcIp = arp.getSenderProtocolAddress();
		} else {
			//logger.debug("Protocol not supported");
			return Command.STOP;
		}
		//logger.info("SRC sw = {}", sw);
		//logger.info("src IP = {}", srcIp);
		//logger.info("dst IP = {}", dstIp);
		List<LinkPortsInfo> path;
		if (eth.getPayload() instanceof ARP){
			path = WRRLoadBalancer.getBestPath(srcIp, dstIp);
		}else{
			path = WRRLoadBalancer.getPath(srcIp, dstIp);
		}
		
		if (path == null) {
			//logger.debug("Path not found");
			return Command.STOP;
		}
		String pathString = "";
		for (LinkPortsInfo node : path){
			pathString += node.getSwitchId() + ", ";
		}
		
		//logger.info("path = {}", pathString);
		for (int i = path.size() - 1; i >= 0; i--) {
			LinkPortsInfo node = path.get(i);
			IOFSwitch s = switchService.getSwitch(node.getSwitchDatapathId());
			Flows.simpleAdd(s, pin, cntx, node.getInOFPort(), node.getOutOFPort(), i == 0);
		}
		if (!(eth.getPayload() instanceof ARP)){
			logger.info("************* NEW PACKET IN *************");
			logger.info("Type = {}", eth.getPayload().getClass().getSimpleName());
			logger.info("SRC sw = {}", sw);
			logger.info("src IP = {}", srcIp);
			logger.info("dst IP = {}", dstIp);
			logger.info("src Port = {}", srcPort);
			logger.info("dst Port = {}", dstPort);
			logger.info("path = {}", pathString);
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
