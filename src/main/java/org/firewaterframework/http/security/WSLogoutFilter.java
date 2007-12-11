package org.firewaterframework.http.security;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class WSLogoutFilter implements Filter {
    //~ Static fields/initializers =====================================================================================

    private static final Log logger = LogFactory.getLog(WSLogoutFilter.class);
    private String filterProcessesUrl = "/logout";

    public void destroy() {}

    public void init(FilterConfig filterConfig) throws ServletException {}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            throw new ServletException("Can only process HttpServletRequest");
        }

        if (!(response instanceof HttpServletResponse)) {
            throw new ServletException("Can only process HttpServletResponse");
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (requiresLogout(httpRequest, httpResponse)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (logger.isDebugEnabled()) {
                logger.debug("Logging out user '" + auth + "' and redirecting to logout page");
            }
            if( httpRequest.getSession( false ) != null )
            {
                httpRequest.getSession(false).invalidate();
            }

            httpResponse.setStatus( 200 );
            httpResponse.getWriter().print( "Logout OK" );
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();

            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Allow subclasses to modify when a logout should tak eplace.
     *
     * @param request the request
     * @param response the response
     *
     * @return <code>true</code> if logout should occur, <code>false</code> otherwise
     */
    protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        int pathParamIndex = uri.indexOf(';');

        if (pathParamIndex > 0) {
            // strip everything after the first semi-colon
            uri = uri.substring(0, pathParamIndex);
        }

        if ("".equals(request.getContextPath())) {
        	return uri.endsWith(filterProcessesUrl);
        }

        return uri.endsWith(request.getContextPath() + filterProcessesUrl);
    }

    public String getFilterProcessesUrl() {
        return filterProcessesUrl;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.filterProcessesUrl = filterProcessesUrl;
    }
}
