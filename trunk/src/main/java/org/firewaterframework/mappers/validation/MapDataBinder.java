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

    @Override
    protected void applyPropertyValues( MutablePropertyValues mpvs )
    {
        // process 'default' values
        Map<String,MapPropertyEditor> propertyEditors = ((MapPropertyBindingResult.MapPropertyAccessor)getPropertyAccessor()).getPropertyEditors();
        for( Map.Entry<String,MapPropertyEditor> entry: propertyEditors.entrySet() )
        {
            if( entry.getValue().hasDefaultValue() && mpvs.getPropertyValue( entry.getKey() ) == null )
            {
                mpvs.addPropertyValue( entry.getKey(), entry.getValue().getDefaultValue() );
            }
        }
        super.applyPropertyValues( mpvs );
    }

}
