package org.firewaterframework.rest;

import org.dom4j.Document;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Nov 28, 2007
 * Time: 10:26:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class Response
{
    protected MIMEType mimeType;
    protected Status status;
    protected Document content;

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

    public Document getContent() {
        return content;
    }

    public void setContent(Document content) {
        this.content = content;
    }
}
