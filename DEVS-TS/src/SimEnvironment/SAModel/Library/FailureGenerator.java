
package SimEnvironment.SAModel.Library;

import model.modeling.message;
import util.rand;
import view.modeling.ViewableAtomic;

public class FailureGenerator extends ViewableAtomic {

	private Double refFailsTimeInterval;
	private Double refDowntime;
	private int count;
	private rand rndDowntimeGen;
	private rand rndIntTimeGen;

	public FailureGenerator() {
		this("FailureGenerator", 1000.0, 10.0);
	}

	public FailureGenerator(String name, Double intMeanTimeBF, Double intDowntimeMean) {
		super(name);
		addInport("rstateip");
		addOutport("failop");
		addTestInput("rstateip", new GenCol.entity("activated"));
		this.refFailsTimeInterval = intMeanTimeBF;
		this.refDowntime = intDowntimeMean;
		rndIntTimeGen = new rand(875);
		rndDowntimeGen = new rand(234);
		initialize();
	}

	public void initialize() {
		passivateIn("inactive");
		count = 0;
		super.initialize();
	}

	public void deltext(Double e, message x) {
		Continue(e);
		if (phaseIs("inactive")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "rstateip", i)) {
					GenCol.entity rtateEvent = x.getValOnPort("rstateip", i);
					holdIn("active", rndIntTimeGen.expon(this.refFailsTimeInterval));
				}
			}
		}
	}

	public void deltint() {
		if (phaseIs("active")) {
			count = count + 1;
			holdIn("active", rndIntTimeGen.expon(this.refFailsTimeInterval));
		}
	}

	public void deltcon(Double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message getRequestId() {
		message m = new message();
		Failure fail = new Failure("failure" + count, count, this.rndDowntimeGen.uniform(this.refDowntime));
		m.add(makeContent("failop", fail));
		return m;
	}

	public void showState() {
		super.showState();
	}

	//TODO: preguntar a vero por el error
	/*public String getTooltipText() {
		return super.getTooltipText() + "\n" + " Mean time between failures: " + this.refFailsTimeInterval + "\n"
				+ " Count of failures: " + count;
	}*/

}
