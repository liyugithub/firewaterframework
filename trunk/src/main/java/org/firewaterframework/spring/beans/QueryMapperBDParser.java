package org.firewaterframework.spring.beans;

import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.firewaterframework.mappers.jdbc.QueryMapper;
import org.firewaterframework.mappers.jdbc.PivotTreeBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Feb 18, 2008
 * Time: 9:23:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryMapperBDParser extends AbstractBeanDefinitionParser
{
    protected static final String validationPackage = "org.firewaterframework.mappers.validation.";
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
                    if( "attribute-columns".equals( child.getLocalName() ))
                    {
                        pivotTable.addPropertyValue( "attributeColumnString", child.getTextContent() );
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

        NamedNodeMap attributes = element.getAttributes();
        if( attributes != null )
        {
            for( int i = 0; i < attributes.getLength(); ++i )
            {
                Attr child = (Attr) attributes.item(i);
                pivotTable.addPropertyValue( xmlStringToBeanName( child.getLocalName() ), child.getValue() );
            }
        }

        return pivotTable.getBeanDefinition();
    }

    private ManagedMap parseFields( Element element )
    {
        ManagedMap fields = new ManagedMap();
        NodeList childElements = element.getChildNodes();

        if ( childElements != null && childElements.getLength() > 0)
        {
            for ( int i = 0; i < childElements.getLength(); ++i )
            {
                if( childElements.item(i) instanceof Element )
                {
                    Element child = (Element) childElements.item(i);
                    BeanDefinitionBuilder validator =
                            BeanDefinitionBuilder.rootBeanDefinition( validationPackage + xmlStringToBeanName( child.getLocalName(), true ));

                    NamedNodeMap attributes = child.getAttributes();
                    for( int j = 0; j < attributes.getLength(); ++j )
                    {
                        Attr attr =(Attr)attributes.item(j);
                        if( "name".equals( attr.getLocalName() ))
                        {
                            fields.put( attr.getValue(), validator.getBeanDefinition() );
                        }
                        else
                        {
                            validator.addPropertyValue( attr.getLocalName(), attr.getValue() );
                        }
                    }
                }
            }
        }
        return fields;
    }

    private String xmlStringToBeanName( String xmlString )
    {
        return xmlStringToBeanName( xmlString,  false );
    }

    private String xmlStringToBeanName( String xmlString, boolean capitalizeFirstLetter )
    {
        boolean capitalize = capitalizeFirstLetter;
        StringBuffer rval = new StringBuffer();
        for( int i = 0; i < xmlString.length(); ++i )
        {
            Character c = xmlString.charAt(i);
            if( '-' == c )
            {
                capitalize = true;
                continue;
            }

            if( capitalize )
            {
                c = Character.toUpperCase( c );
                capitalize = false;
            }
            rval.append( c );
        }
        return rval.toString();
    }
}