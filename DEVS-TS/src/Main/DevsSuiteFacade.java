package Main;
import model.modeling.atomic;
import model.modeling.coupledDevs;
import model.modeling.digraph;
import model.simulation.atomicSimulator;
import model.simulation.coupledCoordinator;
import model.simulation.coordinator;
import model.simulation.realTime.*;




//
import facade.simulation.*;
import facade.modeling.*;
import view.modeling.ViewableAtomic;
import view.modeling.ViewableDigraph;
//
import GenCol.*;



/**
 * Facade class to use Devs-Suite core.
 *
 * @author ezequiel
 */
public class DevsSuiteFacade {
    //Model Simulator/coordinator 
    //private  coordinator simulator;
    
    private  TunableCoordinator simulator;
    

    /**
     * Constructor to simulate a DEVSs Coupled Model
     *
     * @param instanceModel
     */
/*   public DevsSuiteFacade(digraph instanceModel) {
        this.simulator = new coupledCoordinator(instanceModel, true); //SetSimulators = true 
    }*/
   
   
//
//    /**
//     * Constructor to simulate a DEVSs Atomic Model
//     *
//     * @param instanceModel
//     */
//    public DevsSuiteFacade(atomic instanceModel) {
//        this.simulator = new atomicSimulator(instanceModel);
//        this.simulator.initialize(0); //Inicialize at currentTime
//    }

    
    public DevsSuiteFacade(digraph instanceModel){
    	//this.simulator = new coordinator(instanceModel); // llama al constructor coordinator(model, true,null) donde true--> setSimulators() y informCoupling()    	
    	//this.simulator = new RTCentralCoord(instanceModel, false, null);
    	
    	this.simulator=new TunableCoordinator(instanceModel);
    	
    }
    
    /**
     * Method to start the model simulation
     *
     * @param iterations
     */
    public void simulate(Integer iterations) {
        simulator.simulate(iterations); //Start Simulation
     
    }
    
    
    
    /**
     * Method to start the model simulation
     *
     * @param iterations
     */
    public void simulate(double elapsedtime, String portname, entity pentity) {
        //EJECUTAR EN BASE A UN EVENTO
        simulator.simInject(elapsedtime,portname, pentity); 
     
    }

//    public List<Map> getSimulationResults() {
//  //      return simulator.getSimulationResults();
//    }

}
