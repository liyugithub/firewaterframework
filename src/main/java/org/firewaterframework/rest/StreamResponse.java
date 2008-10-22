package org.firewaterframework.rest;
/*
    Copyright 2008 John TW Spurway
    Licensed under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with the License.
    You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software distributed under the
    License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
    either express or implied. See the License for the specific language governing permissions
    and limitations under the License.
*/
import org.dom4j.Document;
import org.dom4j.DocumentFactory;

import java.io.*;

/**
 * This class wraps an unstructured byte stream to hold the response payload.  It is typically used for text
 * or binary response payloads.
 * @author Tim Spurway
 */
public class StreamResponse extends Response
{
    protected static DocumentFactory df = DocumentFactory.getInstance();
    
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

    public ByteArrayOutputStream getStream()
    {
        return stream;
    }

    public void setStream( ByteArrayOutputStream stream )
    {
        this.stream = stream;
    }

    public void setContent( Object value )
    {
        try
        {
            this.stream.write( value.toString().getBytes() );
        }
        catch( Exception e )
        {
            // swallow, gulp
        }
    }

    @Override
    public int getContentLength()
    {
        return stream.toString().length();
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
