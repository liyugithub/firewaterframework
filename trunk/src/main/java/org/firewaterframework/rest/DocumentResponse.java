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

import java.io.IOException;
import java.io.Writer;

/**
 * This Response subclass implements it's payload as an XML document.  It is the default Response class for
 * all of the JDBC based Mappers in Firewater.
 * @see org.firewaterframework.mappers.jdbc.JDBCMapper
 * @author Tim Spurway
 */
public class DocumentResponse extends Response
{
    public static final String BASE_URL = "base-url";

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

    /**
     *
     * @param document The dom4j XML document that is the payload for this Response
     */
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

    @Override
    public void setBaseURL( String baseURL )
    {
        super.setBaseURL( baseURL );
        if( document != null )
        {
            document.getRootElement().addAttribute( BASE_URL, baseURL );
        }
    }
}
