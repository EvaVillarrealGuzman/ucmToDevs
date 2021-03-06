/*   
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 

package model.modeling;


import GenCol.*;


import model.simulation.*;




public interface coupledDevs extends EntityInterface{

public void add(IODevs d);
public void addCoupling(IODevs src, String p1, IODevs dest, String p2);

public IODevs withName(String nm);
public ComponentsInterface getComponents();
public couprel getCouprel();
public void setCoordinator(coordinator coordinator);
public coordinator getCoordinator();
}



