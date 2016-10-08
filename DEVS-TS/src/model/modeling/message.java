/* 
 * Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 

package model.modeling;


import GenCol.*;






public class message extends ensembleBag implements MessageInterface,EntityInterface{

@Override
public ExternalRepresentation getExtRep(){return new ExternalRepresentation.ByteArray();}
@Override
public ContentIteratorInterface mIterator(){return new contentIterator(this);}

@Override
public String getName(){return "message";}

public content read(int i){ //for downward compatibililty
ContentIteratorInterface cit = mIterator();
for (int j = 0;j <i;j++) cit.next();
return (content)cit.next();
}

@Override
public boolean onPort(PortInterface port, ContentInterface c){
return c.onPort(port);
}

public boolean onPort(String portName, int i){
return onPort(new port(portName),read(i));
}

@Override
public Object getValOnPort(PortInterface port,ContentInterface c){
  if (onPort(port,c)) {
    return c.getValue();
  }
  return null;
}
public entity getValOnPort(String portName,content c){ //for downward compatibililty
  if (onPort(new port(portName),c)) {
    return (entity)c.getValue();
  }
  return null;
}

public entity getValOnPort(String portName, int i){ //for downward compatibililty
    return getValOnPort(portName,read(i));
}
public int getLength(){  //for downward compatibililty
return size();
}


@Override
public String toString(){
String s = "";
ContentIteratorInterface cit = mIterator();
while (cit.hasNext()){
content c = (content)cit.next();
s += " " + c.toString();
}
return s;
}



/* examples of ensembleBag use */

public ensembleBag getPortNames(){
ensembleBag r = new ensembleBag();
Class [] classes = {};
Object [] args  = {};
AskAll(r,"getPortName",classes,args);
return r;
}
@Override
public ensembleBag valuesOnPort(String portName){
ensembleBag r = new ensembleBag();
Class [] classes = {ensembleBag.getTheClass("java.lang.String")};
Object [] args  = {portName};
which(r,"valueOnPort",classes,args);
return r;
}


}

