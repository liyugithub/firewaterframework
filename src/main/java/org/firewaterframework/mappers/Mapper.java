package org.firewaterframework.mappers;

import org.firewaterframework.mappers.validation.MapDataBinder;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Response;
import org.springframework.validation.DataBinder;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 3, 2007
 * Time: 11:44:08 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Mapper
{
    Map<String, PropertyEditor> fields;

    public Mapper()
    {
    }
    
    public abstract Response handle( Request request );

    public void setFields( Map<String, PropertyEditor> fields )
    {
        this.fields = fields;
    }

    public DataBinder getDataBinder()
    {
        DataBinder dataBinder = new MapDataBinder( new HashMap<String,Object>() );
        if( fields != null )
        {
            for( Map.Entry<String,PropertyEditor> entry: fields.entrySet() )
            {
                dataBinder.registerCustomEditor( String.class, entry.getKey(), entry.getValue() );
            }
        }
        return dataBinder;
    }
}
