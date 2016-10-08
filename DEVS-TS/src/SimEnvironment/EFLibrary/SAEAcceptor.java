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

import GenCol.*;
import model.modeling.*;
import view.modeling.ViewableAtomic;

public class SAEAcceptor extends ViewableAtomic{
	double simTime = 0.0;
	
    public SAEAcceptor(String  name, double stime){
        super(name);
               
        this.setSimTime (stime);
        
        //adding the inputs ports
        this.addInport("sip"); //start input port
        
        // adding the output ports
        this.addOutport("ssop");  //start|stop output port     
    
        //initialize
        this.initialize();
        
        //testing the input ports
        addTestInput("sip",new entity("start"));
        
        
    }
    
    public SAEAcceptor(){this("SAEAcceptor",200);}
    
    public void initialize() {
        passivateIn("inactive");
        super.initialize();
    }
    
    public double getSimTime() {
		return simTime;
	}

	public void setSimTime(double simtime) {
		simTime = simtime;
	}   
	       
    public void deltint() {
        if(phaseIs("active"))
            holdIn("simulating",this.getSimTime());
        else if(phaseIs("simulating"))
        	passivateIn("inactive");

    }
	         
    public void deltext(double e, message x){

        Continue(e); //sigma-e 
        
        //inactive-->executing
        if (phaseIs("inactive")){
            for (int i=0; i< x.getLength();i++){
                if (messageOnPort(x,"sip",i)){
                	entity sripEvent=x.getValOnPort("sip",i);
                	if (sripEvent.getName().equals("start")){
                		//setting the new state
                    	holdIn("active",0); //sigma= 0, transitory state
                	}//if
                }//if
            }//for
        }//if
    }//function
	     
  //Confluent Function
    public void deltcon(double e,message x)
    {
       deltint();
       deltext(0,x);
    }
    
    public message out() {
      
        message m = new message();
        
        if(phaseIs("active")){
        	//information for the successor elements
        	m.add(makeContent("ssop", new entity("start")));
        }else if(phaseIs("simulating")){
        	m.add(makeContent("ssop", new entity("stop")));
        }
   
        return m;
    }

    
    public void showState(){
        super.showState();
        System.out.println("Simulation time (sec): " +this.getSimTime());
    }
    
}
