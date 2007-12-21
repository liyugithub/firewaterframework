package org.firewaterframework.rest;

/**
 * This class corresponds to the HTTP METHOD header argument in that protocol.  The class provides a number
 * of static properties to define all of the METHODs used by Firewater.
 * @author Tim Spurway
 */
public class Method
{
    protected String method;

    public static Method GET = new Method( "GET" );
    public static Method PUT = new Method( "PUT" );
    public static Method DELETE = new Method( "DELETE" );
    public static Method POST = new Method( "POST" );
    public static Method HEAD = new Method( "HEAD" );
    public static Method OPTIONS = new Method( "OPTIONS" );

    public static Method getMethod( String method )
    {
        if( method.equalsIgnoreCase( "get") ) return GET;
        if( method.equalsIgnoreCase( "put") ) return PUT;
        if( method.equalsIgnoreCase( "post") ) return POST;
        if( method.equalsIgnoreCase( "delete") ) return DELETE;
        if( method.equalsIgnoreCase( "head") ) return HEAD;
        if( method.equalsIgnoreCase( "options") ) return OPTIONS;
        return new Method( method );
    }

    protected Method( String method )
    {
        this.method = method;
    }

    @Override
    public boolean equals( Object other )
    {
        return this == other || (other instanceof Method && ((Method) other).method.equalsIgnoreCase(method));
    }

    @Override
    public int hashCode()
    {
        return method.hashCode();
    }

    public String getMethod() {
        return method;
    }
}
