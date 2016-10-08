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

import java.util.*;
import GenCol.entity;
import model.modeling.message;
import view.modeling.ViewableAtomic;

import SimEnvironment.SAModel.Library.MeasureMsg;
import SimEnvironment.SAModel.Library.Request;


public class SAPerformanceStat extends ViewableAtomic {
	 private Map<Integer,Double> arta,rata, rath, sentReqClock,procReqClock; //turnaround time for responsibility and request count
	 private Map<Integer,Integer> rcreq;
	 private List<MeasureMsg> tarr;
	 private double clock;
          
     public SAPerformanceStat(String  name){
      super(name);
      //adding input ports
      addInport("rtaip"); //responsibility ta per each processed request (to calculate average ta for each responsibility)
      addInport("ssip"); //start/stop input port
      addInport("sentreqip"); //sent request (from the generator)
      addInport("procreqip"); //processed request (from the output of the system)
     
      //Adding output ports
      addOutport("staop");
      addOutport("sthop");
      
      //lists of requests resolved by each responsibility
      rcreq = new HashMap<Integer,Integer>();
      rata = new HashMap<Integer,Double>();
      rath = new HashMap<Integer,Double>();
      arta = new HashMap<Integer,Double>();
      tarr = new ArrayList<MeasureMsg>();
      
      sentReqClock = new HashMap<Integer,Double>();
      procReqClock = new HashMap<Integer,Double>();
         
      //testing the input ports
      //request 1:
      Request rq1= new Request("request 1",1);
      addTestInput("sentreqip",rq1);
      addTestInput("procreqip",rq1,0);
      addTestInput("rtaip",new MeasureMsg("ta 1",1,1,10.0),10);
      addTestInput("rtaip",new MeasureMsg("ta 2",2,1,30.0),10);
      addTestInput("rtaip",new MeasureMsg("ta 3",3,1,50.0),0);
      addTestInput("rtaip",new MeasureMsg("ta 4",4,1,10.0),10);
      
      //request 2:
      Request rq2= new Request("request 2",2);
      addTestInput("sentreqip",rq2);
      addTestInput("procreqip",rq2);
      addTestInput("rtaip",new MeasureMsg("ta 1",1,2,30.0),20);
      addTestInput("rtaip",new MeasureMsg("ta 2",2,2,20.0),10);
      addTestInput("rtaip",new MeasureMsg("ta 3",3,2,40.0),40);
      addTestInput("rtaip",new MeasureMsg("ta 4",4,2,20.0),10);
      
      //start/stop input port
      addTestInput("ssip",new entity("start"));
      addTestInput("ssip",new entity("stop"));
      
      //initializing the state and variables
      initialize();
     }

     public SAPerformanceStat() {this("SAEPerformanceStats");}

     public void initialize(){
      phase = "inactive";
      sigma = INFINITY;
      clock = 0;
      super.initialize();
     }

     public void showState(){
      super.showState();
     }

     //external transition function
     public void  deltext(double e,message  x){
    	//advancing internal time
		clock = clock + e;
		
		//continuing the time in the same state (active)
		Continue(e);
		
		for(int i=0; i< x.size();i++){
			if (phaseIs("calculating")){
				if(messageOnPort(x,"rtaip",i)){
	        	
					//getting the entity in the message
					//value received from the ports
					entity val = x.getValOnPort("rtaip",i);
					   
					//the entity in the message is the type of MeasureMsg (index)
					MeasureMsg valm=(MeasureMsg) val;
					
					//adding message to the list
					tarr.add(valm);
					double taAux=0;
					int reqCount=0;
					if (arta.containsKey((Integer)valm.getResponsibilityId())){ 
						taAux = arta.get((Integer)valm.getResponsibilityId()).doubleValue();
						reqCount=rcreq.get((Integer)valm.getResponsibilityId()).intValue();
					}
					
					taAux+= valm.getValue();
					reqCount++;
					
					//adding the element or updating an existing element 
					arta.put((Integer)valm.getResponsibilityId(), (Double)taAux);
				
					//adding the new value for the counter
					rcreq.put((Integer)valm.getResponsibilityId(),(Integer) reqCount);
					
				}//if
			
				if(messageOnPort(x,"sentreqip",i)){//event on port sent request
					//getting the entity in the message
					//value received from the sent request input port
					entity val = x.getValOnPort("sentreqip",i);
					   
					//the entity in the message is the type of Request
					Request request=(Request) val;
					sentReqClock.put((Integer)request.getRequestId(), (Double)clock);
				}
				
				if(messageOnPort(x,"procreqip",i)){//event on port sent request
					//getting the entity in the message
					//value received from the processed request input port
					entity val = x.getValOnPort("procreqip",i);
					   
					//the entity in the message is the type of Request
					Request request=(Request) val;
					procReqClock.put((Integer)request.getRequestId(), (Double)clock);
				}
				
				if(messageOnPort(x,"ssip",i)){ //event on port ssip, control variable
					entity ssipEvent=x.getValOnPort("ssip",i);
					if (ssipEvent.getName().equals("stop")){
						holdIn("resultant",0); //sigma= infinity, passive state	
					}
				}
		}else if (phaseIs("inactive")){
			if(messageOnPort(x,"ssip",i)){
				entity ssipEvent=x.getValOnPort("ssip",i);
				if (ssipEvent.getName().equals("start")){
					passivateIn("calculating"); //sigma= infinity, passive state
				}
			}
			
		}//if
			
		}//for
		showState();
     }

