package org.firewaterframework;
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
import org.firewaterframework.rest.Status;

/**
 * A generic runtime exception for use in processing REST Requests.  Note that this class wraps a Status code
 * so that Response objects can be easily constructed.
 * @author Tim Spurway
 * 
 */
public class WSException extends RuntimeException
{
    protected Status status = Status.STATUS_SERVER_ERROR;
    
    public WSException( String description )
    {
        super( description );
    }

    public WSException( String description, Exception cause )
    {
        super( description, cause );
    }

    public WSException( String description, Status status, Exception cause )
    {
        super( description, cause );
        this.status = status;
    }

    public WSException( String description, Status status )
    {
        super( description );
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
