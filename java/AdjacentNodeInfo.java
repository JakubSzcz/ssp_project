package pl.edu.agh.kt;

import java.util.ArrayList;
import java.util.List;

import org.projectfloodlight.openflow.types.DatapathId;

public class AdjacentNodeInfo {

	//in Node/ origin node/ left Node
	private DatapathId inNode;
	
	private List<LinkPortsInfo> adjecentList;

	public AdjacentNodeInfo(DatapathId inNode,
			List<LinkPortsInfo> adjecentList) {
		super();
		this.inNode = inNode;
		this.adjecentList = adjecentList;
	}
	
	public AdjacentNodeInfo(DatapathId inNode) {
		this.inNode = inNode;
		this.adjecentList = new ArrayList<>();
	}

	public DatapathId getInNode() {
		return inNode;
	}

	public List<LinkPortsInfo> getAdjecentList() {
		return adjecentList;
	}
	
	public void addNewLink(LinkPortsInfo link){
		adjecentList.add(link);
	}
	
	public void removeLink(LinkPortsInfo link){
		adjecentList.remove(link);
	}

	@Override
	public String toString() {
		return "For node = " + inNode + ", adjecentList="
				+ adjecentList + ".";
	}
	
	

}
