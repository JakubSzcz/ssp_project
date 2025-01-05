package pl.edu.agh.kt;

import java.util.List;

import org.projectfloodlight.openflow.types.IPv4Address;

import pl.edu.agh.kt.rest.LinkPortsInfo;
import pl.edu.agh.kt.rest.PathInfo;

import net.floodlightcontroller.topology.NodePortTuple;

public class WRRLoadBalancer {
	public static List<LinkPortsInfo> getPath(IPv4Address src, IPv4Address dst){
		PathInfo paths = Flows.paths.getPaths(src, dst);
		if (paths == null){
			return null;
		}else{
			return paths.getPaths().get(0);
		}
	}
}
