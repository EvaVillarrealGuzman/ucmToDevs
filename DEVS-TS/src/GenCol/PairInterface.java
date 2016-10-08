/*     
 *    
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */
/*
/* PairInterface is similar to Entry contained within Map
*/

package GenCol;


interface PairInterface {

@Override
public String toString();
@Override
public boolean equals(Object o);
public Object getKey();
public Object getValue();
@Override
public int hashCode();
public int compare(Object m,Object n);     //less than
}



