/* 
 * Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 

package model.simulation.realTime;

import GenCol.*;
import model.modeling.*;
import model.simulation.*;
import util.*;

public class atomicRTSimulator extends atomicSimulator
            implements RTSimulatorInterface {

protected  Thread myThread;
protected double startTime;
protected int numIter;
protected injectThread injThread;
protected long timeToSleep;
public boolean pauseFlag = false;

public atomicRTSimulator(IOBasicDevs atomic){
 super(atomic);
 myThread = new Thread(this);
 numIter = 0;
}

@Override
public void initialize(){
 myModel.initialize();
 startTime = timeInMillis();
 tL = startTime;
 tN = tL + myModel.ta()*1000;
 Logging.log("Relative INITIALIZATION, time: " + 0 +
    ", next event at: "+(tN - startTime), Logging.full);
 myModel.showState();
 }

@Override
public void setTN(){
   tN = timeInMillis()+myModel.ta()*1000;
}

@Override
public double getTN(){ return tN; }

@Override
public long timeInSecs() {
    return (timeInMillis()/1000);
}
@Override
public long timeInMillis() {
    return System.currentTimeMillis();
}

@Override
public void sendMessages(){}

public  synchronized void myThreadpause() {
    pauseFlag = true;
}

public void myThreadrestart() {
    pauseFlag = false;
}

@Override
public void  simulate(int num){
  int i=1;
  numIter = num;
  myThread.start();
}

@Override
public void stopSimulate(){
numIter = 0;
myThread.interrupt();
}

@Override
public void run(){
     tL = timeInMillis();
     tN = tL + myModel.ta()*1000;
     int iter = 0;

     while( (tN < DevsInterface.INFINITY) && (iter < numIter) ) {
        System.out.println("ITERATION " + iter  + " ,relative time: " + (tN-startTime));
        while(timeInMillis() < getTN() - 10){
            timeToSleep = (long)(getTN() - timeInMillis());
            if(timeToSleep >= 0) {
                try {
                  Thread.sleep(timeToSleep);
                } catch (Exception e) { continue; }
            }
        }
        computeInputOutput(this.getTN());
        showOutput();
        sendMessages();
        wrapDeltfunc(this.getTN());
        showModelState();
        while(pauseFlag) { }    // busy waiting

        tL = timeInMillis();
        tN = tL + myModel.ta()*1000;
        iter ++;
      } // end of while
      System.out.println("Terminated Normally at ITERATION " + iter + ",relative time: " + (tN -startTime));
}

@Override
public void  wrapDeltfunc(double t,MessageInterface x){
 if(x == null){
    System.out.println("ERROR RECEIVED NULL INPUT  " + myModel.toString());
    return;
  }
  if (x.isEmpty() && !equalTN(t)) {
    return;
  }
  else if((!x.isEmpty()) && equalTN(t)) {
    double e = t - tL;
    myModel.deltcon(e/1000,x);
  }
  else if(equalTN(t)) {
     myModel.deltint();
  }
  else if(!x.isEmpty()) {
    double e = t - tL;
    myModel.deltext(e/1000,x);
  }

  tL = t;
  tN = tL + myModel.ta()*1000;
}

@Override
public MessageInterface makeMessage(){return new message();}

@Override
public void simInject(double e,String portName,entity value){
simInject(e,new port(portName),value);
}

@Override
public void simInject(double e,PortInterface p,EntityInterface value){
MessageInterface m = makeMessage();
m.add(myModel.makeContent(p,value));
simInject(e,m);
}

@Override
public void simInject(double e,MessageInterface m){
injThread = new injectThread(this,e,m);
}

}

class injectThread extends Thread{
protected atomicRTSimulator sim;
protected double e;
protected MessageInterface m;
public injectThread(atomicRTSimulator sim,double e,MessageInterface m){
this.sim = sim;
this.e = e;
this.m = m;
start();
}

@Override
public void run(){
try {
 sleep((long)e*1000);
} catch (Exception e) {}
sim.myThreadpause();
sim.wrapDeltfunc(sim.timeInMillis(),m);
System.out.println("Time: " + sim.timeInMillis() +" ,input injected: " );
m.print();
sim.getModel().showState();
sim.myThreadrestart();
}

}

