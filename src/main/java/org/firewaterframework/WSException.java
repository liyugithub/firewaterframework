package org.firewaterframework;

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
