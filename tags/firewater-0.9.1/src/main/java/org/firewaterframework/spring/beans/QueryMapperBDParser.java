package org.firewaterframework.spring.beans;

import org.firewaterframework.mappers.jdbc.PivotTreeBuilder;
import org.firewaterframework.mappers.jdbc.QueryMapper;
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
 * Date: Feb 18, 2008
 * Time: 9:23:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryMapperBDParser extends AbstractMapperBDParser
{
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext)
    {
        BeanDefinitionBuilder queryMapper = BeanDefinitionBuilder.rootBeanDefinition( QueryMapper.class);
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
                    else if( "query".equals( child.getLocalName() ))
                    {
                        queryMapper.addPropertyValue( "query", child.getTextContent() );
                    }
                    else if( "pivot-tree".equals( child.getLocalName() ))
                    {
                        queryMapper.addPropertyValue( "pivotTreeBuilder", parsePivotTree( child ));
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

    private AbstractBeanDefinition parsePivotTree( Element element )
    {
        BeanDefinitionBuilder pivotTable = BeanDefinitionBuilder.rootBeanDefinition( PivotTreeBuilder.class);
        NodeList childElements = element.getChildNodes();
        ManagedList subNodes = new ManagedList();

        if ( childElements != null && childElements.getLength() > 0)
        {
            for ( int i = 0; i < childElements.getLength(); ++i )
            {
                if( childElements.item(i) instanceof Element )
                {
                    Element child = (Element)childElements.item(i);
                    if( "column-mappings".equals( child.getLocalName() ))
                    {
                        pivotTable.addPropertyValue( "columnMappingsString", child.getTextContent() );
                    }
                    else if( "pivot-tree".equals( child.getLocalName() ))
                    {
                        subNodes.add( parsePivotTree( child ));
                    }
                }
            }
            if( subNodes.size() > 0 )
            {
                pivotTable.addPropertyValue( "subNodes", subNodes );
            }
        }

        String resource = element.getAttribute( "resource" );
        RuntimeBeanReference ref = new RuntimeBeanReference( resource );
        pivotTable.addPropertyValue( "resourceDescriptor", ref );
        return pivotTable.getBeanDefinition();
    }

}
