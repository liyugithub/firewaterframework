package org.firewaterframework.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.firewaterframework.WSException;
import org.firewaterframework.mappers.Mapper;
import org.firewaterframework.rest.Method;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Response;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.io.StringWriter;
import java.io.PrintWriter;

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
            try
            {
                response.sendError( e.getStatus().getCode(), e.getMessage() );
            }
            catch( Exception ex ){ log.error( "Pathetic, caught error sending error..."); }
        }
        catch( Exception e )
        {
            log.error( "Error handling Firewater Request: ", e );
            StringWriter writer = new StringWriter();
            PrintWriter pw = new PrintWriter( writer );
            e.printStackTrace( pw );
            pw.flush();
            pw.close();
            log.error( writer.getBuffer().toString() );
            try
            {
                response.sendError( 500, "Error handling REST request for URL: " + request.getPathInfo() + " error: " + e.getMessage() );
            }
            catch( Exception ex ){ log.error( "Pathetic, caught error sending error..."); }
        }
    }
}
