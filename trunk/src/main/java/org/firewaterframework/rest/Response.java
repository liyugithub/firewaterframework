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

import java.io.IOException;
import java.io.Writer;

/**
 * This class is responsible for result representation of a REST Request.  It contains the MIMEType of the
 * response and the HTTP status code.  
 */
public class Response
{
    protected MIMEType mimeType;
    protected Status status;
    protected String baseURL;

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

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public Document toDocument()
    {
        return null;
    }

    public int getContentLength()
    {
        return 0;
    }

    public void write( Writer out ) throws IOException
    {
    }
}
