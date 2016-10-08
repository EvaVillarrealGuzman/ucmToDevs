/*     
 *    
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */
/*
/*  ZFunction is same as relation in ZContainers
/*  can't define relation and Function due to Java restriction
*/

package GenCol;


import java.util.*;


public class ZFunction  extends Function implements ZFunctionInterface{


public ZFunction(){
super();
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
public Object assoc(Object key){
return get(key);
}

@Override
public boolean keyIsIn(Object key){
return containsKey(key);
}

@Override
public boolean valueIsIn(Object value){
return containsValue(value);
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
public synchronized void Remove(Object key){
remove(key);
}

@Override
public synchronized void replace(Object key, Object value){
put(key,value);
}

}