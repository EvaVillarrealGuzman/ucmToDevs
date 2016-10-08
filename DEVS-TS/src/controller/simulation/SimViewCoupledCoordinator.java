/*     
 *    
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */
package controller.simulation;

import model.modeling.*;
import model.simulation.*;
import model.simulation.realTime.*;

/**
 * A tunable-coupled-coordinator specialized to operate under the direction
 * of a SimViewCoordinator.
 */
public class SimViewCoupledCoordinator extends TunableCoupledCoordinator
{
    /**
     * Holds part of the implementation for this coordinator.
     */    
    protected SimViewCoordinatorBase base;

    /**
     * Constructor.
     *
     * See parent constructor for parameter descriptions.
     */
    public SimViewCoupledCoordinator(digraph digraph, Listener listener)
    {
        super(digraph, listener);
    }

    /**
     * See parent method.
     */
    @Override
	protected void constructorHook1() {base = new SimViewCoordinatorBase();}

    /**
     * See parent method.
     */
    @Override
	protected void addSimulatorHook1(coupledSimulator simulator)
    {
        base.setListenerIntoSimulator(listener, simulator);
    }

    /**
     * See parent method.
     */
    @Override
	protected coupledSimulator createSimulatorHook1(IOBasicDevs devs)
    {
        return base.createSimulator(devs);
    }

    /**
     * See parent method.
     */
    @Override
	protected TunableCoupledCoordinator addCoordinatorHook1(digraph digraph)
    {
        return new SimViewCoupledCoordinator(digraph,
            listener);
    }
}