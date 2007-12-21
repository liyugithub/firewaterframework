package org.firewaterframework.mappers.validation;

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

    public MapPropertyEditor copy()
    {
        MapPropertyEditor newCopy = (MapPropertyEditor)BeanUtils.instantiateClass( this.getClass() );
        return newCopy;
    }

    @Override
    public String toString()
    {
        return this.getClass().getName();
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
