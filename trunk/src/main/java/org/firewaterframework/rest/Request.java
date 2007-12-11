package org.firewaterframework.rest;

import java.util.HashMap;
import java.util.Map;

public class Request
{
    protected String url;
    protected String baseUrl;
    protected Map<String,Object> args;
    protected Method method;

    public Request( String url, Method method, Map<String,Object> args )
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
                queryURL = url.substring( questionIndex + 1, url.length() - 1 );

                // add the query args to the argument map
                String[] argPairs = queryURL.split( "&" );
                for( String argPair: argPairs )
                {
                    int equalsIndex = argPair.indexOf( '=' );
                    String key = argPair;
                    String value = "true";
                    if( equalsIndex > -1 )
                    {
                        key = argPair.substring( 0, equalsIndex );
                        value = argPair.substring( equalsIndex + 1, argPair.length() - 1 );
                    }
                    this.getArgs().put( key, value );
                }
            }
        }
    }

    public Map<String, Object> getArgs()
    {
        if( args == null )
        {
            args = new HashMap<String,Object>();
        }
        return args;
    }

    public void setArgs( Map<String, Object> args ) {
        this.args = args;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod( Method method ) {
        this.method = method;
    }
}
