package org.firewaterframework.mappers.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.firewaterframework.WSException;
import org.firewaterframework.mappers.Mapper;
import org.firewaterframework.rest.*;

import java.util.List;

public class XPathMapper implements Mapper
{
    protected static final Log log = LogFactory.getLog( XPathMapper.class );
    public static DocumentFactory factory = DocumentFactory.getInstance();

    protected String path, subbedPath, urlSelector;
    protected Document source;
    protected boolean deep = false;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDeep() {
        return deep;
    }

    public void setDeep(boolean deep) {
        this.deep = deep;
    }


    public String getUrlSelector() {
        return urlSelector;
    }

    public void setUrlSelector(String urlSelector) {
        this.urlSelector = urlSelector;
    }

    public Response handle( Request request )
    {
        try
        {
            //Response response = source.handle( request );
            List<Element> nodes = source.selectNodes( getSubbedPath( request ));
            Element root = factory.createElement( "result");

            for( Element node: nodes )
            {
                Element copiedNode = copyElement( node );

                // if there is a urlSelector set AND the node has an 'id' tag, create a URL for the node
                if( urlSelector != null &&
                        copiedNode.attributeValue( "id") != null &&
                        copiedNode.attributeValue( "url") == null )
                {
                    copiedNode.addAttribute( "url", '/' + urlSelector + '/' + copiedNode.attributeValue( "id" ));
                }
                root.add( copiedNode );

            }
            Document target = factory.createDocument( root );
            Response rval = new DocumentResponse( Status.STATUS_OK, MIMEType.application_xml, target );
            
            return rval;
        }
        catch( Exception e )
        {
            throw new WSException( "Error processing XPath Mapper for: " + path, e );
        }
    }

    protected Element copyElement( Element node )
    {
        if( deep )
        {
            return node.createCopy();
        }
        else
        {
            Element newNode = factory.createElement( node.getName() );
            for( Attribute attribute: (List<Attribute>)node.attributes() )
            {
                newNode.add( (Attribute) attribute.clone() );
            }
            return newNode;
        }
    }

    public String getSubbedPath( Request request )
    {
        if( subbedPath == null )
        {
            subbedPath = path;
            for( String attr: request.getArgs().keySet() )
            {
                subbedPath = subbedPath.replaceAll( "\\{" + attr + "\\}", request.getArgs().get( attr ).toString() );
            }
        }
        return subbedPath;
    }

    public Document getSource() {
        return source;
    }

    public void setSource(Document source) {
        this.source = source;
    }
    
}
