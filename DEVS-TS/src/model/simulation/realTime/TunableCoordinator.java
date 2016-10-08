/* 
 * Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 

package model.simulation.realTime;

import model.modeling.*;
import model.simulation.*;
import util.*;

/**
 * A real-time coordinator whose simulation time scale can be adjusted
 * up or down (to control the real-time simulation speed).
 *
 * @author  Jeff Mather
 */
public class TunableCoordinator extends RTCentralCoord
{
	protected short state;
    /**
     * An object interested in events generated by this coordinator.
     * Note that this listener is expecting to be passed down to all
     * subordinate coordinators and simulators.
     */
    protected Listener listener;

    /**
     * How many milliseconds this coordinator should wait to simulate one
     * second of simulation time.
     */
    protected int timeScale = 1000;

    /**
     * Whether this coordinator is at a point where it should perform
     * more simulation iterations.
     */
    protected boolean shouldIterate = false;

    /**
     * Constructor.
     *
     * @param   digraph     The digraph model over which this coordinator
     *                      should preside.
     * @param   listener_   See member variable accessed.
     */
    public TunableCoordinator(digraph digraph, Listener listener_)
    {
        super(digraph, false, null);        
        listener = listener_;
        constructorHook1();
        setSimulators();
        informCoupling();
        state = 0;
    }

    /**
     * A convienence constructor.
     */
    public TunableCoordinator(digraph digraph) {this(digraph, null);}

    /**
     * A hook used by the simView package.
     */    
    protected void constructorHook1() {}

    /**
     * Creates a simulator for the given devs component,
     * and associates that simulator with this coordinator.
     *
     * @param   devs        The devs component for which to create a simulator.
     */
    @Override
	public void addSimulator(IOBasicDevs devs)
    {
        // get the simulator created for the given component
        coupledSimulator simulator = createSimulator(devs);

        addSimulatorHook1(simulator);

        simulatorCreated(simulator, devs);
    }

    /**
     * A hook used by the simView package.
     *
     * @param   simulator   The newly created simulator.
     */
    protected void addSimulatorHook1(coupledSimulator simulator) {}

    /**
     * Creates a simulator for the given devs component.
     *
     * @param   devs        The devs component for which to create a simulator.
     */
    protected coupledSimulator createSimulator(IOBasicDevs devs)
    {
        // if deferring to a hook doesn't produce a simulator
        // for the given component
        coupledSimulator simulator = createSimulatorHook1(devs);
        if (simulator == null) {
            // create a coupled simulator for the given component
            simulator = new coupledSimulator(devs);
        }

        // initialize the simulator
        simulator.setRootParent(this);
        simulator.initialize();

        return simulator;
    }

    /**
     * A hook used by the simView package.
     *
     * @param   devs        The devs component for which to create a simulator.
     */
    protected coupledSimulator createSimulatorHook1(IOBasicDevs devs) {return null;}

    /**
     * Creates a tunable-coupled-coordinator to preside over the given digraph
     * (which is assumed to be a child of this coordinator's digraph).
     *
     * @param   digraph     The digraph for which to create a coordinator.
     */
    @Override
	public void addCoordinator(Coupled digraph)
    {
        // if deferring to a hook doesn't produce a coordinator for the
        // given digraph
        TunableCoupledCoordinator coordinator = addCoordinatorHook1(
            (digraph)digraph);
        if (coordinator == null) {
            // create a coordinator for the given digraph
            coordinator = new TunableCoupledCoordinator((digraph)digraph,
                (TunableCoupledCoordinator.Listener)listener);
        }

        coordinator.setRootParent(this);

        simulatorCreated(coordinator, digraph);
    }

    /**
     * A hook used by the simView package.
     *
     * @param   digraph        The digraph for which to create a coordinator.
     */
    protected TunableCoupledCoordinator addCoordinatorHook1(digraph digraph) {return null;}

    /**
     * Returns the simulation time of the last event processed by this
     * coordinator.
     */
    public double getTimeOfLastEvent()
    {
        return tL;
    }
    public double getTimeOfNextEvent()
    {
        return nextTN();
    }

    /**
     * Sets how many seconds this coordinator should wait to simulate one
     * second of simulation time.
     *
     * @param   realTimeFactor      How many seconds to wait per one second
     *                              of simulation time.
     */
    public void setTimeScale(double realTimeFactor)
    {    	
        // convert the given time factor to milliseconds
        timeScale = (int)Math.floor(1000 * realTimeFactor);
    }

    /**
     * Initializes this coordinator.
     */
    @Override
	public void initialize()
    {
        simulators.tellAll("initialize");
        state = 0;
        tL = 0;
    }

    /**
     * Tells this coordinator to execute the specified number of iterations
     * of the simulation.
     *
     * @param   numIterations       The number of iterations to execute.
     */
    @Override
	public void simulate(int numIterations)
    {
        numIter = numIterations;
        //Set Current State
        currentState(numIter);
        shouldIterate = true;
        if (!myThread.isAlive()) {
            myThread.start();
        }
        
    }

    /**
     * Executes the actual iterations of the simulation.
     */
    @Override
	public void run()
    {
    	///state = 1;
        // keep doing this
        while (true) {
            // while this coordinator hasn't yet been told to do more iterations
            while (!shouldIterate) {
                // sleep a bit
                final int minWait = 100;
                Util.sleep(myThread, minWait);
            }

            // for each iteration this coordinator was told to do,
            // stopping early if there are no more next events to process
            int i = 1;
            tN = nextTN();
            while (tN < INFINITY && i <= numIter) {
                Logging.log("ITERATION " + i + " ,time: " + getTimeOfLastEvent(),
                    Logging.full);

                // detm how long to sleep before performing this iteration,
                // such that it will be performed at the actual time of next
                // event
                timeToSleep = (long)((tN - tL) * timeScale);

                // if the simulation is restarted, there may be a brief period
                // of time where tL is greater than tN (which is now zero), so
                // make sure we don't have a negative time to sleep
                timeToSleep = Math.max(timeToSleep, 0);

                // sleep for that amount
                Util.sleep(myThread, timeToSleep);

                // perform the actual simulation iteration
                computeInputOutput(tN);
                wrapDeltFunc(tN);
                tL = tN;
                tN = nextTN();
                i++;

                // inform our listener that the clock has changed
                if (listener != null) {
                    listener.clockChanged(getTimeOfLastEvent());
                }
            }

            // the iterations have now been completed
            System.out.println("Terminated Normally before ITERATION " + i +
                " ,time: "+ getTimeOfLastEvent());
            shouldIterate = false;
            
                      
            // inform our listener that the iterations have now been completed
            if (listener != null) {
                listener.iterationsCompleted();
            }
        }
    }

    /**
     * Interrupts this coordinator's iterations thread.
     */
    public void interrupt() {myThread.interrupt();}

    /**
     * An object interested in events generated by this coordinator.
     */
    static public interface Listener
    {
        /**
         * Informs this listener that the simulation clock time has changed.
         *
         * @param   newTime     The new value of the simulation clock.
         */
        void clockChanged(double newTime);

        /**
         * Informs this listener that this coordinator has finished
         * the iterations it was ordered to perform.
         */
        void iterationsCompleted();
    }

    /**
     * See member variable accessed.
     */
    public double getTimeScale() {return timeScale;}
    
    public Short getCurrentState(){return state;}
    
    public void currentState(int num){
    	//state = 0(reset), 1(run&step), 2(end or pause)
        if(num == 1)
        	state = 1;
        else if(num == 0)
        	state = 0;
        else if(num == Integer.MAX_VALUE)
        	state = 1;
        else
        	state=3;   
        
    }
}
