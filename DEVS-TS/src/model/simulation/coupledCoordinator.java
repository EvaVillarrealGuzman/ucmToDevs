/*
 * Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 

package model.simulation;

import java.util.*;

import GenCol.*;


import model.modeling.*;
import util.*;


public class coupledCoordinator extends coordinator implements CoupledCoordinatorInterface{

protected CoupledCoordinatorInterface myParent;
protected CoordinatorInterface myRootParent;

public coupledCoordinator(Coupled c){
super(c);
}

public coupledCoordinator(Coupled c, boolean setSimulators){
super(c, setSimulators, null);
}

@Override
public void addSimulator(IOBasicDevs comp){
coupledSimulator s = new coupledSimulator(comp);
s.setParent(this);      // set the parent
simulatorCreated(s, comp);
// later will download modelToSim to its children and then will be updated by its parents
// so after initialization, modelToSim store the brother models and simulators
// internalModelTosim store its children models and simulators
}

@Override
public void addCoordinator(Coupled comp){
coupledCoordinator s = new coupledCoordinator(comp);
s.setParent(this);       // set the parent
simulatorCreated(s, comp);
// later will download modelToSim to its children and then will be updated by its parents
// so after initialization, modelToSim store the brother models and simulators
// internalModelTosim store its children models and simulators
}


@Override
public void setParent( CoupledCoordinatorInterface p){
myParent = p;
}

@Override
public CoupledCoordinatorInterface getParent(){
return myParent;
}

@Override
public void setRootParent( CoordinatorInterface p){
myRootParent = p;
}

@Override
public CoordinatorInterface getRootParent(){
return myRootParent;
}


@Override
public void addPair(Pair cs,Pair cd) {
coupInfo.add(cs,cd);
}

@Override
public void removePair(Pair cs,Pair cd) {
	coupInfo.remove(cs,cd);
}

@Override
public void showCoupling(){
super.showCoupling();
coupInfo.print();
}

@Override
public void setModToSim(Function mts){
modelToSim = mts;
}

@Override
public Relation convertInput(MessageInterface x) {
  Relation r = new Relation();
  message  msg = new message();
  if(x.isEmpty()) return r;
  ContentIteratorInterface cit = ((message)x).mIterator();
  while (cit.hasNext()){
     content co = (content)cit.next();
     HashSet s = extCoupInfo.translate(myCoupled.getName(), co.getPort().getName());
     Iterator it = s.iterator();
     while(it.hasNext()){
        Pair cp = (Pair) it.next();
        Object ds = cp.getKey();
        String por = (String)cp.getValue();
        Object tempval = co.getValue();
        content tempco = new content(por,(entity)tempval);
        r.put(ds,tempco);

        convertInputHook1(co, cp, tempco);
     }
  }
  return r;
}

public Relation convertMsg(MessageInterface x) {
  Relation r = new Relation();
  message  msg = new message();
  if(x.isEmpty()) return r;
  ContentIteratorInterface cit = ((message)x).mIterator();
  while (cit.hasNext()){
     content co = (content)cit.next();
     HashSet s = coupInfo.translate(myCoupled.getName(), co.getPort().getName());
     Iterator it = s.iterator();
     while(it.hasNext()){
        Pair cp = (Pair) it.next();
        Object ds = cp.getKey();
        String por = (String)cp.getValue();
        Object tempval = co.getValue();
        content tempco = new content(por,(entity)tempval);
        r.put(ds,tempco);

        convertMsgHook1(co, cp, tempco);
     }
  }
  return r;
}

@Override
public void sendMessages() {    //extend so they send message to its parent also
  MessageInterface o = getOutput();
  if( o!= null && !o.isEmpty()) {
    Relation r = convertMsg(getOutput());//assume computeInputOutput done first
    Iterator rit = r.iterator();
    while (rit.hasNext()){
       Pair p = (Pair)rit.next();
       content co = (content)p.getValue();
       Object ds = p.getKey();
       if(modelToSim.get(ds) instanceof CoupledSimulatorInterface){
           CoupledSimulatorInterface sim = (CoupledSimulatorInterface)modelToSim.get(ds);
           sim.putMessages(co);
       }
       else if(modelToSim.get(ds) instanceof CoupledCoordinatorInterface){
           CoupledCoordinatorInterface sim = (CoupledCoordinatorInterface)modelToSim.get(ds);
           sim.putMessages(co);
       }
       else{            // this is an internal output coupling
           CoupledCoordinatorInterface cci = getParent();
           CoordinatorInterface ci = getRootParent();
           if(cci != null) myParent.putMyMessages(co);
           else if(ci != null)  myRootParent.putMyMessages(co);
       }
    }
  }
}


@Override
public void putMessages(ContentInterface c){
input.add(c);
}

@Override
public void sendDownMessages() {
  if(!input.isEmpty()){
    Relation r = convertInput(input);
    Iterator rit = r.iterator();
    while (rit.hasNext()){
       Pair p = (Pair)rit.next();
       Object ds = p.getKey();
       content co = (content)p.getValue();
       if(internalModelTosim.get(ds) instanceof CoupledSimulatorInterface){
           CoupledSimulatorInterface sim = (CoupledSimulatorInterface)internalModelTosim.get(ds);
           sim.putMessages(co);
       }
       else if(internalModelTosim.get(ds) instanceof CoupledCoordinatorInterface){
           CoupledCoordinatorInterface sim = (CoupledCoordinatorInterface)internalModelTosim.get(ds);
           sim.putMessages(co);
       }
    }
  }
}

@Override
public void DeltFunc(Double d){
DeltFunc(d.doubleValue());
}


@Override
public void DeltFunc(double t) {
   wrapDeltFunc(t);
}


@Override
public void wrapDeltFunc(double time) {
sendDownMessages();
Class [] classes  = {ensembleBag.getTheClass("java.lang.Double")};
Object [] args  = {new Double(time)};
simulators.tellAll("DeltFunc",classes,args);
input = new message();
output = new message();
}


@Override
public void  simulate(int num_iter){
  int i=1;
  tN = nextTN();
  while( (tN < DevsInterface.INFINITY) && (i<=num_iter) ) {
    Logging.log("ITERATION " + i + " ,time: " + tN, Logging.full);
    computeInputOutput(tN);
    showOutput();
    DeltFunc(tN);
    tL = tN;
    tN = nextTN();
    showModelState();
    i++;
  }
  System.out.println("Terminated Normally at ITERATION " + i + " ,time: " + tN);
}

    protected void convertMsgHook1(content oldContent, Pair coupling, content newContent) {}

    /**
     * See parent method.
     */
    @Override
	public List getCouplingsToSourcePort(String portName)
    {
        return AtomicSimulatorUtil.getCouplingsToSourcePort(portName,
            myCoupled.getName(), coupInfo, extCoupInfo, modelToSim,
            internalModelTosim, (coordinator)myRootParent);
    }
}

