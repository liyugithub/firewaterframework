package org.firewaterframework.rest;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;

import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Nov 28, 2007
 * Time: 10:26:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class Response
{
    protected static DocumentFactory documentFactory = new DocumentFactory();
    
    protected MIMEType mimeType;
    protected Status status;

    public Response( Status status, MIMEType mimeType )
    {
        this.status = status;
        this.mimeType = mimeType;
    }

    public Response( MIMEType mimeType )
    {
        this( Status.STATUS_OK, mimeType );
    }

    public Response( Status status )
    {
        this( status, MIMEType.text_plain );
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public MIMEType getMimeType() {
        return mimeType;
    }

    public void setMimeType(MIMEType mimeType) {
        this.mimeType = mimeType;
    }

    public void write( OutputStream stream )
    {
    }

    public Document asDocument()
    {
        return documentFactory.createDocument( );
    }
}
