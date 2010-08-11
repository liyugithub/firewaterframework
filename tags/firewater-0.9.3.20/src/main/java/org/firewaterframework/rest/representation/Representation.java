package org.firewaterframework.rest.representation;

import org.firewaterframework.rest.MIMEType;

import java.io.Writer;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Oct 22, 2008
 * Time: 9:51:44 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Representation
{
    String getName();
    void setName( String name );
    Representation addChild( String name );
    void addChild( Representation representation );
    void addAttribute( String key, Object value );
    void write( Writer out ) throws IOException;
    int getContentLength();
    MIMEType getMimeType();
    void setMimeType( MIMEType mimeType );
    Object getUnderlyingRepresentation();
}
