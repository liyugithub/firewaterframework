package org.firewaterframework.rest;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Nov 28, 2007
 * Time: 10:27:05 AM
 * To change this template use File | Settings | File Templates.
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

    public Method( String method )
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
}
