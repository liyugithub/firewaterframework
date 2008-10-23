package org.firewaterframework.rest.representation;

import org.firewaterframework.rest.MIMEType;

import java.io.Writer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Oct 22, 2008
 * Time: 5:47:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractRepresentation implements Representation
{
    private String contentCache;
    private MIMEType mimeType = MIMEType.text_plain;

    public MIMEType getMimeType()
    {
        return mimeType;
    }

    public void setMimeType( MIMEType mimeType )
    {
        this.mimeType = mimeType;
    }

    protected String getContentCache()
    {
        if( contentCache == null )
        {
            contentCache = getContent();
        }
        return contentCache;
    }

    public int getContentLength()
    {
        return getContentCache().length();
    }

    public void write( Writer out ) throws IOException
    {
        out.write( getContentCache() );
    }    

    @Override
    public String toString()
    {
        return getContentCache();
    }

    protected abstract String getContent();

}
