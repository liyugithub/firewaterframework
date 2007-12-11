package org.firewaterframework.http.security;

import org.acegisecurity.vote.AccessDecisionVoter;
import org.acegisecurity.ConfigAttribute;
import org.acegisecurity.Authentication;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.intercept.web.FilterInvocation;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Nov 20, 2007
 * Time: 1:50:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class MethodRoleVoter implements AccessDecisionVoter
{
    protected String methodAttribute = "_method";
    protected String expectedMethod = "POST";

    public boolean supports(ConfigAttribute attribute)
    {
        if ((attribute.getAttribute() != null) && attribute.getAttribute().startsWith( getMethodRolePrefix() ))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean supports(Class clazz)
    {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int vote(Authentication authentication, Object object, ConfigAttributeDefinition config)
    {
        int rval = ACCESS_ABSTAIN;
        Iterator<ConfigAttribute> iter = config.getConfigAttributes();
        while( iter.hasNext() )
        {
            ConfigAttribute attr = iter.next();
            if( supports( attr ))
            {
                FilterInvocation fi = (FilterInvocation)object;
                HttpServletRequest request = fi.getHttpRequest();
                String requestMethod = request.getMethod();
                if( !"GET".equalsIgnoreCase( requestMethod ))
                {
                    // only do checks against non-GET requests - the actual REST method is an attribute in the request
                    String subRequestMethod = (String)request.getParameter( methodAttribute );
                    if( subRequestMethod != null )
                    {
                        // by REST convention, if there is no _method attribute, assume it's a POST to the url
                        requestMethod = subRequestMethod;
                    }
                }


                if( requestMethod.equalsIgnoreCase( expectedMethod ))
                {
                    // we've matched up the method with the expected method - snip the role out of the argument and compare to authentication
                    String role = attr.getAttribute().substring( getMethodRolePrefix().length() );
                    for (int i = 0; i < authentication.getAuthorities().length; i++)
                    {
                        if ( role.equals( authentication.getAuthorities()[i].getAuthority() ))
                        {
                            return ACCESS_GRANTED;
                        }
                    }
                    rval = ACCESS_DENIED;
                }
            }
        }
        return rval;
    }

    public String getMethodRolePrefix() {
        return expectedMethod + '_';
    }

    public String getMethodAttribute() {
        return methodAttribute;
    }

    public void setMethodAttribute(String methodAttribute) {
        this.methodAttribute = methodAttribute;
    }

    public String getExpectedMethod() {
        return expectedMethod;
    }

    public void setExpectedMethod(String expectedMethod) {
        this.expectedMethod = expectedMethod;
    }
}

