package org.firewaterframework.rest;

import org.dom4j.Document;

import java.io.*;

/**
 * This class wraps an unstructured byte stream to hold the response payload.  It is typically used for text
 * or binary response payloads.
 * @author Tim Spurway
 */
public class StreamResponse extends Response
{
    protected PrintWriter writer;
    protected ByteArrayOutputStream stream;

    public StreamResponse( Status status, MIMEType mimeType )
    {
        super( status, mimeType );
        stream = new ByteArrayOutputStream();
        writer = new PrintWriter( stream );
    }

    public StreamResponse( MIMEType mimeType )
    {
        this( Status.STATUS_OK, mimeType );
    }

    public StreamResponse( Status status )
    {
        this( status, MIMEType.text_plain );
    }

    public PrintWriter getWriter() {
        return writer;
    }

    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public Document toDocument()
    {
        // probably shouldn't be used too often...
        Document rval = df.createDocument();
        rval.getRootElement().add( df.createCDATA( stream.toString() ));
        return rval;
    }

    @Override
    public void write( Writer out ) throws IOException
    {
        out.write( stream.toString() );
    }
}
