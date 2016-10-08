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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import model.modeling.atomic;

import GenCol.entity;
import SimEnvironment.SAModel.Library.Interval;
import SimEnvironment.SAModel.Library.MeasureMsg;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class SAAvailabilityStat extends ViewableAtomic {
	private Map<Integer, Double> respDowntime, respRecovTime; // turnaround time
																// for
																// responsibility
																// and request
																// count
	private Map<Integer, Integer> respfails; // responsibility failures
	private Map<List, Double> downtimes, recoverytimes, failClocks; // all time
																	// messages
																	// sent by
																	// each
																	// responsibility
																	// each time
																	// a failure
																	// occurs
	private double clock;

	public SAAvailabilityStat(String name) {
		super(name);
		// adding input ports
		addInport("rdowntimeip"); // responsibility downtime per each failure
									// happened when a request has started to be
									// processed (to calculate mean donwtime for
									// each responsibility)
		addInport("rrecovtimeip"); // responsibility recovery time per each
									// failure happened when a request has
									// started to be processed (to calculate
									// mean recovery time for each
									// responsibility)
		addInport("rfailip"); // responsibility failure (time fail)

		addInport("ssip"); // start/stop input port

		// Adding output ports
		addOutport("savailop");
		addOutport("sunavailop");

		// lists of messages with time values for each responsibility and
		// request (this request is not processed correctly)that was being
		// processed when a failure happened
		respDowntime = new HashMap<Integer, Double>();
		respRecovTime = new HashMap<Integer, Double>();
		respfails = new HashMap<Integer, Integer>();

		downtimes = new HashMap<List, Double>();
		recoverytimes = new HashMap<List, Double>();
		failClocks = new HashMap<List, Double>();

		// testing the input ports
		addTestInput("rfailip", new MeasureMsg("Falla 1", 1, 1, 20.0));
		addTestInput("rfailip", new MeasureMsg("Falla 2", 1, 2, 40.0));
		addTestInput("rfailip", new MeasureMsg("Falla 3", 2, 1, 50.0));
		addTestInput("rfailip", new MeasureMsg("Falla 4", 3, 1, 60.0));

		addTestInput("rdowntimeip", new MeasureMsg("down 1", 1, 1, 10.0));
		addTestInput("rdowntimeip", new MeasureMsg("down 2", 1, 2, 20.0));
		addTestInput("rdowntimeip", new MeasureMsg("down 3", 2, 1, 50.0));
		addTestInput("rdowntimeip", new MeasureMsg("down 4", 3, 1, 20.0));

		addTestInput("rrecovtimeip", new MeasureMsg("recovery 1", 1, 1, 0.0));
		addTestInput("rrecovtimeip", new MeasureMsg("recovery 2", 1, 2, 0.0));
		addTestInput("rrecovtimeip", new MeasureMsg("recovery 3", 2, 1, 0.0));
		addTestInput("rrecovtimeip", new MeasureMsg("recovery 4", 3, 1, 0.0));

		addTestInput("ssip", new entity("start"));
		addTestInput("ssip", new entity("stop"));

		// initializing the state and variables
		initialize();
	}

	public SAAvailabilityStat() {
		this("SAEAvailabilityStats");
	}

	public void initialize() {
		phase = "inactive";
		sigma = INFINITY;
		clock = 0;
		super.initialize();
	}

	public void showState() {
		super.showState();
	}

	// external transition function
	public void deltext(double e, message x) {
		// advancing internal time
		clock = clock + e;

		// continuing the time in the same state (active)
		Continue(e);

		for (int i = 0; i < x.size(); i++) {
			if (phaseIs("calculating")) {
				if (messageOnPort(x, "rdowntimeip", i)) {

					// getting the entity in the message
					// value received from the ports
					// the entity in the message is the type of MeasureMsg
					// (index)
					MeasureMsg val = (MeasureMsg) x.getValOnPort("rdowntimeip", i);
					List ids = new ArrayList();
					ids.add(val.getResponsibilityId());
					ids.add(val.getRequestId());
					// for calculating No availability time of the system
					downtimes.put(ids, (Double) val.getValue());

					// adding message to the list: downtimes measured in the
					// corresponding resposibility
					double dtAux = 0;
					int failCounter = 0;
					if (respDowntime.containsKey((Integer) val.getResponsibilityId())) {
						dtAux = respDowntime.get((Integer) val.getResponsibilityId()).doubleValue();
						failCounter = respfails.get((Integer) val.getResponsibilityId()).intValue();
					}

					dtAux += val.getValue();
					failCounter++;

					// adding the element or updating an existing element
					respDowntime.put((Integer) val.getResponsibilityId(), (Double) dtAux);

					// System.out.println("carga AL DE LAS RESPONSBILIDAD");

					// adding the new value for the counter
					respfails.put((Integer) val.getResponsibilityId(), (Integer) failCounter);
				} // if

				if (messageOnPort(x, "rrecovtimeip", i)) {// event on port sent
															// request
					// getting the entity in the message
					// value received from the sent request input port
					MeasureMsg val = (MeasureMsg) x.getValOnPort("rrecovtimeip", i);
					List ids = new ArrayList();
					ids.add(val.getResponsibilityId());
					ids.add(val.getRequestId());
					// for calculating No availability time of the system
					recoverytimes.put(ids, (Double) val.getValue());

					double rtAux = 0;
					if (respRecovTime.containsKey((Integer) val.getResponsibilityId())) {
						rtAux = respRecovTime.get((Integer) val.getResponsibilityId()).doubleValue();
					}
					rtAux += val.getValue();

					// adding the element or updating an existing element
					respRecovTime.put((Integer) val.getResponsibilityId(), (Double) rtAux);
				}

				if (messageOnPort(x, "rfailip", i)) {
					MeasureMsg val = (MeasureMsg) x.getValOnPort("rfailip", i);
					List ids = new ArrayList();
					ids.add(val.getResponsibilityId());
					ids.add(val.getRequestId());

					failClocks.put(ids, (Double) val.getValue());
				}

				if (messageOnPort(x, "ssip", i)) { // event on port ssip,
													// control variable
					entity ssipEvent = x.getValOnPort("ssip", i);
					if (ssipEvent.getName().equals("stop")) {
						holdIn("resultant", 0); // sigma= infinity, passive
												// state
					}
				}
			} else if (phaseIs("inactive")) {
				if (messageOnPort(x, "ssip", i)) {
					entity ssipEvent = x.getValOnPort("ssip", i);
					if (ssipEvent.getName().equals("start")) {
						passivateIn("calculating"); // sigma= infinity, passive
													// state
					}
				}

			} // if

		} // for

	}

	// Internal transition function
	public void deltint() {
		// updating the internal clock (clock of this model)
		clock = clock + sigma;

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
			m.add(makeContent("savailop", new entity("Avail: " + computeSystemAvailability())));
			m.add(makeContent("sunavailop", new entity("Unavail: " + computeSystemNoAvailability())));

			// print on console the metrics for the system
			System.out.println("System Avail. (uptime): " + this.computeSystemAvailability());
			System.out.println("System Unavail. (downtime): " + this.computeSystemNoAvailability());

			// print other information per responsibility
			this.printResponsibilityDowntimes();
			this.printResponsibilityDowntimeIntervals();
		}
		return m;
	}

	// tiempo de disponibilidad promedio del sistema
	public double computeSystemAvailability() {
		return (this.clock - this.computeSystemNoAvailability());
	}

	// tiempo de no disponibilidad promedio del sistema (downtime+recovery time)
	public double computeSystemNoAvailability() {
		double sysNoAvaiTime = 0;
		List<Interval> timeIntervals = new ArrayList<Interval>();
		List<Interval> biggerTimeIntervals = new ArrayList<Interval>();

		if (!failClocks.isEmpty()) {
			Iterator iterator = failClocks.keySet().iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				double clockEnd = failClocks.get(key).doubleValue() + this.downtimes.get(key).doubleValue()
						+ this.recoverytimes.get(key).doubleValue();

				Interval newInterval = new Interval(failClocks.get(key).doubleValue(), clockEnd);
				timeIntervals.add(newInterval);
			}
		}
		Collections.sort(timeIntervals);
		System.out.println("**First sorted Intervals:");
		if (!timeIntervals.isEmpty()) {
			for (int i = 0; i < timeIntervals.size(); i++) {
				System.out.println("intervalo: (" + ((Interval) timeIntervals.get(i)).getMinValue() + ", "
						+ ((Interval) timeIntervals.get(i)).getMaxValue() + ")");
			}
		}
		biggerTimeIntervals = generateNewTimeInterval(timeIntervals);
		sysNoAvaiTime = noAvailabilityTime(biggerTimeIntervals);
		return sysNoAvaiTime;
	}

	private double noAvailabilityTime(List timeIntervalsAux) {
		double noAvaiTime = 0;

		if (!timeIntervalsAux.isEmpty()) {
			for (int i = 0; i < timeIntervalsAux.size(); i++) {
				noAvaiTime += ((Interval) timeIntervalsAux.get(i)).getMaxValue()
						- ((Interval) timeIntervalsAux.get(i)).getMinValue();
			}
		}
		return noAvaiTime;
	}

	private List<Interval> generateNewTimeInterval(List timeIntervalsAux) {
		List<Interval> biggerTimeIntervals = new ArrayList<Interval>();

		while (!timeIntervalsAux.isEmpty()) {
			double t1 = 0;
			double t2 = 0;

			for (int i = 0; i < timeIntervalsAux.size(); i++) {
				if (i == 0 && !(((Interval) timeIntervalsAux.get(0)).checked())) {
					t1 = ((Interval) timeIntervalsAux.get(0)).getMinValue();
					t2 = ((Interval) timeIntervalsAux.get(0)).getMaxValue();

					((Interval) timeIntervalsAux.get(0)).setChecked(true);

				} else {
					if (((Interval) timeIntervalsAux.get(i)).getMinValue() <= t2
							&& ((Interval) timeIntervalsAux.get(i)).getMaxValue() > t2
							&& (!((Interval) timeIntervalsAux.get(i)).checked())) {
						t2 = ((Interval) timeIntervalsAux.get(i)).getMaxValue();
						((Interval) timeIntervalsAux.get(i)).setChecked(true);
					} else if (((Interval) timeIntervalsAux.get(i)).getMinValue() <= t2
							&& ((Interval) timeIntervalsAux.get(i)).getMaxValue() <= t2) {
						((Interval) timeIntervalsAux.get(i)).setChecked(true);
					}

				}
			}

			for (int i = 0; i < timeIntervalsAux.size(); i++) {
				if (((Interval) timeIntervalsAux.get(i)).checked()) {
					timeIntervalsAux.remove(i);
				}
			}
			biggerTimeIntervals.add(new Interval(t1, t2));
		}

		return biggerTimeIntervals;
	}

	public Map computeResponsibilityDowntime() {
		Map<Integer, Double> respMeanDowntime;
		respMeanDowntime = new HashMap<Integer, Double>();

		if (!respDowntime.isEmpty()) {
			Iterator iterator = respDowntime.keySet().iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				double totalDowntime = respDowntime.get(key).doubleValue();
				int totalFails = respfails.get(key).intValue();

				// calculating the downtime per resposibility
				respMeanDowntime.put((Integer) key, (Double) (totalDowntime / totalFails));

			}
		}
		return respMeanDowntime;
	}

	public Map computeResponsibilityRecoveryTime() {
		Map<Integer, Double> respMeanRecoveryTime;
		respMeanRecoveryTime = new HashMap<Integer, Double>();

		if (!respRecovTime.isEmpty()) {
			Iterator iterator = respRecovTime.keySet().iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				double totalRecovTime = respRecovTime.get(key).doubleValue();
				int totalFails = respfails.get(key).intValue();

				// calculating the recovery time for each responsibility
				respMeanRecoveryTime.put((Integer) key, (Double) (totalRecovTime / totalFails)); // tiempo
																									// medio
																									// q
																									// una
																									// responsabilidad
																									// tarda
																									// en
																									// recuperarse
																									// (en
																									// promedio)

			}
		}
		return respMeanRecoveryTime;
	}

	/**
	 * printResponsibilityDowntimes() Function that print on the console the
	 * downtime (average) per responsibility
	 * 
	 * @return void
	 */
	public void printResponsibilityDowntimes() {
		Map respDowntimes = new HashMap<Integer, Double>();

		respDowntimes = computeResponsibilityDowntime();

		System.out.println("***Downtime per responsibility (id, total downtime):  ");

		if (!respDowntimes.isEmpty()) {
			Iterator iterator = respDowntimes.keySet().iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();
				Double respDowntime = (Double) respDowntimes.get(key);

				System.out.println(key + "  " + respDowntime.doubleValue());
			}
		}

	}

	public void printResponsibilityDowntimeIntervals() {

		System.out.println("**Time Intervals (downtime per responsibility per failure):");
		if (!failClocks.isEmpty()) {
			Iterator iterator = failClocks.keySet().iterator();
			while (iterator.hasNext()) {
				Object key = iterator.next();

				double clockEnd = failClocks.get(key).doubleValue() + this.downtimes.get(key).doubleValue()
						+ this.recoverytimes.get(key).doubleValue();
				System.out.println(((List) key).get(0) + ", " + ((List) key).get(1) + "  -"
						+ failClocks.get(key).doubleValue() + ", " + clockEnd);
			}
		}

	}
}
