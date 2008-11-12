package org.firewaterframework.rest.representation;

import org.firewaterframework.util.PrettyDocumentFactory;
import org.firewaterframework.WSException;
import org.firewaterframework.rest.MIMEType;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import java.io.StringWriter;


/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Oct 22, 2008
 * Time: 10:06:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class XMLRepresentation extends AbstractRepresentation
{
    static PrettyDocumentFactory df = PrettyDocumentFactory.getInstance();

    protected Element element;
    protected Document parentDocument;

    public XMLRepresentation()
    {
        mimeType = MIMEType.application_xml;
    }

    protected XMLRepresentation( Document parentDocument, Element element )
    {
        this();
        this.parentDocument = parentDocument;
        this.element = element;
    }

    public String getName()
    {
        return getElement().getTagName();
    }

    public void setName(String name)
    {
        if( element == null )
        {
            element = getElement( name );
        }
        else
        {
            parentDocument.renameNode( element, null, name );
        }
    }

    public void addChild(Representation child)
    {
        if( child instanceof XMLRepresentation )
        {
            getElement().appendChild( ((XMLRepresentation)child).element );
        }
        else
        {
            throw new WSException( "Cannot mix representations" );
        }
    }

    public Representation addChild(String name)
    {
        Element childElement = getParentDocument().createElement( name );
        getElement().appendChild( childElement );
        return new XMLRepresentation( getParentDocument(), childElement );
    }

    public void addAttribute(String key, Object value)
    {
        getElement().setAttribute( key, value.toString() );
    }

    protected Element getElement( )
    {
        return getElement( "result" );
    }
    
    protected Element getElement( String name )
    {
        if( element == null )
        {
            element = getParentDocument().createElement( name );
            getParentDocument().appendChild( element );
        }
        return element;
    }

    protected Document getParentDocument()
    {
        if( parentDocument == null )
        {
            parentDocument = PrettyDocumentFactory.getInstance().createDocument();
        }
        return parentDocument;
    }

    public Document getUnderlyingRepresentation()
    {
        return getParentDocument();
    }

    protected String getContent()
    {
        try
        {
            DOMSource domSource = new DOMSource( getParentDocument() );
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch(TransformerException ex)
        {
            throw new WSException( "Couldn't transform document: ", ex );
        }
    }
}
