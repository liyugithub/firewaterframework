package org.firewaterframework.rest;

/**
 * The HTTP STATUS response values.  Some common responses are included as static properties.
 * @author Tim Spurway
 */
public class Status
{
    public static final Status STATUS_OK = new Status(200);
    public static final Status STATUS_NOT_FOUND = new Status(404);
    public static final Status STATUS_INVALID_AUTH = new Status(401);
    public static final Status STATUS_ACCESS_DENIED = new Status(403);
    public static final Status STATUS_METHOD_NOT_ALLOWED = new Status(405);
    public static final Status STATUS_SERVER_ERROR = new Status(500);

    protected int code;

    protected Status( int code)
    {
        this.code = code;
    }

    @Override
    public boolean equals( Object other )
    {
        if( other instanceof Status && ((Status)other).code == code)
        {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
