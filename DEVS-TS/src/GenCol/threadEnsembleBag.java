/*     
 *    
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */
package GenCol;

import java.util.*;

public class threadEnsembleBag extends ensembleBag implements ensembleInterface{

private threadEnsembleWrapper.ensemble el;

public threadEnsembleBag(){
el = threadEnsembleWrapper.make(this);
}

public threadEnsembleBag(Collection c){
addAll(c);
el = threadEnsembleWrapper.make(this);
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


