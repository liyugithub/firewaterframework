package org.firewaterframework.spring.beans;

import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Feb 19, 2008
 * Time: 12:06:19 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractMapperBDParser extends AbstractBeanDefinitionParser
{
    protected static final String validationPackage = "org.firewaterframework.mappers.validation.";
    
    protected ManagedMap parseFields( Element element )
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
    protected String xmlStringToBeanName( String xmlString )
    {
        return xmlStringToBeanName( xmlString,  false );
    }

    protected String xmlStringToBeanName( String xmlString, boolean capitalizeFirstLetter )
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
