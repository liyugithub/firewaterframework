package org.firewaterframework.mappers.validation;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 13, 2007
 * Time: 8:32:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class SQLNumber extends MapPropertyEditor
{
    public void setAsText(String text) throws IllegalArgumentException
    {
        if( text.matches( "[0-9][0-9]*" ))
        {
            this.setValue( text );
        }
        else
        {
            throw new IllegalArgumentException( "String: " + text + " is not a number." );
        }
    }
}
