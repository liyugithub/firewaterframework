package org.firewaterframework.spring.beans;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Jan 28, 2009
 * Time: 3:19:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class RefBDParser extends AbstractBeanDefinitionParser
{
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext)
    {
        String beanName = element.getAttribute( "bean" );
        return (AbstractBeanDefinition)parserContext.getRegistry().getBeanDefinition( beanName );
    }
}
