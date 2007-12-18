package org.firewaterframework.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.firewaterframework.WSException;
import org.firewaterframework.mappers.Mapper;
import org.firewaterframework.rest.Method;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Response;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
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
 * Created By: tspurway
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

        // pull the config out of the spring web context - serious error if nothing there...
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext( config.getServletContext() );
        dispatcher = (Mapper)ctx.getBean( dispatcherBeanName );
        if( dispatcher == null )
        {
            log.error( "Serious error - no DISPATCHER bean found for RESTServlet - failing" );
            throw new WSException( "Serious error - no DISPATCHER bean found for RESTServlet - failing" );
        }
        else
        {
            log.info("Starting REST service layer");
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response )
    {
        assert dispatcher != null;
        try
        {
            String getModel = request.getPathInfo();
            log.debug( "REST GET " + getModel );
            Request restRequest = new Request( getModel, Method.GET, new ServletRequestParameterPropertyValues( request ));
            Response restResponse = dispatcher.handle( restRequest );
            handleResponse( restResponse, request, response );
        }
        catch( Exception e )
        {
            log.error( "Error handling REST Request: ", e );
            StringWriter writer = new StringWriter();
            PrintWriter pw = new PrintWriter( writer );
            e.printStackTrace( pw );
            pw.flush();
            pw.close();
            log.error( writer.getBuffer().toString() );
            try
            {
                response.sendError( 500, "Error handling GET for URL: " + request.getPathInfo() + " error: " + e.getMessage() );
            }
            catch( Exception ex ){}
        }
    }

    @Override
    public void doPost( HttpServletRequest request, HttpServletResponse response )
    {
        try
        {
            Map<String,Object> args = new HashMap<String,Object>();

            // handle multi-part stuff
            /*if(ServletFileUpload.isMultipartContent( request ))
            {

                FileItemFactory fiFac = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload( fiFac );
                List<FileItem> items = upload.parseRequest( request );

                for( FileItem item: items )
                {
                    if( item.isFormField() )
                    {
                        args.put( item.getFieldName(), item.getString() );
                    }
                    else
                    {
                        // here we have a multipart thingy - we need to move it to a unique name in our upload_image_directory
                        String fileName = item.getName();
                        int dotIndex = fileName.lastIndexOf( '.' );
                        String suffix = "";
                        if( dotIndex > -1 )
                        {
                            suffix = fileName.substring( dotIndex );
                        }

                        // now, let's move the file to our upload directory, if it exists
                        if( restConfig.getUploadImageDirectory() != null )
                        {
                            File imageFile = File.createTempFile( "img_", suffix, restConfig.getUploadImageDirectory().getFile() );
                            item.write( imageFile );
                            args.put( item.getFieldName(), imageFile.getName() );
                        }
                        else
                        {
                            log.error( "No IMAGE UPLOAD DIRECTORY defined on uploading image: " + item.getName() +
                                    ".  This should be set as an init_parmam on the RESTServlet" );
                            args.put( item.getFieldName(), item.getName() );
                        }
                    }
                }
            }
            else
            {
                args.putAll( setupArguments( request.getParameterMap() ));
            }*/

            String postModel = request.getPathInfo();
            String methodString = (String)args.get( METHOD_ARG );
            Method method = Method.POST;
            if( methodString != null &&
                    ("PUT".equalsIgnoreCase( methodString ) || "DELETE".equalsIgnoreCase( methodString )))
            {
                method = new Method( methodString.toUpperCase() );
            }

            log.debug( "REST " + method + " " + postModel );

            Request restRequest = new Request( postModel, method );
            Response restResponse = dispatcher.handle( restRequest );
            handleResponse( restResponse, request, response );
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
                response.sendError( 500, "Error handling POST for URL: " + request.getPathInfo() + " error: " + e.getMessage() );
            }
            catch( Exception ex ){}
        }
    }

    protected void handleResponse( Response restResponse, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        response.setStatus( restResponse.getStatus().getCode() );
        
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        response.setDateHeader ("Expires", 0);

        // check for errors
        if( restResponse.getStatus().getCode() != 200 )
        {
            response.setContentType( "text/html" );
            response.getWriter().write( "Error handling REST request URL: " + request.getRequestURL() + " message: " + restResponse.getStatus() );
        }
        else
        {
            response.setContentType( restResponse.getMimeType().getType() );
            restResponse.write( response.getWriter() );
        }
        response.getWriter().flush();
    }
}
