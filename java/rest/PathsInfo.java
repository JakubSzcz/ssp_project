package pl.edu.agh.kt.rest;

import java.util.List;

import org.projectfloodlight.openflow.types.IPv4Address;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PathsInfo {

	@JsonProperty("paths_info")
	private List<PathInfo> pathsInfo;

	public PathsInfo(List<PathInfo> pathsInfo) {
		super();
		this.pathsInfo = pathsInfo;
	}

	public List<PathInfo> getPathsInfo() {
		return pathsInfo;
	}

	public void setPathsInfo(List<PathInfo> pathsInfo) {
		this.pathsInfo = pathsInfo;
	}
	
	public PathInfo getPaths(IPv4Address src, IPv4Address dst){
		for (PathInfo info : pathsInfo){
			if (IPv4Address.of(info.getSrcIp()).equals(src) && IPv4Address.of(info.getDstIp()).equals(dst)){
				return info;
			}
		}
		return null;
	}

	public PathsInfo() {
		super();
	}

	
	
}
