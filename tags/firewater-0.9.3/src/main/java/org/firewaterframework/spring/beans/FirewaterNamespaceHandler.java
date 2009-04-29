package org.firewaterframework.spring.beans;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Feb 18, 2008
 * Time: 2:12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class FirewaterNamespaceHandler extends NamespaceHandlerSupport
{
    public void init()
    {
        registerBeanDefinitionParser( "dispatcher", new DispatcherBDParser() );
        registerBeanDefinitionParser( "method-mapper", new MethodMapperBDParser() );
        registerBeanDefinitionParser( "query-mapper", new QueryMapperBDParser() );
        registerBeanDefinitionParser( "update-mapper", new UpdateMapperBDParser() );
        registerBeanDefinitionParser( "resource", new ResourceBDParser() );
        registerBeanDefinitionParser( "ref", new RefBDParser() );
    }
}
