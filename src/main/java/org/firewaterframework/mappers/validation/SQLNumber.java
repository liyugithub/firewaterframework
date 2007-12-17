package org.firewaterframework.mappers.validation;

/**
 * This PropertyEditor will match only numeric input.  No processing is done on binding.
 * @author Tim Spurway
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
