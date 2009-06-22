package org.firewaterframework.spring.beans;

import org.firewaterframework.mappers.MethodMapper;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Feb 18, 2008
 * Time: 4:30:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethodMapperBDParser extends AbstractMapperBDParser
{
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder methodMapper = BeanDefinitionBuilder.rootBeanDefinition( MethodMapper.class);
        NodeList childElements = element.getChildNodes();

        if ( childElements != null && childElements.getLength() > 0)
        {
            for ( int i = 0; i < childElements.getLength(); ++i )
            {
                if( childElements.item(i) instanceof Element )
                {
                    Element child = (Element)childElements.item(i);
                    String mapperRefID = child.getAttribute("mapper");
                    RuntimeBeanReference ref = new RuntimeBeanReference(mapperRefID);
                    String subMapper = child.getLocalName() + "Mapper";
                    methodMapper.addPropertyValue( subMapper, ref );
                }
            }
        }

        NamedNodeMap attributes = element.getAttributes();
        if( attributes != null )
        {
            for( int i = 0; i < attributes.getLength(); ++i )
            {
                Attr child = (Attr) attributes.item(i);
                if( "cache".equals( child.getLocalName() ))
                {
                    RuntimeBeanReference ref = new RuntimeBeanReference( child.getValue() );
                    methodMapper.addPropertyValue( "cache", ref );
                }
                else if( "cache-groups".equals( child.getLocalName() ))
                {
                    RuntimeBeanReference ref = new RuntimeBeanReference( child.getValue() );
                    methodMapper.addPropertyValue( "cacheGroups", ref );
                }
            }
        }

        return methodMapper.getBeanDefinition();
    }
}
