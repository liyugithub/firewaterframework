package org.firewaterframework.mappers.validation;

import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.validation.DataBinder;

import java.util.Map;

/**
 * This class extends Spring's {@link DataBinder} to allow for binding onto Maps, instead of just POJOs.
 * @author tspurway
 */
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
