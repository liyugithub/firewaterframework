package org.firewaterframework.spring.beans;

import org.firewaterframework.mappers.jdbc.QueryHolder;
import org.firewaterframework.mappers.jdbc.UpdateMapper;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Feb 19, 2008
 * Time: 12:05:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateMapperBDParser extends AbstractMapperBDParser
{
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext)
    {
        BeanDefinitionBuilder queryMapper = BeanDefinitionBuilder.rootBeanDefinition( UpdateMapper.class );
        NodeList childElements = element.getChildNodes();

        if ( childElements != null && childElements.getLength() > 0)
        {
            for ( int i = 0; i < childElements.getLength(); ++i )
            {
                if( childElements.item(i) instanceof Element )
                {
                    Element child = (Element)childElements.item(i);
                    if( "fields".equals( child.getLocalName() ))
                    {
                        queryMapper.addPropertyValue( "fields", parseFields( child ));
                    }
                    else if( "queries".equals( child.getLocalName() ))
                    {
                        queryMapper.addPropertyValue( "queries", parseQueries( child ));
                    }
                }
            }
        }

        NamedNodeMap attributes = element.getAttributes();
        if( attributes != null )
        {
            for( int i = 0; i < attributes.getLength(); ++i )
            {
                Attr child = (Attr) attributes.item(i);
                if( "data-source".equals( child.getLocalName() ))
                {
                    RuntimeBeanReference ref = new RuntimeBeanReference( child.getValue() );
                    queryMapper.addPropertyValue( "dataSource", ref );
                }
                else if( !"id".equals( child.getLocalName() ))
                {
                    queryMapper.addPropertyValue( xmlStringToBeanName( child.getLocalName()), child.getValue() );
                }
            }
        }

        return queryMapper.getBeanDefinition();
    }

    protected ManagedList parseQueries( Element element )
    {
        ManagedList rval = new ManagedList();
        NodeList childElements = element.getChildNodes();

        if ( childElements != null && childElements.getLength() > 0)
        {
            for ( int i = 0; i < childElements.getLength(); ++i )
            {
                if( childElements.item(i) instanceof Element )
                {
                    Element child = (Element)childElements.item(i);
                    if( "query".equals( child.getLocalName() ))
                    {
                        BeanDefinitionBuilder query = BeanDefinitionBuilder.rootBeanDefinition( QueryHolder.class );
                        query.addPropertyValue( "query", child.getTextContent() );
                        String keyName = child.getAttribute( "keyName" );
                        if( keyName != null )
                        {
                            query.addPropertyValue( "keyName", keyName );
                        }
                        rval.add( query.getBeanDefinition() );
                    }
                }
            }
        }
        return rval;
    }
}
