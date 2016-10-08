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

import GenCol.Queue;
import GenCol.entity;
import model.modeling.message;
import util.rand;
import view.modeling.ViewableAtomic;

public class ORFork extends ViewableAtomic {
	// internal variables
	private int orforkid;
	private ArrayList<Double> pathProbabilities;

	// Queue for the requests that wait to be processed
	private Queue requests;

	// removed request to send to the next responsibility
	private Request lastRequest;
	String selectedport;
	private rand rndselectedport;

	// Constructors
	// By default:
	public ORFork() {
		this(1, "ORfork1", 2, new ArrayList<Double>()); // ver bien el valor de
														// la media
	}

	// With parameters: refExecutionTime (upper limit in uniform distribution)
	public ORFork(int id, String name, int opnumber, ArrayList<Double> pathprobabilities) {
		super(name);
		this.setORForkId(id);
		this.setPathProbabilities(pathprobabilities);

		// Request queue
		requests = new Queue();

		// random output port
		rndselectedport = new rand(1);

		// Define the ports for the model:
		// Input ports
		addInport("pathin"); // input from predecessor responsibilities

		// output ports
		for (int i = 1; i <= opnumber; i++) {
			addOutport("pathout" + i); // output to successor responsibilities
		}

		// Ports tests (each line add a line in the inject box)
		addTestInput("pathin", new Request("request1", 1), 0);
		addTestInput("pathin", new Request("request2", 2), 0);
		addTestInput("pathin", new Request("request3", 3), 0);
		addTestInput("pathin", new Request("request4", 4), 0);
	}

	public void initialize() {
		passivateIn("inactive"); // phase = "inactive" and sigma = INFINITY
		super.initialize();
	}

	// accessors -----------------------------------------------

	public void setORForkId(int id) {
		this.orforkid = id;
	}

	public int getORForkId() {
		return this.orforkid;
	}

	public ArrayList<Double> getPathProbabilities() {
		return pathProbabilities;
	}

	public void setPathProbabilities(ArrayList<Double> pathProbabilities) {
		this.pathProbabilities = pathProbabilities;
	}

	// ------------------------------------------------------------

	// External transition
	public void deltext(double e, message x) {
		// clock = clock + e;
		Continue(e); // sigma-e

		// inactive-->active
		if (phaseIs("inactive")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "pathin", i)) {
					entity arrRequest = x.getValOnPort("pathin", i);
					requests.add(arrRequest);

					// setting the new state
					holdIn("active", 0); // sigma= 0, transitory state
				}
			}
		} else if (phaseIs("active")) { // active
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "pathin", i)) {
					entity arrRequest = x.getValOnPort("pathin", i);
					requests.add(arrRequest);
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

		if (phaseIs("active")) { // active-->active
			if (!requests.isEmpty()) {
				holdIn("active", 0);
			} else {// active-->inactive
				passivateIn("inactive");// phase=inactive and sigma=infinity
			}
		}
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
				// Extrae de la cola el primer requerimiento
				lastRequest = (Request) requests.remove();

				// calculate output port for the request
				selectedport = this.selectedOutputPort();
				System.out.println("PUERTO: " + selectedport + " VALOR: " + lastRequest.getName());

				// sends the request to the next responsibility
				m.add(makeContent(selectedport, this.lastRequest));
			}
		}

		// this.lastRequest=null;

		// return the message, using the corresponding ports
		return m;
	}

	// elige el puerto (camino ) por el cual se envía el request según
	// probabilidad de ser elegido
	public String selectedOutputPort() {
		int portnum = 1;
		double p = this.rndselectedport.uniform(0, 1);

		for (int i = 0; i < this.getPathProbabilities().size(); i++) {
			if (p <= this.getPathProbabilities().get(i).doubleValue()) {
				portnum = i;
			}
		}

		String portname = "pathout" + portnum;
		return portname;
	}

	public void showState() {
		super.showState();
		System.out.println("ORFork id: " + this.getORForkId());
		System.out.println("Queue Length (requests): " + this.requests.size());

	}

}// class