     //Internal transition function
     public void  deltint(){
    	 //updating the internal clock (clock of this model)
    	 clock = clock + sigma;
    	 
    	 //changing the state to passive
    	 if (phaseIs("resultant")){ //resultant-->inactive
    		 passivateIn("inactive");
    	 }   	 

     }

   //Confluent Function
     public void deltcon(double e,message x)
     {
        deltint();
        deltext(0,x);
     }
     
     public  message    out( ){
		message  m = new message();
		 
		if (phaseIs("resultant")) {
			m.add(makeContent("staop",new entity("Sys TA: "+computeSystemTAAvg())));
			m.add(makeContent("sthop",new entity("Sys Thr: "+computeSystemThruPutAvg())));
			
			//print on console the metrics for the system
			System.out.println("System turnaround time: " + this.computeSystemTAAvg());
			System.out.println("System throughtput: " + this.computeSystemThruPutAvg());
			System.out.println("Responsibility turnaround time: "+this.computeResponsibilityTAAvg());
			System.out.println("Responsibility throughput: " +this.computeResponsibilityThruPutAvg());
			
			//print responsibility values
			this.printResponsibilityTA();
			this.printResponsibilityThroughput();
		}
		  
		return m;
     }

    
     //-------------Indicators: Performance. ------------------/
     
    //tiempo promedio de respuesta del sistema
	public double computeSystemTAAvg (){
		double avgTa = 0;
		double requestta = 0;
	
		if(!procReqClock.isEmpty()){
			Iterator iterator = procReqClock.keySet().iterator();
			while(iterator.hasNext()){
				Object key   = iterator.next();
				Double procClock = procReqClock.get(key);
				Double sentClock = sentReqClock.get(key);
				 
				//calculating the turnaround time for the request
				requestta =procClock.doubleValue()- sentClock.doubleValue();
				
				//total time
				avgTa += requestta;
			}
		}
		//calculating the system turnaround time average (global number)
		return avgTa/procReqClock.size();
	}	
		
	public double computeResponsibilityTAAvg(){
		double rataaux = 0;
		double avgTa = 0;
	
		if(!arta.isEmpty()){
			Iterator iterator = arta.keySet().iterator();
			while(iterator.hasNext()){
				Object key   = iterator.next();
				Integer rqcount = rcreq.get(key);
				Double rtatotal = arta.get(key);
				 
				//calculating the turnaround time average for each responsibility
				rataaux =rtatotal.doubleValue()/rqcount.intValue();
				//saving the average
				rata.put((Integer)key,(Double)rataaux);
				//total time
				avgTa += rataaux;
			}
		}		
		//calculating the turnaround time average of the responsibilities (global number)
		return avgTa/rata.size();
	}
	
	
	public double computeSystemThruPutAvg(){
		double thruput = 0;
	   
		if(clock > 0){
			if(!procReqClock.isEmpty()){
				//calculating the turnaround time average for each responsibility
				thruput =procReqClock.size()/clock;
			}		
		}
	    return thruput;
	}

     public double computeResponsibilityThruPutAvg(){
      double thruput = 0;
      double rthrAux = 0;
      
      
      if(clock > 0){
    	  if(!rcreq.isEmpty()){
  			Iterator iterator = rcreq.keySet().iterator();
  			while(iterator.hasNext()){
  				Object key   = iterator.next();
  				Integer rqcount = rcreq.get(key);
  				  				 
  				//calculating the turnaround time average for each responsibility
  				rthrAux =rqcount.intValue()/clock;
  				//saving the average
  				rath.put((Integer)key,(Double)rthrAux);
  				//total time
  				thruput += rthrAux;
  				

  			}
  			//calculating the system turnaround time average (global number)
  			thruput = thruput/rath.size();
  		}		
      }
      	return thruput;
     }

     
     /** printResponsibilityThroughput()
 	 * 
 	 * Function that print on the console the throughput of each responsibility
 	 *  
 	 * @return void
 	 */
 	public void printResponsibilityThroughput(){
 		
 		System.out.println("\n***Throughput of each responsibility:  ");
 		if(!rath.isEmpty()){
 			Iterator iterator = rath.keySet().iterator();
 			while(iterator.hasNext()){
 				Object key   = iterator.next();
 				Double respThrPut = rath.get(key);

 				System.out.println(key + "  " + respThrPut);
 			}
 		}		
 		
 	}
 	
 	
 	/** printResponsibilityTimeAround()
 	 * 
 	 * Function that print on the console the turnaround time of each responsibility
 	 *  
 	 * @return void
 	 */
 	public void printResponsibilityTA(){
 		
 		System.out.println("\n*** Turnaround time of each responsibility:  ");
 		if(!rata.isEmpty()){
 			Iterator iterator = rata.keySet().iterator();
 			while(iterator.hasNext()){
 				Object key   = iterator.next();
 				Double respTimeAround = rata.get(key);

 				System.out.println(key + "  " + respTimeAround);
 			}
 		}		
 		
 	}
}



