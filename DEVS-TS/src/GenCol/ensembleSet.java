/*     
 *    
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */
package GenCol;

import java.util.*;

public class ensembleSet extends HashSet implements ensembleInterface{

private ensembleWrapper.ensemble el;

public ensembleSet(){
el = ensembleWrapper.make(this);
}

public ensembleSet(Collection c){
addAll(c);
el = ensembleWrapper.make(this);
}

@Override
public void tellAll(String MethodNm,Class[] classes,Object[] args){
el.tellAll(MethodNm,classes,args);
}

@Override
public void tellAll(String MethodNm){
Class [] classes = {};
Object [] args  = {};
el.tellAll(MethodNm,classes,args);
}


@Override
public void AskAll(ensembleInterface result,String MethodNm,Class[] classes,Object[] args){
el.AskAll(result,MethodNm,classes,args);
}

@Override
public void which(ensembleInterface result,String MethodNm,Class[] classes,Object[] args){
 //MethodNm method must return this if condition is true and null otherwise
el.AskAll(result,MethodNm,classes,args);
}

@Override
public Object whichOne(String MethodNm,Class[] classes,Object[] args){
return el.whichOne(MethodNm,classes,args);
}

@Override
public  ensembleInterface copy(ensembleInterface c){
return el.copy(c);                             
}

@Override
public  void wrapAll(ensembleInterface Result,Class cl){
el.wrapAll(Result,cl);
}

@Override
public boolean none(String MethodNm,Class[] classes,Object[] args){
return el.none(MethodNm,classes,args);
}
@Override
public boolean all(String MethodNm,Class[] classes,Object[] args){
return el.all(MethodNm,classes,args);
}

@Override
public void print(){
el.print();
}

}


