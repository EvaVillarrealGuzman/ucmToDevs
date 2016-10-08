/* 
 *  SAESE Library     
 *  
 *  Email: vbogado@santafe-conicet.gov.ar
 *  Date : 2012
 *  Extended from DEVSSuite Libraries, which can 
 *  be found at http://acims.asu.edu/software/devs-suite
 *  
 */

package SimEnvironment.EFLibrary;

import java.util.*;

import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

import SimEnvironment.SAModel.Library.MeasureMsg;

public class SAReliabilityStat extends ViewableAtomic {

	private Map<Integer, Integer> respTotalFails; // total failures per
													// responsibility
	private List<MeasureMsg> respFailsMsg; // turnaround time per responsibility
											// and request (all arrived msg with
											// time around)

	public SAReliabilityStat(String name) {
		super(name);
		// adding input ports
		addInport("rfailip");
		addInport("ssip");

		// Adding output ports
		addOutport("sfailsop");

		// number of failures happened in each responsibility
		respTotalFails = new HashMap<Integer, Integer>();

		// Responsibility Failure messages, record
		respFailsMsg = new ArrayList<MeasureMsg>();

		// testing the input ports

		// failures from responsibilities (responsibilityid, requestid, fail
		// time -clock)
		addTestInput("rfailip", new MeasureMsg("Falla 1", 1, 1, 10.0));
		addTestInput("rfailip", new MeasureMsg("Falla 2", 1, 2, 60.5));
		addTestInput("rfailip", new MeasureMsg("Falla 3", 2, 2, 30.0));
		addTestInput("rfailip", new MeasureMsg("Falla 4", 2, 3, 100.0));
		addTestInput("rfailip", new MeasureMsg("Falla 5", 2, 6, 110.3));

		// control variables from acceptor (control simulation)
		addTestInput("ssip", new entity("start"));
		addTestInput("ssip", new entity("stop"));

		// initializing the state and variables
		initialize();
	}

	public SAReliabilityStat() {
		this("SAEReliabilityStats");
	}

	public void initialize() {
		passivateIn("inactive");
		super.initialize();
	}

	public void showState() {
		super.showState();
		System.out.println("System Failures (total): " + this.computeSystemFailures());

	}

	// external transition function
	public void deltext(double e, message x) {
		// advancing internal time

		// continuing the time in the same state (active)
		Continue(e);

		if (phaseIs("calculating")) {
			for (int i = 0; i < x.size(); i++) {
				if (messageOnPort(x, "rfailip", i)) {
					// getting the entity in the message
					// value received from the ports
					MeasureMsg failmsg = (MeasureMsg) x.getValOnPort("rfailip", i);

					// adding message to the list
					respFailsMsg.add(failmsg);

					int failsAux = 0;
					if (respTotalFails.containsKey((Integer) failmsg.getResponsibilityId())) {
						failsAux = respTotalFails.get((Integer) failmsg.getResponsibilityId()).intValue();
					}
					failsAux++;
					// adding the element or updating an existing element
					respTotalFails.put((Integer) failmsg.getResponsibilityId(), (Integer) failsAux);

					holdIn("calculating", INFINITY); // sigma= infinity, passive
														// state
				} // if

				// if message arrives to ssip (port)
				if (messageOnPort(x, "ssip", i)) {
					entity ssipEvent = x.getValOnPort("ssip", i);
					if (ssipEvent.getName().equals("stop")) { // value is stop
																// (x)
						holdIn("resultant", 0); // sigma= infinity, passive
												// state
					}
				} // if

			} // for
		} else if (phaseIs("inactive")) {
			for (int i = 0; i < x.size(); i++) {
				// if message arrives to ssip (port)
				if (messageOnPort(x, "ssip", i)) {
					entity ssipEvent = x.getValOnPort("ssip", i);
					if (ssipEvent.getName().equals("start")) { // value is start
																// (x)
						holdIn("calculating", INFINITY); // sigma= infinity,
															// passive state
					}
				}
			} // for
		}

		// VER
		showState();
	}

	// Internal transition function
	public void deltint() {
		// changing the state to passive
		if (phaseIs("resultant")) { // resultant-->inactive
			passivateIn("inactive");
		}
	}

	// Confluent Function
	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message out() {
		message m = new message();

		if (phaseIs("resultant")) {
			// Send a massage with the total fails happened during one execution
			// of the system
			m.add(makeContent("sfailsop", new entity("Sys Failures: " + computeSystemFailures())));

			// print on console the metrics for the system
			System.out.println("System Failures: " + this.computeSystemFailures());
			// Print the total failures per responsibility
			this.printResponsibilityFails();
		}

		return m;
	}

	/**
	 * computeSystemFailures Function that adds all total responsibilities
	 * failures to obtain the total failures happened during the execution of
	 * the system.
	 * 
	 * @return int
	 */
	public int computeSystemFailures() {
		int totalFails = 0;

		if (!respTotalFails.isEmpty()) {
			Iterator iterator = respTotalFails.keySet().iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				Integer respFails = respTotalFails.get(key);

				// calculating the total fails of the system
				totalFails += respFails.intValue();
			}
		}
		return totalFails;
	}

	/**
	 * printResponsibilityFails() Function that print on the console the total
	 * fails per responsibility
	 * 
	 * @return void
	 */
	public void printResponsibilityFails() {

		System.out.println("***Fails per responsibility (id, number of failures):  ");
		if (!respTotalFails.isEmpty()) {
			Iterator iterator = respTotalFails.keySet().iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				Integer respFails = respTotalFails.get(key);

				System.out.println(key + "  " + respFails);
			}
		}

	}
}
