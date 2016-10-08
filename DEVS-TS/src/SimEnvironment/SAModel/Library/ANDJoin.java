/*      Copyright 2002 Arizona Board of regents on behalf of
 *                  The University of Arizona
 *                     All Rights Reserved
 *         (USE & RESTRICTION - Please read COPYRIGHT file)
 *
 *  Version    : DEVSJAVA 2.7
 *  Date       : 01-06-10
 */

package SimEnvironment.SAModel.Library;

import java.util.ArrayList;

import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class ANDJoin extends ViewableAtomic {
	// internal variables
	private int andjoinid;
	private int pathinnumber;

	// clock from simulation
	// private double clock;

	// Queue for the requests that wait to be processed
	private ArrayList requests;
	private ArrayList syncounter;

	// removed request to send to the next responsibility
	private Request lastRequest;
	// String selectedport;

	// Constructors
	// By default:
	public ANDJoin() {
		this(1, "ANDjoin1", 2); // ver bien el valor de la media
	}

	// With parameters: refExecutionTime (upper limit in uniform distribution)
	public ANDJoin(int id, String name, int ipnumber) {
		super(name);
		this.setAndjoinid(id);
		this.setPathinnumber(ipnumber);

		// Request queue
		requests = new ArrayList();
		syncounter = new ArrayList();

		// Define the ports for the model:
		// Input ports
		for (int i = 1; i <= ipnumber; i++) {
			addInport("pathin" + i); // input from predecessor responsibilities
		}
		// prueba
		// addInport("pathprueba");

		// output ports
		addOutport("pathout"); // output to successor responsibilities

		// Ports tests (each line add a line in the inject box)
		Request r1 = new Request("request1", 1);
		Request r2 = new Request("request2", 1);
		addTestInput("pathin1", r1, 0);
		addTestInput("pathin1", r2, 0);
		addTestInput("pathin2", r1, 0);
		addTestInput("pathin2", r2, 0);
		// addTestInput("pathprueba",new entity("prueba"), 0);
	}

	public void initialize() {
		passivateIn("inactive"); // phase = "inactive" and sigma = INFINITY
		super.initialize();
	}

	// accessors -----------------------------------------------

	public int getAndjoinid() {
		return andjoinid;
	}

	public void setAndjoinid(int andjoinid) {
		this.andjoinid = andjoinid;
	}

	public int getPathinnumber() {
		return pathinnumber;
	}

	public void setPathinnumber(int pathinnumber) {
		this.pathinnumber = pathinnumber;
	}

	// ------------------------------------------------------------

	// External transition
	public void deltext(double e, message x) {
		// clock = clock + e;
		Continue(e); // sigma-e

		// inactive-->active
		if (phaseIs("inactive")) {
			for (int i = 0; i < x.getLength(); i++) {
				for (int j = 1; j <= this.getNumInports(); j++) {
					if (messageOnPort(x, "pathin" + j, i)) {
						entity arrRequest = x.getValOnPort("pathin" + j, i);
						if (requests.contains(arrRequest)) {
							int index = requests.indexOf(arrRequest);
							Integer count = (Integer) syncounter.get(index);
							int value = (count.intValue()) + 1;
							syncounter.set(index, (Integer) value);

							// System.out.println("EXISTENTE (CONTADOR): " +
							// value);
						} else {
							requests.add(arrRequest);
							syncounter.add((Integer) 1);

							// System.out.println("AGREGADO NUEVO: "
							// +arrRequest.getName() + "-TAM:"+requests.size() +
							// " Cont= " + (Integer)1);
						}

						// setting the new state

						if (!requests.isEmpty()) {
							boolean change = false;
							for (int k = 0; k < requests.size(); k++) {

								if (((Integer) syncounter.get(k)).equals(this.getPathinnumber())) {
									change = true;

								}
							}
							if (change) {

								holdIn("active", 0); // to emit the requests
														// that have arrived
														// from all ports
														// (pathin)
							} else {
								passivateIn("inactive");// to continue recording
														// the requests
							}
						}

						// holdIn("active",0); //sigma= 0, transitory state

					}
				}
			}
		} else if (phaseIs("active")) { // active
			for (int i = 0; i < x.getLength(); i++) {
				for (int j = 1; j <= this.getNumInports(); j++) {
					if (messageOnPort(x, "pathin" + j, i)) {
						entity arrRequest = x.getValOnPort("pathin" + j, i);
						if (requests.contains(arrRequest)) {
							int index = requests.indexOf(arrRequest);
							Integer count = (Integer) syncounter.get(index);
							int value = (count.intValue()) + 1;
							syncounter.set(index, (Integer) value);
							// System.out.println("EXISTENTE (CONTADOR): " +
							// value);
						} else {
							requests.add(arrRequest);
							syncounter.add((Integer) 1);

							// System.out.println("AGREGADO NUEVO: "
							// +arrRequest.getName() + "-TAM:"+requests.size() +
							// " Cont= " + (Integer)1);
						}

						/*
						 * //two possible options if (!requests.isEmpty()) {
						 * boolean change=false; for (int k=0;
						 * k<requests.size();k++){
						 * 
						 * if (((Integer)syncounter.get(k)).equals(this.
						 * getPathinnumber())){ change=true;
						 * 
						 * } } if (change){
						 * 
						 * holdIn("processing",0); //to emit the requests that
						 * have arrived from all ports (pathin) }else{
						 * passivateIn("active");//to continue recording the
						 * requests } }
						 */
					}
				}
			}
		}

		// sacar--------------------------------
		// this.showState();
		// --------------------------------------

	}

	// Internal Transition
	public void deltint() {
		// clock = clock + sigma;

		if (phaseIs("active")) {
			// active-->active
			/*
			 * if (!requests.isEmpty()) { //holdIn("active",0);
			 * passivateIn("active"); }else{//active-->inactive
			 */ passivateIn("inactive");// phase=inactive and sigma=infinity
		}
		// }
	}

	// Confluent Function
	public void deltcon(double e, message x) {
		deltint();
		deltext(0, x);
	}

	public message out() {
		message m = new message();

		if (phaseIs("active")) {
			if (!requests.isEmpty()) {
				for (int i = 0; i < requests.size(); i++) {

					if (((Integer) syncounter.get(i)).equals(this.getPathinnumber())) {
						// Extrae de la cola el primer requerimiento
						lastRequest = (Request) requests.remove(i);
						syncounter.remove(i);
						m.add(makeContent("pathout", this.lastRequest));
					}
				}
			}
		}

		// this.lastRequest=null;

		// return the message, using the corresponding ports
		return m;
	}

	public void showState() {
		super.showState();
		System.out.println("ORJoin id: " + this.getAndjoinid());
		System.out.println("Queue Length (requests): " + this.requests.size());

	}

}// class