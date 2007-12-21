package org.firewaterframework.mappers.validation;

/**
 * This PropertyEditor is a catch-all SQL Literal validator.  It will match any string, but if that string is
 * numeric, it will not wrap it in single quotes before output binding, and will wrap all other values in single
 * quotes.
 * @author Tim Spurway
 * 
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
