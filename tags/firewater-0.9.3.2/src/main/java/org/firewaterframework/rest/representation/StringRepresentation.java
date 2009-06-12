package org.firewaterframework.rest.representation;

import org.firewaterframework.rest.MIMEType;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Oct 22, 2008
 * Time: 5:43:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringRepresentation extends AbstractRepresentation
{
    private StringBuffer buffer = new StringBuffer();

    public StringRepresentation( String contents, MIMEType mimeType )
    {
        buffer.append( contents );
        this.mimeType = mimeType;
    }
    
    public StringRepresentation( String contents )
    {
        this( contents, MIMEType.text_plain );
    }

    public String getName() {
        return null;
    }

    public void setName(String name) {
    }

    public Representation addChild(String name) {
        return null;
    }

    public void addChild(Representation representation) {
    }

    public void addAttribute(String key, Object value) {
    }

    protected String getContent() {
        return buffer.toString();
    }

    public String getUnderlyingRepresentation() {
        return getContentCache();
    }
}
