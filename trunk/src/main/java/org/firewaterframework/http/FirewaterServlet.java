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
import org.firewaterframework.mappers.Mapper;
import org.firewaterframework.rest.Method;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Response;
import org.firewaterframework.rest.Status;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class FirewaterServlet extends HttpServlet
{
    protected static final Log log = LogFactory.getLog( FirewaterServlet.class );
    public static final String METHOD_ARG = "_method";
    public static final String DISPATCHER_BEAN_NAME = "dispatcher";
    protected Mapper dispatcher;

    @Override
    public void init( ServletConfig config ) throws ServletException
    {
        super.init( config );

        String dispatcherBeanName = config.getInitParameter( DISPATCHER_BEAN_NAME );
        if( dispatcherBeanName == null )
        {
            dispatcherBeanName = DISPATCHER_BEAN_NAME;
        }

        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext( config.getServletContext() );
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
        try
        {
            String url = request.getPathInfo();
            String methodString = request.getParameter( METHOD_ARG );
            Method method = Method.GET;
            if( methodString == null )
            {
                if( request.getMethod().equalsIgnoreCase( "POST" ))
                {
                    method = Method.POST;
                }
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
            Request restRequest = new Request( url, method, args, true );
            Response restResponse = dispatcher.handle( restRequest );

            response.setStatus( restResponse.getStatus().getCode() );
            response.setHeader( "Cache-Control", "no-cache" );
            response.setHeader( "Pragma", "no-cache" );
            response.setDateHeader( "Expires", 0 );
            response.setContentType( restResponse.getMimeType().getType() );
            restResponse.write( response.getWriter() );
            response.getWriter().flush();
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
}
