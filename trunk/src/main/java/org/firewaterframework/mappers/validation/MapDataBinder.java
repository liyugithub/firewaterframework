package org.firewaterframework.mappers.validation;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.validation.DataBinder;

import java.util.Map;


public class MapDataBinder extends DataBinder
{
    protected MapPropertyBindingResult myBindingResult;

    public MapDataBinder( Map target )
    {
        super( target );
    }

    @Override
    protected AbstractPropertyBindingResult getInternalBindingResult() {
        if (this.myBindingResult == null)
        {
            myBindingResult = new MapPropertyBindingResult( (Map)getTarget(), getObjectName() );
        }
        return this.myBindingResult;
    }

}
