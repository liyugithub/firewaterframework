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
import org.springframework.beans.MutablePropertyValues;

import java.net.URLDecoder;
import java.util.*;
import java.io.Serializable;

/**
 * This class is the Request object for the Firewater framework.  It contains the request URL, any arguments,
 * and the METHOD of the underlying request.
 */
public class Request implements Serializable
{
    public enum Header implements Serializable
    {
        Accept(),
        AcceptEncoding("Accept-Encoding"),
        Authorization(),
        CacheControl("Cache-Control"),
        Date(),
        Host(),
        IfMatch("If-Match"),
        IfModifiedSince("If-Modified-Since"),
        IfNoneMatch("If-None-Match"),
        IfUnmodifiedSince("If-Unmodified-Since"),
        UserAgent("User-Agent");

        private final String attributeName;

        Header()
        {
            attributeName = this.name();
        }

        Header( String attributeName )
        {
            this.attributeName = attributeName;
        }

        public String getAttributeName()
        {
            return attributeName;
        }
    }

    protected String url;
    protected String baseUrl;
    protected MutablePropertyValues args;
    protected Method method;
    protected String idString;
    protected Map<Header,String> headers = new HashMap<Header,String>();

    /**
     * Create a new REST request.  Note that incoming URLs are not complete URLs, but rather logical URLs
     * stripped of protocol, host, port, application, and prefix.  For example, if our actual Firewater servlet
     * for handling web request is rooted at <code>http://www.abc.com/myapp/webservices/</code> and our
     * web API is configured to handle the <code>/pets</code> resource request.  Then the url attribute in
     * this Request object will simply be <code>/pets</code>.
     *
     * @param url the URL of the Requested resource
     * @param method GET, PUT, POST, DELETE, HEAD or OPTIONS
     * @param args the arguments
     * @param argsAlreadyProcessed this is true for those callers that have already URLdecoded the query string
     *  into the args (ie. Servlets).  If you want the Request object
     *  to decode and parse the query string, set this to false (default behaviour of the other constructors).
     */
    public Request( String url, Method method, MutablePropertyValues args, boolean argsAlreadyProcessed )
    {
        this.method = method;
        this.args = args;
        if( argsAlreadyProcessed )
        {
            this.url = url;
            this.baseUrl = url;
            if( url != null )
            {
                int questionIndex = url.indexOf( '?' );
                if( questionIndex > -1 )
                {
                    this.baseUrl = url.substring( 0, questionIndex );
                }
            }
        }
        else
        {
            // Parse the query string.
            setInternalUrl( url );
        }
    }

    public Request( String url, Method method, MutablePropertyValues args )
    {
        this( url, method, args, false );
    }

    public Request( String url, Method method, Map args, boolean argsAlreadyProcessed )
    {
        this( url, method, new MutablePropertyValues(args), argsAlreadyProcessed );
    }

    public Request( String url, Method method, Map args )
    {
        this( url, method, new MutablePropertyValues( args ), false );
    }

    public Request( Map args )
    {
        this( "/_no_url_", Method.GET, new MutablePropertyValues( args ), false );
    }

    public Request( String url, Method method )
    {
        this( url, method, (MutablePropertyValues)null, false );
    }

    public Request( String url )
    {
        this( url, Method.GET, (MutablePropertyValues)null, false );
    }

    public String getUrl() {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public String getBaseUrl(){
        return baseUrl;
    }

    /**
     * Setting the internal url of this request implies that any arguments passed in with the URL (eg /pets?age=9&region=4)
     * will be parsed out and added to the args of this request.  Note that the Request will differentiate between
     * the id URL and the baseURL.  The baseURL is the URL stripped of any arguments and is the URL to actually
     * match against the configured list of patterns.  Note that downstream cacheing schemes must use the full url in order
     * accurately cache appropriate Responses for the given Request.
     * <p>
     * Servlet containers will have already parsed the query string and added those values to the args.  This method
     * will typically only be used by the constructors that don't include the query string, which will be called from
     * non-servlet front ends (like APIs or testcases).
     * 
     * @see org.firewaterframework.mappers.RouteMapper
     * @param url the full logical url, including arguments for this request
     *
     */
    public void setInternalUrl(String url) {
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
                queryURL = url.substring( questionIndex + 1, url.length() );

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
                        value = argPair.substring( equalsIndex + 1, argPair.length() );

                        // remove any leading or trailing quotes, decode the value
                        if( (value.startsWith( "'" ) || value.startsWith( "\"")) &&
                            (value.endsWith("'") || value.endsWith( "\"") ))
                        {
                            value = value.substring( 1, value.length() - 1 );
                        }
                        try{
                        	value = URLDecoder.decode( value, "ISO-8859-1" );
                        }catch (Exception e){
                        	throw new RuntimeException( "unable to decode URL: ", e);
                        }
                    }
                    this.getArgs().addPropertyValue( key, value );
                }
            }
        }
    }

    public MutablePropertyValues getArgs()
    {
        // lazily instantiate args
        if( args == null )
        {
            args = new MutablePropertyValues();
        }
        return args;
    }

    public void setArgs( MutablePropertyValues args ) {
        this.args = args;
    }

    public Object getArg( String name )
    {
        return getArgs().getPropertyValue( name );
    }

    public void setArg( String name, Object value )
    {
        getArgs().addPropertyValue( name, value );
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod( Method method ) {
        this.method = method;
    }

    public String getParameter( String parameterName )
    {
        if( args.getPropertyValue( parameterName ) != null )
            return args.getPropertyValue( parameterName ).getValue().toString();
        return null;
    }

    public Map<Header,String> getHeaders()
    {
        return headers;
    }

    public void addHeader( Header key, String value )
    {
        headers.put( key, value );
    }

    public String getQueryString( )
    {
        return getQueryString( true );
    }
    
    public String getQueryString( boolean includeQuestionMark )
    {
        if( url != null )
        {
            // strip off any arguments from incoming URL
            int questionIndex = url.indexOf( '?' );
            if( questionIndex > 0 && questionIndex < url.length() )
            {
                if( includeQuestionMark ) return '?' + url.substring( questionIndex + 1 );
                else return url.substring( questionIndex + 1 );
            }
        }
        return "";
    }
}
