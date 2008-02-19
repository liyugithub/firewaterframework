package org.firewaterframework.spring.beans;

import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.firewaterframework.mappers.RouteMapper;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Feb 18, 2008
 * Time: 2:18:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class DispatcherBDParser extends AbstractBeanDefinitionParser
{
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext)
    {
        BeanDefinitionBuilder dispatcher = BeanDefinitionBuilder.rootBeanDefinition(RouteMapper.class);
        List<Element> childElements = DomUtils.getChildElementsByTagName(element, "route");

        if (childElements != null && childElements.size() > 0) {
            ManagedMap urlMap = new ManagedMap();
            for( Element child: childElements )
            {
                String key = child.getAttribute( "pattern" );
                String mapperRefID = child.getAttribute( "mapper" );
                RuntimeBeanReference ref = new RuntimeBeanReference( mapperRefID );
                urlMap.put( key, ref );
            }
            dispatcher.addPropertyValue( "urlMap", urlMap );
        }
        return dispatcher.getBeanDefinition();
    }
}
