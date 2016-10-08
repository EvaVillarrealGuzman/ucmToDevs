/*
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 

package model.modeling;

import GenCol.*;


import model.simulation.*;

public class activity extends Thread implements ActivityInterface {

protected double processingTime;

protected CoupledSimulatorInterface sim;

public activity(String name){
 super(name);
 processingTime = 10;

 }

public activity(String name, double pt){
this(name);
processingTime = pt;
}

@Override
public void setSimulator(CoupledSimulatorInterface sim){
this.sim = sim;
}

public void returnTheResult(entity myresult) {
System.out.println("return result in Activity");
 sim.returnResultFromActivity(myresult);
}

@Override
public double getProcessingTime() {
    return processingTime;
}
/*
public void run(){
   try {
 sleep((long)getProcessingTime()*1000);
} catch (InterruptedException e) {return;}
returnTheResult();
}
*/
@Override
public entity computeResult(){
return  new entity(getName() + " --activity result");
}

@Override
public void kill(){
interrupt();
}


}