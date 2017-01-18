package Transformer.Domain;

import java.util.ArrayList;

public class Path {

	private ArrayList<String> first;
	private ArrayList<String> port;
	private ArrayList<String> second;
	
	
	public Path() {
		super();
		first = new ArrayList<String>();
		port = new ArrayList<String>();
		second = new ArrayList<String>();
	}


	public ArrayList<String> getFirst() {
		return first;
	}


	public void setFirst(ArrayList<String> first) {
		this.first = first;
	}


	public ArrayList<String> getPort() {
		return port;
	}


	public void setPort(ArrayList<String> port) {
		this.port = port;
	}


	public ArrayList<String> getSecond() {
		return second;
	}


	public void setSecond(ArrayList<String> second) {
		this.second = second;
	}

}
