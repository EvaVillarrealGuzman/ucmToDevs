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
import view.modeling.ViewableAtomic;

public class ANDFork extends ViewableAtomic{
	//internal variables
	private int andforkid;

	
	//clock from simulation
	//private double clock;
	
	//Queue for the requests that wait to be processed 
	private Queue requests;
	
	//removed request to send to the next responsibility
	private Request lastRequest;
	//String selectedport;
	
	//Constructors 
	//By default:
	public ANDFork(){
        this(1,"ANDfork1", 2); //ver bien el valor de la media
    }

	//With parameters: refExecutionTime (upper limit in uniform distribution)
    public ANDFork(int id, String  name, int opnumber){
        super(name);
        this.setANDForkId(id);
        
        //Request queue
        requests = new Queue();
               
        //Define the ports for the model:
        //Input ports
        addInport("pathin"); //input from predecessor responsibilities
        
        //output ports
        //output ports
        for (int i=1; i<= opnumber; i++){
        	addOutport("pathout"+i); //output to successor responsibilities
        }
         
        //Ports tests (each line add a line in the inject box)
        addTestInput("pathin",new Request("request1",1), 0);
        addTestInput("pathin",new Request("request2",2), 0);
        addTestInput("pathin",new Request("request3",3), 0);
        addTestInput("pathin",new Request("request4",4), 0);
    }

    public void initialize(){
    	passivateIn("inactive"); // phase = "inactive" and sigma = INFINITY
        super.initialize();
     }

    //accessors -----------------------------------------------

	public void setANDForkId(int id) {
		this.andforkid = id;
	}
	
	public int getANDForkId() {
		return this.andforkid;
	}

	//------------------------------------------------------------

	//External transition
    public void  deltext(double e,message x) {
    	//clock = clock + e;
        Continue(e); //sigma-e 
        
        //inactive-->active
        if (phaseIs("inactive")){
            for (int i=0; i< x.getLength();i++){
                if (messageOnPort(x,"pathin",i)){
                	entity arrRequest = x.getValOnPort("pathin",i);
                	requests.add(arrRequest);
                	
                	//setting the new state
                	holdIn("active",0); //sigma= 0, transitory state
                }
            }
        } else if (phaseIs("active")){ //active
        	for (int i=0; i< x.getLength();i++){
                if (messageOnPort(x,"pathin",i)){
                	entity arrRequest = x.getValOnPort("pathin",i);
                	requests.add(arrRequest);
                }
            }
        }
        
      //sacar--------------------------------
    	//this.showState();
    	//--------------------------------------
    	
    }

    //Internal Transition
    public void  deltint(){
    	//clock = clock + sigma;
    	
    	if (phaseIs("active")){ //active-->active
    		if (!requests.isEmpty()) {
    			holdIn("active",0);
    		}else{//active-->inactive
    			passivateIn("inactive");//phase=inactive and sigma=infinity
    		}
    	}
    }

    //Confluent Function
    public void deltcon(double e,message x)
    {
       deltint();
       deltext(0,x);
    }

    
    public message out( ) {
    	message   m = new message();
    
    	//TODO preguntar
    	if (phaseIs("active")) {
    		if (!requests.isEmpty()) {
    			//Extrae de la cola el primer requerimiento
    			lastRequest= (Request)requests.remove();
	    		//sends the request to the next responsibility
				ArrayList<String> portnames = (ArrayList) this.getOutportNames();
	    		for (int i=0; i<this.getNumOutports();i++){
					m.add(makeContent(portnames.get(i),this.lastRequest ));
	    		}
    		}
		}
	   
	   //this.lastRequest=null;
    	
	   //return the message, using the corresponding ports
	   return m;
    }

	public void showState(){
        super.showState();
        System.out.println("ANDFork id: " + this.getANDForkId());
        System.out.println("Queue Length (requests): " + this.requests.size());

    }

}