/*     
 *    
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */
/*
/*  ZRelation is same as relation in ZContainers
/*  can't define relation and Relation due to Java restriction
*/

package GenCol;


import java.util.*;


public class ZRelation  extends Relation implements ZRelationInterface{
String name;

public ZRelation(){
super();
}

public ZRelation(String name){
super();
this.name = name;
}

@Override
public String getName(){
return name;
}

@Override
public ExternalRepresentation getExtRep(){
return new ExternalRepresentation.ByteArray();
}

@Override
public boolean empty(){
return isEmpty();
}

@Override
public int getLength(){
return size();
}

@Override
public Set assocAll(Object key){
return getSet(key);
}


@Override
public Object assoc(Object key){
return get(key);
}


@Override
public boolean isIn(Object key, Object value){
return contains(key, value);
}

@Override
public Set domainObjects(){
return keySet();
}

@Override
public Set rangeObjects(){
return valueSet();
}


@Override
public synchronized void add(Object key, Object value){
put(key,value);
}

@Override
public synchronized void Remove(Object key, Object value){
remove(key,value);
}

}
