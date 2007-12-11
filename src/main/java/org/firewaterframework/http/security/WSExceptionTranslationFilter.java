package org.firewaterframework.http.security;

import org.springframework.beans.factory.InitializingBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.AuthenticationException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Oct 31, 2007
 * Time: 4:34:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class WSExceptionTranslationFilter  implements Filter, InitializingBean {
    //~ Static fields/initializers =====================================================================================

    private static final Log logger = LogFactory.getLog(WSExceptionTranslationFilter.class);

    //~ Methods ========================================================================================================

    public void afterPropertiesSet() throws Exception
    {
    }

    public void destroy() {}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        if (!(request instanceof HttpServletRequest))
        {
            throw new ServletException("HttpServletRequest required");
        }

        if (!(response instanceof HttpServletResponse))
        {
            throw new ServletException("HttpServletResponse required");
        }

        HttpServletResponse httpResponse = (HttpServletResponse)response;
        try
        {
            chain.doFilter(request, response);
            if (logger.isDebugEnabled())
            {
                logger.debug("Chain processed normally");
            }
        }
        catch (AuthenticationException ex)
        {
            httpResponse.sendError( 401, "Authentication Failed" );
        }
        catch (AccessDeniedException ex)
        {
            httpResponse.sendError( 403, "Access Denied" );
        }
        catch (ServletException ex)
        {
            if (ex.getRootCause() instanceof AuthenticationException )
            {
                httpResponse.sendError( 401, "Authentication Failed" );
            }
            else if ( ex.getRootCause() instanceof AccessDeniedException )
            {
                httpResponse.sendError( 403, "AccessDenied" );
            }
            else
            {
                throw ex;
            }
        } 
    }

    public void init(FilterConfig filterConfig) throws ServletException {}

}
