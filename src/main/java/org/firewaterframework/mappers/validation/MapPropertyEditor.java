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
import org.springframework.beans.BeanUtils;

import java.beans.PropertyEditorSupport;

/**
 * This is a PropertyEditor that supports a simple copy mechanism.  This is the base class of Firewater's validation
 * scheme.  It is important because the framework is inherintly susceptible to SQL injection attacks, as it is
 * totally possible have string passed in from the web layer be copied directly into SQL queries.  This class and it's
 * subclasses represent the line of defense against attacks like this, plus giving a mechanism for transforming
 * incoming Request data in a number of ways.
 *
 * @see org.springframework.validation.DataBinder
 * @author Tim Spurway
 */
public abstract class MapPropertyEditor extends PropertyEditorSupport
{
    protected boolean required = false;
    protected boolean nullable = false;

    public MapPropertyEditor copy()
    {
        MapPropertyEditor newCopy = (MapPropertyEditor)BeanUtils.instantiateClass( this.getClass() );
        newCopy.required = required;
        newCopy.nullable = nullable;
        return newCopy;
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }

    protected boolean checkRequired( String value )
    {

        if( nullable && value == null )
        {
            this.setValue( null );
            return false;
        }
        if( required && value == null )
        {
            throw new IllegalArgumentException( "Value required for field" );
        }
        return value != null;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
}
