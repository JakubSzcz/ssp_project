package pl.edu.agh.kt.rest;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkPortsInfo {

	@JsonProperty("node")
	private String switchId;
	@JsonProperty("in_port")
	private int inPort;
	@JsonProperty("out_port")
	private int outPort;

	public LinkPortsInfo(String switchId, int inPort, int outPort) {
		super();
		this.switchId = switchId;
		this.inPort = inPort;
		this.outPort = outPort;
	}

	public String getSwitchId() {
		return switchId;
	}
	
	public DatapathId getSwitchDatapathId(){
		return DatapathId.of(this.switchId);
	}

	public void setSwitchId(String switchId) {
		this.switchId = switchId;
	}

	public int getInPort() {
		return inPort;
	}
	
	public OFPort getInOFPort(){
		return OFPort.of(this.inPort);
	}

	public void setInPort(int inPort) {
		this.inPort = inPort;
	}

	public int getOutPort() {
		return outPort;
	}
	
	public OFPort getOutOFPort(){
		return OFPort.of(this.outPort);
	}

	public void setOutPort(int outPort) {
		this.outPort = outPort;
	}

	public LinkPortsInfo() {
		super();
	}

	@Override
	public String toString() {
		return "LinkPortsInfo [switchId=" + switchId + ", inPort=" + inPort
				+ ", outPort=" + outPort + "]";
	}

}
