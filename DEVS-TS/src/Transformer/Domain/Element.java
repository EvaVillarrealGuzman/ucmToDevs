package Transformer.Domain;

import java.util.ArrayList;

public class Element extends Node {

	private String type;
	private ArrayList<String> successor;
	private ArrayList<String> predecessor;
	private ArrayList<Double> pathProbabilities; // Este atributo solo será
													// distinto de null si el
													// elemento es un or fork

	public Element(int id, String name) {
		super(id, name);
	}

	public Element(int id, String name, String type, ArrayList<String> successor, ArrayList<String> predecessor,
			ArrayList<Double> pathProbabilities) {
		super(id, name);
		this.type = type;
		this.successor = successor;
		this.predecessor = predecessor;
		this.pathProbabilities = pathProbabilities;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<String> getSuccessor() {
		if (successor == null) {
			successor = new ArrayList<String>();
		}
		return successor;
	}

	public void setSuccessor(ArrayList<String> successor) {
		this.successor = successor;
	}

	public ArrayList<String> getPredecessor() {
		if (predecessor == null) {
			predecessor = new ArrayList<String>();
		}
		return predecessor;
	}

	public void setPredecessor(ArrayList<String> predecessor) {
		this.predecessor = predecessor;
	}

	public ArrayList<Double> getPathProbabilities() {
		if (pathProbabilities == null) {
			pathProbabilities = new ArrayList<Double>();
		}
		return pathProbabilities;
	}

	public void setPathProbabilities(ArrayList<Double> pathProbabilities) {
		this.pathProbabilities = pathProbabilities;
	}

}
