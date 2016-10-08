
package SimEnvironment.SAModel.Library;

import GenCol.Queue;
import model.modeling.message;
import util.rand;
import view.modeling.ViewableAtomic;

public class ResponsibilityDEVS extends ViewableAtomic {

	private int responsibilityId;
	private Double clock;
	private Queue requests;
	private Queue clockIns;
	private Double refExecutionTime;
	private Double refRecoveryTime;
	private rand rndExecutionTimeGen;
	private rand rndRecoveryTimeGen;
	private Double taLastR;
	private Request lastRequest;
	private Double clockLastFail;
	private int procRequestsCount;
	private int failcounter;

	public ResponsibilityDEVS() {
		this(1, "r1", 10.0, 0.0);
	}

	public ResponsibilityDEVS(int id, String name, Double refExecutionTime, Double refRecoveryTime) {
		super(name);
		this.setResponsibilityId(id);
		requests = new Queue();
		clockIns = new Queue();
		rndExecutionTimeGen = new rand(324);
		rndRecoveryTimeGen = new rand(2234);
		addInport("prip");
		addInport("intfailip");
		addOutport("srop");
		addOutport("stateop");
		addOutport("taop");
		addOutport("dtop");
		addOutport("rtop");
		addOutport("failop");
		this.setExecutionTime(refExecutionTime);
		this.setRefRecoveryTime(refRecoveryTime);
		taLastR = 0.0;
		procRequestsCount = 0;
		this.clockLastFail = 0.0;
		addTestInput("prip", new Request("request1", 1), 0);
		addTestInput("prip", new Request("request2", 2), 0);
		addTestInput("prip", new Request("request3", 3), 0);
		addTestInput("prip", new Request("request4", 4), 0);
		addTestInput("intfailip", new Failure("failure", 1, 10.00), 0);
	}

	public void initialize() {
		passivateIn("inactive");
		super.initialize();
	}

	public Double getExecutionTime() {
		return refExecutionTime;
	}

	public void setExecutionTime(Double refExecutionTime) {
		this.refExecutionTime = refExecutionTime;
	}

	public int getResponsibilityId() {
		return responsibilityId;
	}

	public void setResponsibilityId(int responsibilityId) {
		this.responsibilityId = responsibilityId;
	}

	public Double getRefExecutionTime() {
		return refExecutionTime;
	}

	public void setRefExecutionTime(Double refExecutionTime) {
		this.refExecutionTime = refExecutionTime;
	}

	public rand getRndExecutionTimeGen() {
		return rndExecutionTimeGen;
	}

	public void setRndExecutionTimeGen(rand rndExecutionTimeGen) {
		this.rndExecutionTimeGen = rndExecutionTimeGen;
	}

	public Request getLastRequest() {
		return lastRequest;
	}

	public void setLastRequest(Request lastRequest) {
		this.lastRequest = lastRequest;
	}

	public Double getRefRecoveryTime() {
		return refRecoveryTime;
	}

	public void setRefRecoveryTime(Double refRecoveryTime) {
		this.refRecoveryTime = refRecoveryTime;
	}

	public void deltext(Double e, message x) {
		clock = clock + e;
		Continue(e);
		if (phaseIs("inactive")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "prip", i)) {
					GenCol.entity arrRequest = x.getValOnPort("prip", i);
					requests.add(arrRequest);
					clockIns.add((Double) clock);
					holdIn("active", 0);
				}
			}
		} else if (phaseIs("executing")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "prip", i)) {
					GenCol.entity arrRequest = x.getValOnPort("prip", i);
					requests.add(arrRequest);
					clockIns.add((Double) clock);
				} else if (messageOnPort(x, "intfailip", i)) {
					Failure arrFail = (Failure) x.getValOnPort("intfailip", i);
					this.failcounter++;
					this.clockLastFail = clock;
					holdIn("failed", arrFail.getDowntime());
				}
			}
		}
	}

	public void deltint() {
		clock = clock + sigma;
		if (phaseIs("active")) {
			lastRequest = (Request) requests.remove();
			taLastR = ((Double) clockIns.remove()).doubleValue();
			holdIn("executing", this.rndExecutionTimeGen.uniform(this.getExecutionTime()));
		} else if (phaseIs("executing")) {
			if (!requests.isEmpty()) {
				lastRequest = (Request) requests.remove();
				taLastR = ((Double) clockIns.remove()).doubleValue();
				holdIn("executing", this.rndExecutionTimeGen.uniform(this.getExecutionTime()));
			} else {
				passivateIn("inactive");
			}
		} else if (phaseIs("failed")) {
			holdIn("recovering", this.rndRecoveryTimeGen.uniform(this.getRefRecoveryTime()));
		} else if (phaseIs("recovering")) {
			if (!requests.isEmpty()) {
				lastRequest = (Request) requests.remove();
				taLastR = ((Double) clockIns.remove()).doubleValue();
				holdIn("executing", this.rndExecutionTimeGen.uniform(this.getExecutionTime()));
			} else {
				passivateIn("inactive");
			}
		}
	}

	public void deltcon(Double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message getRequestId() {
		message m = new message();
		if (phaseIs("executing")) {
			taLastR = (clock + sigma) - taLastR;
			m.add(makeContent("stateop", new GenCol.entity("finished")));
			MeasureMsg taMsn = new MeasureMsg("ta=" + taLastR, this.getResponsibilityId(),
					this.lastRequest.getRequestId(), taLastR);
			m.add(makeContent("taop", taMsn));
			m.add(makeContent("srop", this.lastRequest));
			this.procRequestsCount++;
		} else if (phaseIs("active")) {
			m.add(makeContent("stateop", new GenCol.entity("activated")));
		} else if (phaseIs("failed")) {
			MeasureMsg dtMsn = new MeasureMsg("down=" + sigma, this.getResponsibilityId(),
					this.lastRequest.getRequestId(), sigma);
			m.add(makeContent("dtop", dtMsn));
			MeasureMsg failsMsn = new MeasureMsg("failures=" + this.failcounter, this.getResponsibilityId(),
					this.lastRequest.getRequestId(), this.clockLastFail);
			m.add(makeContent("failop", failsMsn));
		} else if (phaseIs("recovering")) {
			MeasureMsg rtMsn = new MeasureMsg("recovery=" + sigma, this.getResponsibilityId(),
					this.lastRequest.getRequestId(), sigma);
			m.add(makeContent("rtop", rtMsn));
		}
		return m;
	}

	public void showState() {
		super.showState();
		System.out.println("**Responsibility id: " + this.getResponsibilityId());
		System.out.println("    -Queue Length (requests): " + this.requests.size());
		System.out.println("    -Requests Proc.: " + this.procRequestsCount);
	}

}
