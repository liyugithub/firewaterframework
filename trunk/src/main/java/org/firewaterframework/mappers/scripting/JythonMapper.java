package org.firewaterframework.mappers.scripting;

import org.firewaterframework.mappers.Mapper;
import org.firewaterframework.mappers.RouteMapper;
import org.firewaterframework.mappers.scripting.util.PyJavaDictionary;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Response;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Feb 3, 2008
 * Time: 10:53:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class JythonMapper extends Mapper
{
    protected String script;
    protected RouteMapper router;
    static
    {
        PythonInterpreter.initialize( null, null, null );
    }

    public Response handle( Request request )
    {
        PythonInterpreter pyterp = new PythonInterpreter( );
        pyterp.set( "_request", request );
        pyterp.set( "_router", router );
        pyterp.set( "_fields", super.bind( request ));
        pyterp.set( "_result", new PyJavaDictionary() );

        PyObject rval = pyterp.eval( script );
        return null; //TODO: integrate jython...
    }
}
