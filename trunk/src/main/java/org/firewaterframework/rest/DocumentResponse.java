package org.firewaterframework.rest;

import org.dom4j.Document;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 13, 2007
 * Time: 2:26:07 PM
 * To change this template use File | Settings | File Templates.
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
