/*     
 *    
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */


/*
/*  no correspondence in Java collections
/*  iteration is through Pairs rather than Entries
*/

package GenCol;


import java.util.*;


public class Relation extends entity implements RelationInterface{
//  extending Hashtable{ results in loop due to overloading of put

//protected
public Hashtable h;
protected int size;

public Relation(){
h = new Hashtable();
size = 0;
}

@Override
public boolean isEmpty(){
return size == 0; //bpz
}

@Override
public int size(){
return size;
}

@Override
public Set getSet(Object key){
if (h.get(key) == null) return new HashSet();
else return (HashSet)h.get(key);
}

@Override
public  synchronized Object put(Object key, Object value){
Set s = getSet(key);
Iterator it = s.iterator();
Object old = it.hasNext()?it.next():null;
if (s.add(value))size++;
h.remove(key);
h.put(key,s);
return old;
}


@Override
public  synchronized Object remove(Object key, Object value){
Set s = getSet(key);
Iterator it = s.iterator();
Object old = it.hasNext()?it.next():null;
if (s.remove(value))size--;
h.remove(key);
if (s.size() > 0)h.put(key,s);
return old;
}

@Override
public  synchronized void removeAll(Object key){
Set s = getSet(key);
size -= s.size();
h.remove(key);
}

@Override
public Object get(Object key){
Set s = getSet(key);
if (s.isEmpty()) return null;
else{
Iterator it = s.iterator();
return it.next();
}
}

@Override
public boolean contains(Object key, Object value){
return  getSet(key).contains(value);
}
@Override
public Set keySet(){
return h.keySet();
}

@Override
public Set valueSet(){
Iterator it = iterator();
HashSet hs = new HashSet();
while (it.hasNext())
  hs.add(((Pair)it.next()).getValue());
return hs;
}

@Override
public Iterator iterator(){
return new RelationIterator(this);
}

@Override
public String toString(){
Iterator it = iterator();
String s = "";
while (it.hasNext())
  s += ((Pair)it.next()).toString()+"\n";
return s;
}
@Override
public int hashCode(){
Iterator it = iterator();
int sum = 0;
while (it.hasNext())
  sum += ((Pair)it.next()).hashCode();
return sum;
}

@Override
public void print(){
System.out.println(toString());
}

@Override
public synchronized boolean equals(Object o) {
	if (o == this)
	    return true;
Class cl = getClass();
if (!cl.isInstance (o))return false;
	Relation t = (Relation) o;
	if (t.size() != size())
	    return false;

  Set keyset = keySet();
  Set tset = t.keySet();
 if (!keyset.equals(tset))return false;

	Iterator it = keyset.iterator();
  while (it.hasNext()) {
	    Object key =  it.next();
	    Set valueset = getSet(key);
	    Set tvalueset = t.getSet(key);
	  if (!valueset.equals(tvalueset))
        	return false;
	    }
  return true;
}
}


