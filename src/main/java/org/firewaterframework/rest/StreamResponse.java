package org.firewaterframework.rest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.DefaultCDATA;
import org.firewaterframework.WSException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 10, 2007
 * Time: 10:04:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class StreamResponse extends Response
{
    protected ByteArrayOutputStream outputStream;
    protected PrintWriter printWriter;

    public StreamResponse( Status status, MIMEType mimeType )
    {
        super( status, mimeType );
        outputStream = new ByteArrayOutputStream();
        printWriter = new PrintWriter( outputStream );
    }

    public void write(OutputStream stream)
    {
        try
        {
            stream.write( outputStream.toByteArray() );
        }
        catch( Exception e )
        {
            throw new WSException( "Caught exception wrting Stream based Response", e );
        }
    }

    public Document asDocument()
    {

        // encode the stream as a CDATA section - probably not a great idea in general...
        // TODO: should we just throw an exception here
        Document rval = documentFactory.createDocument();
        Element root = rval.addElement( "result" );
        root.add( new DefaultCDATA( outputStream.toString() ));
        return rval;
    }
}
