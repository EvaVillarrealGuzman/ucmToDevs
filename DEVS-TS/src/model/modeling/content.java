/*  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 

package model.modeling;


import GenCol.*;






public class content extends entity implements ContentInterface,EntityInterface{
private port port;
private entity value;

public content(port p, entity value){
port = p;
this.value =  value;
}

public content(String portNm, entity value){
this(new port(portNm),value);
}

@Override
public PortInterface getPort(){return port;}

@Override
public String getPortName(){return port.getName();}

@Override
public Object getValue (){return value;}

@Override
public String toString(){
return "port: " + port.getName() + " value: "+ value.getName();
}

@Override
public void print(){
System.out.println(toString());
}

@Override
public boolean equals(Object o)
{
    if (o instanceof content) {
        content content = (content)o;
        return port.equals(content.port) && value.equals(content.value);
    }

    return false;
}

@Override
public boolean onPort(PortInterface port){
return port.equals(getPort());
}


public Object valueOnPort(String portName){
if (port.eq(portName))
return getValue();
else return null;
}
}

