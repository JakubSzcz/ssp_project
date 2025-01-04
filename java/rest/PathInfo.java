package pl.edu.agh.kt.rest;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PathInfo {

	@JsonProperty("src")
	private String srcIp;
	@JsonProperty("dst")
	private String dstIp;
	@JsonProperty("paths")
	private List<List<LinkPortsInfo>> paths;

	public PathInfo(String srcIp, String dstIp, List<List<LinkPortsInfo>> paths) {
		super();
		this.srcIp = srcIp;
		this.dstIp = dstIp;
		this.paths = paths;
	}

	public String getSrcIp() {
		return srcIp;
	}

	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}

	public String getDstIp() {
		return dstIp;
	}

	public void setDstIp(String dstIp) {
		this.dstIp = dstIp;
	}

	public List<List<LinkPortsInfo>> getPaths() {
		return paths;
	}

	public PathInfo() {
		super();
	}

}
