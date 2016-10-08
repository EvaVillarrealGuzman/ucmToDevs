
package SimEnvironment.SAModel.Library;

import view.modeling.ViewableDigraph;

public class CPXResponsibility extends ViewableDigraph {

	public CPXResponsibility() {
		super("responsibility");
		ResponsibilityDEVS r = new ResponsibilityDEVS();
		FailureGenerator fg = new FailureGenerator();
		cxpResConstruct(r, fg);
	}

	public CPXResponsibility(Integer id, String name, Double refExecutionTime, Double refRecoveryTime,
			Double intMeanTimeBF, Double intDowntimeMean) {
		super(name);
		ResponsibilityDEVS rm = new ResponsibilityDEVS(id, "rm", refExecutionTime, refRecoveryTime);
		FailureGenerator fg = new FailureGenerator("fg", intMeanTimeBF, intDowntimeMean);
		cxpResConstruct(rm, fg);
	}

	public void cxpResConstruct(ResponsibilityDEVS rm, FailureGenerator fg) {
		add(rm);
		add(fg);
		initialize();
		addInport("prip");
		addOutport("srop");
		addOutport("taop");
		addOutport("dtop");
		addOutport("rtop");
		addOutport("failop");
		addCoupling(this, "prip", rm, "prip");
		addCoupling(rm, "stateop", fg, "rstateip");
		addCoupling(fg, "failop", rm, "intfailip");
		addCoupling(rm, "srop", this, "srop");
		addCoupling(rm, "taop", this, "taop");
		addCoupling(rm, "dtop", this, "dtop");
		addCoupling(rm, "rtop", this, "rtop");
		addCoupling(rm, "failop", this, "failop");
	}

}
