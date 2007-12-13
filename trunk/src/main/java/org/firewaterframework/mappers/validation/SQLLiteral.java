package org.firewaterframework.mappers.validation;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 12, 2007
 * Time: 11:43:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLLiteral extends MapPropertyEditor
{
    public void setAsText(String text) throws IllegalArgumentException
    {
        if( text.matches( "[0-9][0-9]*" ))
        {
            this.setValue( text );
        }
        else
        {
            this.setValue( '\'' + text + '\'' );
        }
    }
}
