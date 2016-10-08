package SimEnvironment.SAModel.Library;

import GenCol.Queue;
import GenCol.entity;
import model.modeling.atomic;
import model.modeling.message;
import view.modeling.ViewableAtomic;

public class ORJoin extends ViewableAtomic {
	// internal variables
	private int orjoinid;

	// clock from simulation
	// private double clock;

	// Queue for the requests that wait to be processed
	private Queue requests;

	// removed request to send to the next responsibility
	private Request lastRequest;

	// Constructors
	// By default:
	public ORJoin() {
		this(1, "ORjoin1", 2); // ver bien el valor de la media
	}

	// With parameters: refExecutionTime (upper limit in uniform distribution)
	public ORJoin(int id, String name, int ipnumber) {
		super(name);
		this.setORJoinId(id);

		// Request queue
		requests = new Queue();

		// Define the ports for the model:
		// Input ports
		// primeros puertos de entrada deben ser reservados para los path,
		// los demas despues (array ordenado indice corresponda con el numero
		// del pathin del nombre)
		for (int i = 1; i <= ipnumber; i++) {
			addInport("pathin" + i); // input from predecessor responsibilities
		}

		// output ports
		addOutport("pathout"); // output to successor responsibilities

		// Ports tests (each line add a line in the inject box)
		// addTestInput("pathin0",new bag(new Request("request11",1), new
		// Request("request12",1)));
		addTestInput("pathin1", new Request("request1", 1), 0);
		addTestInput("pathin1", new Request("request2", 2), 0);
		addTestInput("pathin2", new Request("request3", 3), 0);
	}

	public void initialize() {
		passivateIn("inactive"); // phase = "inactive" and sigma = INFINITY
		super.initialize();
	}

	// accessors -----------------------------------------------

	public void setORJoinId(int id) {
		this.orjoinid = id;
	}

	public int getORJoinId() {
		return this.orjoinid;
	}

	// ------------------------------------------------------------

	// External transition
	public void deltext(double e, message x) {
		// clock = clock + e;
		Continue(e); // sigma-e

		// inactive-->active
		// TODO: preguntar a vero del error
		if (phaseIs("inactive")) {
			for (int i = 0; i < x.getLength(); i++) {
				String pathname = "";
				for (int j = 0; j < this.getNumInports(); j++) {
					if (messageOnPort(x, "pathin" + (j + 1), i)) {
						entity arrRequest = x.getValOnPort("pathin" + (j + 1), i);
						requests.add(arrRequest);

						// setting the new state
						holdIn("active", 0); // sigma= 0, transitory state
					}
				}
			}
		} else if (phaseIs("active")) { // active
			for (int i = 0; i < x.getLength(); i++) {
				String pathname = "";
				for (int j = 0; j < this.getNumInports(); j++) {
					if (messageOnPort(x, "pathin" + (j + 1), i)) {
						entity arrRequest = x.getValOnPort("pathin" + (j + 1), i);
						requests.add(arrRequest);

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

	// 1° output, 2° internal transition
	public message out() {
		message m = new message();

		if (phaseIs("active")) {
			if (!requests.isEmpty()) {
				// remove the firts element in the queue of requests
				lastRequest = (Request) requests.remove();

				// sends the request to the next responsibility
				m.add(makeContent("pathout", this.lastRequest));
			}
		}

		// return the message, using the corresponding ports
		return m;
	}

	public void showState() {
		super.showState();
		System.out.println("ORJoin id: " + this.getORJoinId());
		System.out.println("Queue Length (requests): " + this.requests.size());

	}


}// class