package org.firewaterframework.spring.beans;

import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.firewaterframework.mappers.RouteMapper;
import org.firewaterframework.mappers.MethodMapper;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Feb 18, 2008
 * Time: 4:30:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethodMapperBDParser extends AbstractBeanDefinitionParser
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
        return methodMapper.getBeanDefinition();
    }
}
