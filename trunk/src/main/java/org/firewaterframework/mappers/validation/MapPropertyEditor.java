package org.firewaterframework.mappers.validation;

import org.springframework.beans.BeanUtils;

import java.beans.PropertyEditorSupport;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 13, 2007
 * Time: 8:37:43 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class MapPropertyEditor extends PropertyEditorSupport
{
    public MapPropertyEditor copy()
    {
        MapPropertyEditor newCopy = (MapPropertyEditor)BeanUtils.instantiateClass( this.getClass() );
        return newCopy;
    }
}
