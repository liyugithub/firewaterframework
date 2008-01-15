package org.firewaterframework.mappers.validation;
/*
    Copyright 2008 John TW Spurway
    Licensed under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with the License.
    You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software distributed under the
    License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
    either express or implied. See the License for the specific language governing permissions
    and limitations under the License.
*/
import org.springframework.beans.*;
import org.springframework.validation.AbstractPropertyBindingResult;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an extension to Spring's {@link org.springframework.validation.BindingResult} to allow for binding into
 * Maps instead of just POJOs.
 *
 * @author Tim Spurway
 */
public class MapPropertyBindingResult extends AbstractPropertyBindingResult
{
    protected Map target;
    protected ConfigurablePropertyAccessor propertyAccessor;

    public MapPropertyBindingResult( Map target, String string )
    {
        super( string );
        this.target = target;
        propertyAccessor = new MapPropertyAccessor( target );
    }
    public ConfigurablePropertyAccessor getPropertyAccessor() {
        return propertyAccessor;
    }

    public Object getTarget() {
        return target;
    }

    public final class MapPropertyAccessor implements ConfigurablePropertyAccessor
    {
        protected Map target;
        protected Map<String,MapPropertyEditor> propertyEditors
                = new HashMap<String,MapPropertyEditor>();

        public MapPropertyAccessor( Map target )
        {
            this.target = target;
        }

        public void setExtractOldValueForEditor(boolean b) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isExtractOldValueForEditor() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isReadableProperty(String s) {
            return true;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public boolean isWritableProperty(String s) {
            return true;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public Class getPropertyType(String s) throws BeansException {
            Object value = target.get( s );
            if( value != null )
            {
                return value.getClass();
            }
            return null;
        }

        public Object getPropertyValue(String s) throws BeansException {
            return target.get( s );
        }

        public void setPropertyValue(String s, Object o) throws BeansException
        {
            MapPropertyEditor pe = propertyEditors.get( s );
            try
            {
                if( pe != null )
                {
                    pe = pe.copy();
                    pe.setAsText( (String)o );
                    target.put( s, pe.getValue() );
                }
                else
                {
                    target.put( s, o );
                }
            }
            catch( IllegalArgumentException ex )
            {
                PropertyChangeEvent pce = new PropertyChangeEvent( target, s, null, o );
                throw new TypeMismatchException(  pce, o.getClass(), ex );
            }
        }

        public void setPropertyValue(PropertyValue propertyValue) throws BeansException
        {
            setPropertyValue( propertyValue.getName(), propertyValue.getValue() );
        }

        public void setPropertyValues(Map map) throws BeansException
        {
            List<PropertyAccessException> exceptions = new ArrayList<PropertyAccessException>();
            Map<String,Object> cmap = (Map<String,Object>)map;
            for( Map.Entry<String,Object> entry: cmap.entrySet() )
            {
                try
                {
                    setPropertyValue(entry.getKey(), entry.getValue() );
                }
                catch( PropertyAccessException e )
                {
                    exceptions.add( e );
                }
            }
            
            if( exceptions.size() > 0 )
            {
                throw new PropertyBatchUpdateException( exceptions.toArray( new PropertyAccessException[0] ) );
            }
        }

        public void setPropertyValues(PropertyValues propertyValues) throws BeansException
        {
            List<PropertyAccessException> exceptions = new ArrayList<PropertyAccessException>();
            for( PropertyValue pv: propertyValues.getPropertyValues() )
            {
                try
                {
                    setPropertyValue( pv );
                }
                catch( PropertyAccessException e )
                {
                    exceptions.add( e );
                }
            }

            if( exceptions.size() > 0 )
            {
                throw new PropertyBatchUpdateException( exceptions.toArray( new PropertyAccessException[0] ) );
            }
        }

        public void setPropertyValues(PropertyValues propertyValues, boolean b) throws BeansException
        {
            setPropertyValues( propertyValues );
        }

        public void setPropertyValues(PropertyValues propertyValues, boolean b, boolean b1) throws BeansException
        {
            setPropertyValues( propertyValues );
        }

        public void registerCustomEditor(Class aClass, PropertyEditor propertyEditor)
        {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void registerCustomEditor(Class aClass, String s, PropertyEditor propertyEditor) {
            propertyEditors.put( s, (MapPropertyEditor)propertyEditor );
        }

        public PropertyEditor findCustomEditor(Class aClass, String s) {
            return propertyEditors.get( s );
        }

        public Map<String, MapPropertyEditor> getPropertyEditors() {
            return propertyEditors;
        }

        public void setPropertyEditors(Map<String, MapPropertyEditor> propertyEditors) {
            this.propertyEditors = propertyEditors;
        }
    }
}
