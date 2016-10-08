package Transformer.Domain;

import java.util.ArrayList;

public class Responsibility extends Node {

	private double meanExecuteTime;
	private double meanRecoveryTime;
	private double meanDownTime;
	private double meanTimeBFail;
	private ArrayList<String> successor;
	private ArrayList<String> predecessor;

	public Responsibility(int id, String name) {
		super(id, name);
	}

	public Responsibility(int id, String name, double meanExecuteTime, double meanRecoveryTime, double meanDownTime,
			double meanTimeBFail, ArrayList<String> successor, ArrayList<String> predecessor) {
		super(id, name);
		this.meanExecuteTime = meanExecuteTime;
		this.meanRecoveryTime = meanRecoveryTime;
		this.meanDownTime = meanDownTime;
		this.meanTimeBFail = meanTimeBFail;
		this.successor = successor;
		this.predecessor = predecessor;
	}

	public double getMeanExecuteTime() {
		return meanExecuteTime;
	}

	public void setMeanExecuteTime(double meanExecuteTime) {
		this.meanExecuteTime = meanExecuteTime;
	}

	public double getMeanRecoveryTime() {
		return meanRecoveryTime;
	}

	public void setMeanRecoveryTime(double meanRecoveryTime) {
		this.meanRecoveryTime = meanRecoveryTime;
	}

	public double getMeanDownTime() {
		return meanDownTime;
	}

	public void setMeanDownTime(double meanDownTime) {
		this.meanDownTime = meanDownTime;
	}

	public double getMeanTimeBFail() {
		return meanTimeBFail;
	}

	public void setMeanTimeBFail(double meanTimeBFail) {
		this.meanTimeBFail = meanTimeBFail;
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

}
