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

import SimEnvironment.SAModel.Library.Request;

import util.*;

public class SAEGenerator extends ViewableAtomic{
 
  //Fixed parameters
  //interarrival time
  private double interarrtime;
 
  //internal information:
  //call counter
  private int count;
 
  //random number
  private rand randnumber;

  //Constructor by default
  public SAEGenerator() {
      this("SAE-Generator", 30);
  }
 
  //Constructor by parameters: generator name and the interarrival period.
  public SAEGenerator(String name,double arrtime){
    super(name);
  
    //input ports
    addInport("ssip");
    
    //output ports
    addOutport("rop");
  
    //create the generator of random numbers for simulate the time between requests
    randnumber=new rand(2091);
    
    // assign interarrival period
    this.setIntArrPeriod(arrtime);

    //testing the ports
    addTestInput("ssip",new entity("start"));
    addTestInput("ssip",new entity("stop"));
  }

  //Accessors:
  //setting the interarrival period (time between 2 request)
  public void setIntArrPeriod(double arrtime){
      this.interarrtime=arrtime;
  }
 
  public double getIntArrPeriod(){
      return this.interarrtime;
  }
 
 
    public void initialize(){
        //the initial state, where is until an event indicating the start of simulation
        //(execution of the system, an user or system request sth)
        passivateIn("inactive");
       
        //request number
        count = 0;
       
        //initializing vbles from super
        super.initialize();
     }

    public void  deltext(double e,message x){
        Continue(e);
       
        if(phaseIs("inactive")){
           for (int i=0; i< x.getLength();i++){
              if (messageOnPort(x,"ssip",i)){
            	  entity ssipEvent = x.getValOnPort("ssip",i);
              	  if (ssipEvent.getName().equals("start")){
              		  holdIn("busy",randnumber.expon(this.getIntArrPeriod()));
              	  }
           	   }
           }
    	}
        
       
         if(phaseIs("busy")){
           for (int i=0; i< x.getLength();i++){
              if (messageOnPort(x,"ssip",i)) {
            	  entity ssipEvent = x.getValOnPort("ssip",i);
            	  if (ssipEvent.getName().equals("stop")){
            		  passivateIn("inactive");
            	  }//if
              }//if
           }//for
         }
    }

    public void  deltint( ){
        if(phaseIs("busy")){
            count = count +1;
            holdIn("busy",randnumber.expon(this.getIntArrPeriod()));
        }
    }

  //Confluent Function
    public void deltcon(double e,message x)
    {
       deltint();
       deltext(0,x);
    }
    
    public message  out( ){
        message  m = new message();
        content con = makeContent("rop",new Request("request" + count, count));
        m.add(con);
       
        
        System.out.println("Sent requests:  " + this.count);
        
        
        return m;
    }

    public void showState(){
        super.showState();
        System.out.println("Interarrival time " + this.getIntArrPeriod());
    }

    public String getTooltipText(){
       return
       super.getTooltipText()
        +"\n"+" Interarrival time: " + this.getIntArrPeriod()
         +"\n"+" count: " + count;
    }
}