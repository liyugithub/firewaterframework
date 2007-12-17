package org.firewaterframework.mappers.validation;

/**
 * This PropertyEditor will validate all input, but will unconditionally wrap the output in single quotes.
 * @author Tim Spurway
 */
public class SQLString extends MapPropertyEditor
{
    public void setAsText(String text) throws IllegalArgumentException
    {
        this.setValue( '\'' + text + '\'' );
    }
}
