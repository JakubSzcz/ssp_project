package pl.edu.agh.kt.rest;

import java.util.List;

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

	public PathsInfo() {
		super();
	}

	
	
}
