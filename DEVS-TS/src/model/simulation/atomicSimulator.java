/*
 * Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 

package model.simulation;
import GenCol.*;


import model.modeling.*;
import util.*;

public class atomicSimulator
             implements AtomicSimulatorInterface {//for usual devs

protected double tL,tN;
public MessageInterface input,output;
protected IOBasicDevs myModel;

public atomicSimulator(){}

public atomicSimulator(IOBasicDevs atomic){
myModel = atomic;
input = new message();
output = new message();
}

@Override
public double nextTN(){return tN;}

public boolean  equalTN(double t){return t == tN;}

@Override
public double getTN(){
return tN;
}

@Override
public double getTL(){
return tL;
}

@Override
public synchronized MessageInterface  getOutput(){return output;}
//
public synchronized MessageInterface  getInput(){return input;}


@Override
public Double nextTNDouble(){
return new Double(nextTN());
}

@Override
public synchronized void showModelState(){
myModel.showState();
}

@Override
public  synchronized void initialize(){ //for non real time usage, assume the time begins at 0
 myModel.initialize();
 tL = 0.0;
 tN = myModel.ta();
 Logging.log("INITIALIZATION, time: " + tL +", next event at: "+tN,
    Logging.full);
 myModel.showState();
 }

@Override
public synchronized  void  initialize(Double d){
initialize(d.doubleValue());
}

@Override
public  synchronized void initialize(double currentTime){     // for real time usage
 myModel.initialize();
 tL = currentTime;
 tN = tL + myModel.ta();
 Logging.log("INITIALIZATION, time: " + tL +", next event at: "+tN,
    Logging.full);
 myModel.showState();
 }

@Override
public synchronized void DeltFunc(Double d){
DeltFunc(d.doubleValue());
}
public  synchronized void DeltFunc(double t){
  wrapDeltfunc(t,new message());
}

@Override
public  synchronized void  wrapDeltfunc(double t){
 wrapDeltfunc(t,input); //changed to work with activity
 input = new message();
}

@Override
public  synchronized void  wrapDeltfunc(double t,MessageInterface x){
 if(x == null){
    System.out.println("ERROR RECEIVED NULL INPUT  " + myModel.toString());
    return;
  }
  if (x.isEmpty() && !equalTN(t)) {
    return;
  }
  else if((!x.isEmpty()) && equalTN(t)) {
    double e = t - tL;
    myModel.deltcon(e,x);
  }
  else if(equalTN(t)) {
    myModel.deltint();
  }
  else if(!x.isEmpty()) {
    double e = t - tL;
    myModel.deltext(e,x);
  }
  wrapDeltfuncHook2();
  tL = t;
  tN = tL + myModel.ta();
}

@Override
public  void computeInputOutput(Double d){
computeInputOutput(d.doubleValue());
}

@Override
public  void computeInputOutput(double t){
      if(equalTN(t)) {
          output = myModel.Out();
      }
      else{
        output = new message();//bpz
      }

      computeInputOutputHook1();
}

@Override
public void  simulate(int numIter)
{
  int i=1;
  tN = nextTN();
  while( (tN < DevsInterface.INFINITY) && (i<=numIter) ) {
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

public void simulate(Integer i){
simulate(i.intValue());
}

@Override
public synchronized void  showOutput(){
 if (output == null) return;
// System.out.println("Ouput -------------->");
 if(!output.isEmpty())
        output.print();
}

@Override
public MessageInterface makeMessage(){return new message();}

public void simInject(double e,PortInterface p,EntityInterface value){
MessageInterface m = makeMessage();
m.add(myModel.makeContent(p,value));
simInject(e,m);
}

@Override
public void simInject(double e,String portName,entity value){
                                            //  for use in usual devs
simInject(e,new port(portName),value);
}

@Override
public void simInject(double e,MessageInterface m){
double t = tL+e;
if (e <= myModel.ta()){
simInjectHook1(e);
wrapDeltfunc(t,m);
System.out.println("Time: " + t +" ,input injected: " );
m.print();
myModel.showState();
simInjectHook2(t);
}
else System.out.println("Time: " + tL+ " ,ERROR input rejected : elapsed time " + e +" is not in bounds.");
}

    /**
     * A hook used by the SimView package.
     */
    protected void wrapDeltfuncHook2() {}

    /**
     * A hook used by the SimView package.
     *
     * @param   waitTime        The amount of simulation time to wait before
     *                          injecting the input.
     */
    protected void simInjectHook1(double e) {}

    /**
     * A hook used by the SimView package.
     *
     * @param   newTime     The new current simulation time.
     */
    protected void simInjectHook2(double newTime) {}

    /**
     * A hook used by the SimView package.
     */
    protected void computeInputOutputHook1() {}

    public IOBasicDevs getModel() {return myModel;}
}
