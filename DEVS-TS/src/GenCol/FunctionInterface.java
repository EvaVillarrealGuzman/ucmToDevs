/*     
 *    
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */
/*
/*  iteration is through Pairs rather than Entries
*/

package GenCol;


import java.util.*;




//inherit contains(Object), containsKey(Object), containsValue(Object)


interface FunctionInterface extends Map{

@Override
public boolean isEmpty();
@Override
public int size();
@Override
public boolean containsKey(Object key);
@Override
public boolean containsValue(Object value);
public boolean contains(Object key, Object value);
@Override
public Object get(Object key);
@Override
public Object put(Object key, Object value); //acts as replace
@Override
public Object remove(Object key);
@Override
public Set keySet();
public Set valueSet();
public Iterator iterator();
@Override
public String toString();
@Override
public int hashCode();
}

