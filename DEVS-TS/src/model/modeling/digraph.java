/* 
 * Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 

package model.modeling;

import GenCol.*;


import model.simulation.*;




public class digraph extends devs implements Coupled{
protected coordinator coordinator;
protected ComponentsInterface components;
protected couprel cp;

public digraph(String nm){
super(nm);
components = new Components();
cp = new couprel();
}

@Override
public void add(IODevs iod){
 components.add(iod);
 ((devs)iod).setParent(this);
}

public void remove(IODevs d){
	components.remove(d);
}

@Override
public void addCoupling(IODevs src, String p1, IODevs dest, String p2){
cp.add((entity)src,new port(p1),(entity)dest,new port(p2));
}

@Override
public IODevs withName(String nm){
Class [] classes  = {ensembleBag.getTheClass("java.lang.String")};
Object [] args  = {nm};
return (IODevs)components.whichOne("equalName",classes,args);
}

@Override
public ComponentsInterface getComponents(){
return components;
}

@Override
public couprel getCouprel(){
return cp;
}

@Override
public String toString(){
String s = "";
componentIterator cit = getComponents().cIterator();
while (cit.hasNext()){
IOBasicDevs iod = cit.nextComponent();
s += " " + iod.toString();
}
return s;
}

@Override
public void showState(){
components.tellAll("showState");
}


@Override
public void initialize(){
components.tellAll("initialize");
}

// added for DSDEVS

public void addPair(Pair cs, Pair cd){
	cp.add(cs,cd);
	}

public void removePair(Pair cs, Pair cd){
	cp.remove(cs,cd);
	}
public void addInport(String modelName, String port){
    //s.s("Inside digraph addInport");
    digraph P = (digraph)getParent();
    if (P != null)
      P.withName(modelName).addInport(port);
       else
         System.out.print("parent is not defined");   
       addInportHook(modelName,port);
}

public void addOutport(String modelName, String port){
	  //s.s("Inside digraph addOutport");
	  digraph P = (digraph)getParent();
	  if (P != null)
	    P.withName(modelName).addOutport(port);
	  else
	    System.out.print("parent is not defined");
	  addOutportHook(modelName,port);
	}

public void addInportHook(String modelName, String port){
	  //s.s("Inside digraph addInport hook 1");
	  System.out.print("Inport added: "+port+"      component: "+modelName);
	}

public void addOutportHook(String modelName, String port){
	  //s.s("Inside digraph addInport hook 1");
	System.out.print("Inport added: "+port+"      component: "+modelName);
	}

public boolean checkNameUniqueness(String modelName){
		  ComponentsInterface cpi = getComponents();
		  componentIterator i = cpi.cIterator();
		  String nm;
		  while(i.hasNext()){
		    nm = (i.nextComponent()).getName();
		    if(nm.compareTo(modelName)==0) return false;
		  }
		  return true;
		}

//	
@Override
public void setSimulator(CoupledSimulatorInterface sim){}
@Override
public ActivityInterface getActivity(){return new activity("name");}
@Override
public void deltext(double e,MessageInterface x){}
@Override
public void deltcon(double e,MessageInterface x){}
@Override
public void deltint(){}
@Override
public MessageInterface Out(){return new message();}
@Override
public double ta(){return 0;}

 public componentIterator iterator(){
 return getComponents().cIterator();
 }

    @Override
	public void setCoordinator(coordinator coordinator_) {coordinator = coordinator_;}
    @Override
	public coordinator getCoordinator() {return coordinator;}
}



