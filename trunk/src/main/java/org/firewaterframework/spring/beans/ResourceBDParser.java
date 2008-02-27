package org.firewaterframework.spring.beans;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.firewaterframework.mappers.RouteMapper;
import org.firewaterframework.mappers.MethodMapper;
import org.firewaterframework.mappers.jdbc.ResourceDescriptor;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Feb 26, 2008
 * Time: 9:32:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceBDParser extends AbstractMapperBDParser
{
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext)
    {
        BeanDefinitionBuilder resource = BeanDefinitionBuilder.rootBeanDefinition( ResourceDescriptor.class );

        String value = element.getAttribute( "pivot-attribute" );
        resource.addPropertyValue( "pivotAttribute", value );

        value = element.getAttribute( "url-prefix" );
        if( value != null && value.length() > 0 )
        {
            resource.addPropertyValue( "urlPrefix", value );
        }

        value = element.getAttribute( "sub-resource" );
        if( value != null && value.length() > 0 )
        {
            resource.addPropertyValue( "subResource", value );
        }

        value = element.getAttribute( "tagname" );
        if( value != null && value.length() > 0 )
        {
            resource.addPropertyValue( "tagname", value );
        }

        value = element.getAttribute( "attributes" );
        if( value != null && value.length() > 0 )
        {
            resource.addPropertyValue( "attributesString", value );
        }

        return resource.getBeanDefinition();
    }
}
