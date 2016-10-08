
package SimEnvironment.SAModel.Library;

import GenCol.entity;

public class Request extends entity {

	private int requestId;

	public Request() {
		this("request", 0);
	}

	public Request(String name, int requestId) {
		super(name);
		this.setRequestId(requestId);
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public Boolean equal(entity entityToCmp) {
		Request requestEntity = (Request) entityToCmp;
		return this.getRequestId() == requestEntity.getRequestId();
	}

	public Boolean greaterThan(entity entityToCmp) {
		Request requestEntity = (Request) entityToCmp;
		return this.getRequestId() < requestEntity.getRequestId();
	}

	public void print() {
		System.out.println("Request: " + this.getName() + " id: " + this.getRequestId());
	}

}
