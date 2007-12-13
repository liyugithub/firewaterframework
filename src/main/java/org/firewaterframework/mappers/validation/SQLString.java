package org.firewaterframework.mappers.validation;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 13, 2007
 * Time: 8:30:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class SQLString extends MapPropertyEditor
{
    public void setAsText(String text) throws IllegalArgumentException
    {
        this.setValue( '\'' + text + '\'' );
    }
}
