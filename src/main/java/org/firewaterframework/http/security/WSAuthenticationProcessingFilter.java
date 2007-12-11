package org.firewaterframework.http.security;

import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.event.authentication.InteractiveAuthenticationSuccessEvent;
import org.acegisecurity.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Nov 1, 2007
 * Time: 10:17:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class WSAuthenticationProcessingFilter extends AuthenticationProcessingFilter
{
    public void afterPropertiesSet() throws Exception {
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        Authentication authResult) throws IOException
    {
        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success: " + authResult.toString());
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);

        if (logger.isDebugEnabled()) {
            logger.debug("Updated SecurityContextHolder to contain the following Authentication: '" + authResult + "'");
        }

        String targetUrl = determineTargetUrl(request);

        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to target URL from HTTP Session (or default): " + targetUrl);
        }

        onSuccessfulAuthentication(request, response, authResult);

        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }

        response.setStatus( 200 );

        // determine what kind of User we have - if it's a FullUser, we can return the userID (for building URLs)
        if( authResult.getPrincipal() instanceof FullUser && ((FullUser)authResult.getPrincipal()).getUserid() != null )
        {
            response.getWriter().print(((FullUser)authResult.getPrincipal()).getUserid());
        }
        else
        {
            response.getWriter().print( "Login OK" );
        }
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException failed) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(null);

        if (logger.isDebugEnabled()) {
            logger.debug("Updated SecurityContextHolder to contain null Authentication");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Authentication request failed: " + failed.toString());
        }

        try {
            request.getSession().setAttribute(ACEGI_SECURITY_LAST_EXCEPTION_KEY, failed);
        } catch (Exception ignored) {}

        onUnsuccessfulAuthentication(request, response, failed);
        response.sendError( 401, "Login Failed" );
    }
}
