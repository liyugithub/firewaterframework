package org.firewaterframework.rest;

import org.dom4j.Document;

import java.io.IOException;
import java.io.Writer;

/**
 * This Response subclass implements it's payload as an XML document.  It is the default Response class for
 * all of the JDBC based Mappers in Firewater.
 * @see org.firewaterframework.mappers.jdbc.JDBCMapper
 * @author Tim Spurway
 */
public class DocumentResponse extends Response
{
    protected Document document;

    public DocumentResponse( Status status, MIMEType mimeType )
    {
        super( status, mimeType );
    }

    public DocumentResponse( MIMEType mimeType )
    {
        this( Status.STATUS_OK, mimeType );
    }

    public DocumentResponse( Status status )
    {
        this( status, MIMEType.text_plain );
    }

    public Document getDocument() {
        return document;
    }

    /**
     *
     * @param document The dom4j XML document that is the payload for this Response
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    @Override
    public Document toDocument()
    {
        return document;
    }

    @Override
    public void write( Writer out ) throws IOException
    {
        document.write( out );
    }
}
