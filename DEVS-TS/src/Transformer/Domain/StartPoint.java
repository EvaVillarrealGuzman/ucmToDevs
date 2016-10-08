package Transformer.Domain;

public class StartPoint extends Node {

	double meanTimeBRequest;

	public StartPoint(int id, String name) {
		super(id, name);
	}

	public StartPoint(int id, String name, double meanTimeBRequest) {
		super(id, name);
		this.meanTimeBRequest = meanTimeBRequest;
	}

	public double getMeanTimeBRequest() {
		return meanTimeBRequest;
	}

	public void setMeanTimeBRequest(double meanTimeBRequest) {
		this.meanTimeBRequest = meanTimeBRequest;
	}

}
