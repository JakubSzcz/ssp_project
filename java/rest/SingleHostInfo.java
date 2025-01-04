package pl.edu.agh.kt.rest;

public class SingleHostInfo{
	private String name;
	private int sw;
	private int port;
	private String ip;
	
	public SingleHostInfo(){
		name = "";
		sw = 0;
		port = 0;
		ip = "0.0.0.0";
	}
	
	public SingleHostInfo(String name, int sw, int port, String ip){
		this.name = name;
		this.sw = sw;
		this.port = port;
		this.ip = ip;
	}
	
	public String getName(){
		return name;
	}
	public int getSw(){
		return sw;
	}
	public int getPort(){
		return port;
	}
	public String getIp(){
		return ip;
	}
}
