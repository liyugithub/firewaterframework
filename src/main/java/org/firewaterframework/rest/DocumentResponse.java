package org.firewaterframework.rest;

import org.dom4j.Document;
import org.firewaterframework.WSException;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 10, 2007
 * Time: 9:57:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class DocumentResponse extends Response
{
    protected Document document;

    public DocumentResponse( Status status, MIMEType mimeType, Document document )
    {
        super( status, mimeType );
        this.document = document;
    }
    
    public void write(OutputStream stream)
    {
        PrintWriter wr = new PrintWriter( stream );
        try
        {
            document.write( wr );
        }
        catch( Exception e )
        {
            throw new WSException( "Caught exception writing XML Response document", e );
        }
    }

    public Document asDocument()
    {
        return document;
    }

    public Document getDocument()
    {
        return document;
    }

    public void setDocument(Document document)
    {
        this.document = document;
    }
}
