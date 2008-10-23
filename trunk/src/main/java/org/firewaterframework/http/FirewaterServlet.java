package org.firewaterframework.http;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.firewaterframework.WSException;
import org.firewaterframework.util.PrettyDocumentFactory;
import org.firewaterframework.mappers.Mapper;
import org.firewaterframework.mappers.jdbc.ResourceDescriptor;
import org.firewaterframework.rest.Method;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Response;
import org.firewaterframework.rest.Status;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.StringTemplate;
import org.w3c.dom.ProcessingInstruction;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 */
public class FirewaterServlet extends HttpServlet
{
    protected static final Log log = LogFactory.getLog( FirewaterServlet.class );
    public static final String METHOD_ARG = "_method";
    public static final String DISPATCHER_BEAN_NAME = "dispatcher";
    public static final String XML_VERBATIM_URL = "/__prettyprint__";
    protected Mapper dispatcher;
    protected String verbatimXsl;

    @Override
    public void init( ServletConfig config ) throws ServletException
    {
        super.init( config );

        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext( config.getServletContext() );
        String dispatcherBeanName = config.getInitParameter( DISPATCHER_BEAN_NAME );
        if( dispatcherBeanName == null )
        {
            dispatcherBeanName = DISPATCHER_BEAN_NAME;
        }

        dispatcher = (Mapper)ctx.getBean( dispatcherBeanName );
        if( dispatcher == null )
        {
            log.error( "Serious error - no DISPATCHER bean found for RESTServlet - failing" );
            throw new WSException( "Serious error - no DISPATCHER bean found for RESTServlet - failing" );
        }
        else
        {
            log.info("Starting Firewater Servlet");
        }
    }

    @Override
    public void service( HttpServletRequest request, HttpServletResponse response )
    {
        long timeIn = System.currentTimeMillis();
        try
        {
            String path = request.getPathInfo();

            if( verbatimXsl == null ) createPrettyPrintXsl( request );

            // handle the case where we are fetching the XML_VERBATIM_URL
            if( XML_VERBATIM_URL.equalsIgnoreCase( path ))
            {
                response.setContentType( "text/xsl" );
                response.getWriter().write( verbatimXsl );
                response.getWriter().flush();
                return;
            }

            // process the REST/HTTP method
            String methodString = request.getParameter( METHOD_ARG );
            Method method;
            if( methodString == null )
            {
                method = Method.getMethod( request.getMethod() );
            }
            else
            {
                method = Method.getMethod( methodString.toUpperCase() );
            }

            Map<String,Object[]> requestArgs = request.getParameterMap();
            HashMap<String,Object> args = new HashMap<String,Object>();
            for( Map.Entry<String,Object[]> entry: requestArgs.entrySet() )
            {
                if( entry.getValue().length > 1 )
                {
                    args.put( entry.getKey(), entry.getValue() );
                }
                else
                {
                    args.put( entry.getKey(), entry.getValue()[0] );
                }
            }

            // add any 'special' http request parameters
            String requestURL = request.getRequestURL().toString();
            int uriIndex = requestURL.indexOf( request.getRequestURI() );
            String hostInfo = requestURL.substring( 0, uriIndex );
            args.put( "_request_hostInfo", hostInfo );
            args.put( "_request_remoteAddr", request.getRemoteHost() );

            String idURL = path;
            if( request.getQueryString() != null )
            {
                idURL += '?' + request.getQueryString();
            }

            Request restRequest = new Request( idURL, method, args, true ); 

            log.info( "handling REST request: " + restRequest.getMethod() + " " + restRequest.getUrl() );
            Response restResponse = dispatcher.handle( restRequest );

            if( restResponse != null )
            {
                log.info( "Response for: " + restRequest.getMethod() + " " + restRequest.getUrl() + " " + restResponse.getStatus().getCode() );
	            // encode the baseUrl into the response - it is the requestURI - path
	            String uri = request.getRequestURI();
	            String baseURL = uri.substring( 0, uri.indexOf( path ));
	            restResponse.setBaseURL( baseURL );
	            
	            response.setStatus( restResponse.getStatus().getCode() );
	            response.setHeader( "Cache-Control", "no-cache" );
	            response.setHeader( "Pragma", "no-cache" );
	            response.setDateHeader( "Expires", 0 );
	            response.setContentType( restResponse.getMimeType().getType() );
                response.setContentLength( restResponse.getContentLength() );
                restResponse.write( response.getWriter() );
                response.getWriter().flush();
            }
            else
            {
                throw new WSException( "Empty response", Status.STATUS_SERVER_ERROR );
            }
        }
        catch( WSException e )
        {
            log.error( "Error handling Firewater Request: ", e );

            if( e.getStatus() == Status.STATUS_SERVER_ERROR )
            {
                printStackTrace( e );
            }
            try
            {
                response.sendError( e.getStatus().getCode(), "Error handling REST request for URL: " + request.getPathInfo() + " error: " + e.getMessage() );
            }
            catch( Exception ex ){ log.error( "Pathetic, caught error sending error..."); }
        }
        catch( Exception e )
        {
            log.error( "Error handling Firewater Request: ", e );
            printStackTrace( e );
            try
            {
                response.sendError( 500, "Error handling REST request for URL: " + request.getPathInfo() + " error: " + e.getMessage() );
            }
            catch( Exception ex ){ log.error( "Pathetic, caught error sending error..."); }
        }
        finally
        {
            log.info( "Firewater request completed in(ms): " + (Long)( System.currentTimeMillis() - timeIn ));
        }
    }

    protected void printStackTrace( Exception e )
    {
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter( writer );
        e.printStackTrace( pw );
        pw.flush();
        pw.close();
        log.error( writer.getBuffer().toString() );
    }

    protected void createPrettyPrintXsl( HttpServletRequest req )
    {
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext( this.getServletContext() );
        String contextServletPath = req.getContextPath() + req.getServletPath();

        String hrefPath = '"' + contextServletPath + XML_VERBATIM_URL + '"';
        PrettyDocumentFactory.getInstance().setProcessingInstruction( "xml-stylesheet", "type=\"text/xsl\" href=" + hrefPath );

        // load the xmlverbatim.xsl file as a template
        StringTemplateGroup group = new StringTemplateGroup("xslgroup");
        StringTemplate st = group.getInstanceOf("META-INF/xmlverbatim");

        // find all of the resources in our Spring config - these need to be merged with the XSL file
        String[] resources = ctx.getBeanNamesForType( ResourceDescriptor.class );
        Set<String> resourceUrlPatterns = new HashSet<String>();
        resourceUrlPatterns.add( "url" );
        for( String resourceName: resources )
        {
            ResourceDescriptor resource = (ResourceDescriptor)ctx.getBean( resourceName );

            // add all the relative resources - these are links
            if( resource != null && resource.getRelativeReferences() != null )
            {
                for( String entry: resource.getRelativeReferences().keySet() )
                {
                    resourceUrlPatterns.add( entry + "URL" );
                }
            }

            // also add any property that ends in 'URL'
            if( resource != null && resource.getAttributes() != null )
            {
                for( String entry: resource.getAttributes() )
                {
                    if( entry.endsWith( "URL" ))
                    {
                        resourceUrlPatterns.add( entry );
                    }
                }
            }
        }

        // set the servlet context name in the XSL
        st.setAttribute( "context", contextServletPath );

        // merge the template
        st.setAttribute( "resources", resourceUrlPatterns );
        verbatimXsl = st.toString();
    }

}
