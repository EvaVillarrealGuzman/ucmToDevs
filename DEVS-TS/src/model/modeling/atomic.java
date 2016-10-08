/*
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 

package model.modeling;



import GenCol.*;


import model.simulation.*;
import util.*;

//public abstract
public class atomic extends devs implements AtomicInterface,DynamicStructureInterface{
 protected double sigma;
 protected String phase;
 protected ActivityInterface a;
 protected static double  INFINITY  = DevsInterface.INFINITY;
 protected CoupledSimulatorInterface mySim;

public CoupledSimulatorInterface getSim(){
return mySim;
}

public atomic(){
this("atomic");
}

public atomic(String name){
super(name);
sigma = INFINITY;
}
public atomic(ActivityInterface a){
this(a.getName());
this.a = a;
}
@Override
public void setSimulator(CoupledSimulatorInterface mySim){
this.mySim = mySim;
}

public String getPhase(){
return phase;
}

@Override
public void setSigma(double sigma){

  this.sigma = sigma;
}

@Override
public double getSigma(){
return sigma;
}

@Override
public void initialize(){
}

@Override
public void Continue(double e){
 if (sigma < INFINITY)
    setSigma(sigma - e);
}




    @Override
	public void passivateIn(String phase) {holdIn(phase, INFINITY);}
    @Override
	public void passivate() {passivateIn("passive");}
    @Override
	public void holdIn(String phase, double sigma) {holdIn(phase, sigma, null);}

    @Override
	public void holdIn(String phase, double sigma,ActivityInterface a)
    {

        this.phase = phase;
        setSigma(sigma);
        if (a != null) {
            this.a = a;
            mySim.startActivity(a);
        }
    }


  public void startActiviry(ActivityInterface a){
    this.a = a;
    mySim.startActivity(a);
  }

  @Override
public boolean phaseIs(String phase){
  return this.phase.equals(phase);
  }


  @Override
public double ta(){
      return sigma;
  }

  @Override
public void deltint(){}
  @Override
public void deltext(double e,MessageInterface x){deltext(e,(message)x);}
  public void deltext(double e,message x){} //usual devs
  @Override
public void deltcon(double e,MessageInterface x){deltcon(e,(message)x);}
  public void deltcon(double e,message x){ //usual devs
   deltint();
   deltext(0,x);
}

 @Override
public MessageInterface Out(){

 return out();
}

 public message out(){
 return new message();
}

 @Override
public String toString(){
 return "phase :"+ phase + " sigma : " + sigma ;
 }

  @Override
public void showState(){
    Logging.log("Model "+ getName() + "'s state: ", Logging.full);
    Logging.log("phase :"+ phase + " sigma : " + sigma, Logging.full);
  }

  @Override
public ActivityInterface getActivity(){
  return a;
  }

    public String stringState(){
    return "";
    }


    // Dynamic Structure Interface implementation
    @Override
	public void addCoupling(String src, String p1, String dest, String p2){
        digraph P = (digraph)getParent();
        if(P!=null){
          //update its parent model's coupling info database
          P.addPair(new Pair(src,p1),new Pair(dest,p2));
          // update the corresponding simulator's coupling info database
          coordinator PCoord = P.getCoordinator();
          PCoord.addCoupling(src,p1,dest,p2);
        }
        /*
        else{   // this is for the distributed simulation situation
          if (mySim instanceof RTCoupledSimulatorClient)
            ((RTCoupledSimulatorClient)mySim).addDistributedCoupling(src,p1,dest,p2);
        }*/
      }

     @Override
	public void removeCoupling(String src, String p1, String dest, String p2){
        digraph P = (digraph)getParent();
        if(P!=null){
          //update its parent model's coupling info database
          P.removePair(new Pair(src,p1),new Pair(dest,p2));
          // update the corresponding simulator's coupling info database
          coordinator PCoord = P.getCoordinator();
          PCoord.removeCoupling(src,p1,dest,p2);
        }
        /*
        else{   // this is for the distributed simulation situation
          if (mySim instanceof RTCoupledSimulatorClient)
            ((RTCoupledSimulatorClient)mySim).removeDistributedCoupling(src,p1,dest,p2);
        }*/
     }

     @Override
	public void addModel(IODevs iod){
       digraph P = (digraph)getParent();
       if(P.checkNameUniqueness(iod.getName())){
     //    System.out.println(" add model: "+iod.getName());
         P.add(iod);
         coordinator PCoord = P.getCoordinator();
         PCoord.setNewSimulator( (IOBasicDevs) iod);
       }
       else System.out.println("Conflict!! -- the model name already exists");
     }

     @Override
	public void removeModel(String modelName){
       digraph P = (digraph)getParent();
       coordinator PCoord = P.getCoordinator();
       // first remove all the coupling related to that model
       PCoord.removeModelCoupling(modelName);
       // then remove the model
       IODevs iod = P.withName(modelName);
       P.remove(iod);
       PCoord.removeModel(iod);
     }

     //Copied from  saurabh
     @Override
	public void addInport(String modelName, String port){
       //System.out.print("Inside atomic inport");
       digraph P = (digraph)getParent();
       IODevs iod = P.withName(modelName);
       if (P != null){
         if (iod instanceof atomic)
           iod.addInport(port);
         else
           ((digraph)iod).addInport(iod.getName(),port);
       }else
         System.out.print(modelName+ ": Its parent is not defined");

       addInportHook(modelName,port);
     }

     public void addInportHook(String modelName, String port){
    	  //s.s("Inside addInport hook 1");
    	  System.out.print("Inport added: "+port+"      component: "+modelName);
    	}


     @Override
	public void addOutport(String modelName, String port){
       digraph P = (digraph)getParent();
       IODevs iod = P.withName(modelName);
       if (P != null){
         if (iod instanceof atomic)
           iod.addOutport(port);
         else
           ((digraph)iod).addOutport(iod.getName(),port);
       }else
         System.out.print(modelName+ ": Its parent is not defined");

      addOutportHook(modelName,port);
     }

     

     public void addOutportHook(String modelName, String port){
         //System.out.print("Inside addOutport hook 1");
        System.out.print("Outport added: "+port+"      component: "+modelName);
      }

      @Override
	public void removeInport(String modelName, String port){
        //System.out.print("Inside atomic removeInport");
        digraph P = (digraph)getParent();
          if (P != null)
          P.withName(modelName).removeInport(port);
          else
        	  System.out.print(modelName+ ": Its parent is not defined");

          removeInportHook(modelName,port);
      }

      public void removeInportHook(String modelName, String port){
        //System.out.print("Inside removeInport hook 1");
        System.out.print("Inport removed: "+port+"      component: "+modelName);
      }

      @Override
	public void removeOutport(String modelName, String port){
        //System.out.print("Inside atomic removeOutport");
        digraph P = (digraph)getParent();
          if (P != null)
            P.withName(modelName).removeOutport(port);
          else
            System.out.print(modelName+ ": Its parent is not defined");

          removeOutportHook(modelName,port);
      }

      public void removeOutportHook(String modelName, String port){
        //System.out.print("Inside removeOutport hook 1");
        System.out.print("Inport removed: "+port+"      component: "+modelName);
      }

     
    
}