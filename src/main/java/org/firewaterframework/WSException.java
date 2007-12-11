package org.firewaterframework;

import org.firewaterframework.rest.Status;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Oct 30, 2007
 * Time: 3:08:40 PM
 * To change this template use File | Settings | File Templates.
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
