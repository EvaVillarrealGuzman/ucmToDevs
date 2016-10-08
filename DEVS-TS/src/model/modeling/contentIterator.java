/* Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */ 

package model.modeling;

import java.util.*;




class contentIterator implements ContentIteratorInterface{
private Iterator it;
public contentIterator(MessageInterface m){it = m.iterator();}
@Override
public boolean hasNext(){return it.hasNext();}
@Override
public ContentInterface next() {return (ContentInterface)it.next();}

}