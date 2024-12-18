package pl.edu.agh.kt;

import java.util.ArrayList;
import java.util.List;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.protocol.OFPortDesc;

public class HostInfo {
	private List<SingleHostInfo> hostList;
	
	public HostInfo(){
		hostList = new ArrayList<>();
	}
	
	public HostInfo(List<SingleHostInfo> hostList){
		this.hostList = hostList;
	}
	
	public List<SingleHostInfo> getHostList(){
		return hostList;
	}
	
	public SingleHostInfo getHostInfo(IPv4Address ip){
		String ip_string = ip.toString();
		SingleHostInfo return_info = null;
		for(SingleHostInfo info : hostList){
			if (info.getIp().equals(ip_string)){
				return_info = info;
				break;
			}
		}
		return return_info;
	}

}
