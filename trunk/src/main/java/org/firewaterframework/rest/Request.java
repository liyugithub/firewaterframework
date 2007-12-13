package org.firewaterframework.rest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;

public class Request
{
    protected String url;
    protected String baseUrl;
    protected MutablePropertyValues args;
    protected Method method;

    public Request( String url, Method method, MutablePropertyValues args )
    {
        setUrl( url );
        this.method = method;
        this.args = args;
    }

    public Request( String url, Method method )
    {
        this( url, method, null );
    }

    public String getIdString()
    {
        return url;
    }

    public String getUrl() {
        return url;
    }

    public String getBaseUrl(){
        return baseUrl;
    }

    public void setUrl(String url) {
        this.url = url;

        if( url != null )
        {
            // strip off any arguments from incoming URL
            int questionIndex = url.indexOf( '?' );
            this.baseUrl = url;
            String queryURL = null;
            if( questionIndex > -1 )
            {
                this.baseUrl = url.substring( 0, questionIndex );
            }
        }
    }

    public MutablePropertyValues getArgs()
    {
        return args;
    }

    public void setArgs( MutablePropertyValues args ) {
        this.args = args;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod( Method method ) {
        this.method = method;
    }
}
