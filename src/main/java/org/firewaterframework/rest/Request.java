package org.firewaterframework.rest;

import org.springframework.beans.MutablePropertyValues;

import java.net.URLDecoder;
import java.util.Map;

/**
 * This class is the Request object for the Firewater framework.  It contains the request URL, any arguments,
 * and the METHOD of the underlying request.
 */
public class Request
{
    protected String url;
    protected String baseUrl;
    protected MutablePropertyValues args;
    protected Method method;

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
     * @param argsAlreadyProcessed this is true for those callers that have already URLdecoded tthe query string
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

    public Request( String url, Method method )
    {
        this( url, method, (MutablePropertyValues)null, false );
    }

    public String getIdString()
    {
        return url;
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
                        value = URLDecoder.decode( value );
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

    public Method getMethod() {
        return method;
    }

    public void setMethod( Method method ) {
        this.method = method;
    }
}
