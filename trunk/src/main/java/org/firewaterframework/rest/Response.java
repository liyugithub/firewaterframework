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
import org.firewaterframework.rest.representation.Representation;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is responsible for result representation of a REST Request.  It contains the MIMEType of the
 * response and the HTTP status code.  
 */
public class Response
{
    protected Status status;
    protected String baseURL;
    protected Representation representation;

    public Response( Status status, Representation representation )
    {
        this.status = status;
        this.representation = representation;
    }

    public Response( Representation representation )
    {
        this( Status.STATUS_OK, representation );
    }

    public Response( Status status )
    {
        this( status, null );
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Representation getRepresentation()
    {
        return representation;
    }

    public void setRepresentation( Representation representation )
    {
        this.representation = representation;
    }

    public MIMEType getMimeType() {
        if( representation != null )
        {
            return representation.getMimeType();
        }
        return MIMEType.text_plain;
    }

    public void setMimeType(MIMEType mimeType) {
        if( representation != null ) representation.setMimeType( mimeType );
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public int getContentLength()
    {
        if( representation != null ) return representation.getContentLength();
        return 0;
    }

    public void write( Writer out ) throws IOException
    {
        if( representation != null ) representation.write( out );
    }
}
