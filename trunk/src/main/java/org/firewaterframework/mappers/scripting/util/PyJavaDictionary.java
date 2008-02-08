package org.firewaterframework.mappers.scripting.util;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyList;
import org.python.core.PyTuple;

/**
 * Exposes an unmodifiable java.util.Map as a public field
 * directly accessible from Jython.  The map reflects
 * changes made through the PyDictionary methods.
 *
 * @see java.util.Map
 * @author clark
 */
public class PyJavaDictionary extends PyDictionary {

    /** The underlying dictionary PyDictionary exposed as an
     * unmodifyable Map.
     */
    public final Map map;

    public PyJavaDictionary(){
        this( new HashMap() );
    }

    /** Initializes to the values contained in <code>aMap</code>.
     */
    public PyJavaDictionary(Map aMap) {

        Map tmpMap = new HashMap();
        Iterator it = aMap.entrySet().iterator();
        while(it.hasNext()) {
          Map.Entry e = (Map.Entry)it.next();
          tmpMap.put(Py.java2py(e.getKey()),
            Py.java2py(e.getValue()));
        }

        table = new Hashtable(tmpMap);
        map = Collections.unmodifiableMap(table);
    }

    /** Initializes to the values contained in <code>dict</code>.
     * Use this to construct from a native jython dictionary.
     */
    public PyJavaDictionary(PyDictionary dict) {

        PyList list = dict.items();
        table = new Hashtable();

        for(int i = list.__len__(); i-- > 0; ) {
            PyTuple tup = (PyTuple)list.__getitem__(i);
            table.put(tup.__getitem__(0), tup.__getitem__(1));
        }
        map = Collections.unmodifiableMap(table);
    }
}