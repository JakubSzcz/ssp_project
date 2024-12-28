package pl.edu.agh.kt;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;

public class LinkPortsInfo {

	private DatapathId destNode;
	private OFPort srcPort;
	private OFPort destPort;

	public LinkPortsInfo(DatapathId destNode, OFPort srcPort, OFPort destPort) {
		super();
		this.destNode = destNode;
		this.srcPort = srcPort;
		this.destPort = destPort;
	}

	public DatapathId getDestNode() {
		return destNode;
	}

	public void setSrcPort(OFPort srcPort) {
		this.srcPort = srcPort;
	}

	public void setDestPort(OFPort destPort) {
		this.destPort = destPort;
	}

	public OFPort getSrcPort() {
		return srcPort;
	}

	public OFPort getDestPort() {
		return destPort;
	}

}
