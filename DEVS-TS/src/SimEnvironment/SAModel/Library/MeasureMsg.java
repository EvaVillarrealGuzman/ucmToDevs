
package SimEnvironment.SAModel.Library;

import GenCol.entity;

public class MeasureMsg extends entity {

	private Double value;
	private int responsibilityId;
	private int requestId;

	public MeasureMsg(String name) {
		this(name, 0, 0, 0.0);
	}

	public MeasureMsg(String name, int responsibilityId, int requestId, Double value) {
		super(name);
		this.setResponsibilityId(responsibilityId);
		this.setRequestId(requestId);
		this.setValue(value);
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public int getResponsibilityId() {
		return responsibilityId;
	}

	public void setResponsibilityId(int responsibilityId) {
		this.responsibilityId = responsibilityId;
	}

	public Boolean equal(entity entitymsg) {
		MeasureMsg measuremsg = (MeasureMsg) entitymsg;
		return ((measuremsg.getResponsibilityId() == this.getResponsibilityId())
				&& (measuremsg.getRequestId() == this.getRequestId()));
	}

	public void print() {
		System.out.println("Responsability: " + this.getResponsibilityId() + " -Request: " + this.getRequestId()
				+ " -Value: " + value);
	}

}
