package Transformer.Domain;

import java.util.ArrayList;

abstract public class Node {

	

	// Indica si el nodo es el primero en el camino
	private boolean isStart;
	// Indica si el nodo es el último en el camino
	private boolean isEnd;
	
	
	protected int id;
	protected String name;
	protected ArrayList<String> inputPorts;
	protected ArrayList<String> outputPorts;

	public Node(int id, String name) {
		this.id = id;
		this.name = name;
		isStart = false;
		isEnd = false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getInputPorts() {
		if (inputPorts == null) {
			inputPorts = new ArrayList<String>();
		}
		return inputPorts;
	}

	public void setInputPorts(ArrayList<String> inputPort) {
		this.inputPorts = inputPort;
	}

	public ArrayList<String> getOutputPorts() {
		if (outputPorts == null) {
			outputPorts = new ArrayList<String>();
		}
		return outputPorts;
	}

	public void setOutputPorts(ArrayList<String> outputPort) {
		this.outputPorts = outputPort;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
}
