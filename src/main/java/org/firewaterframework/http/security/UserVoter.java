package org.firewaterframework.http.security;

import org.acegisecurity.Authentication;
import org.acegisecurity.ConfigAttribute;
import org.acegisecurity.ConfigAttributeDefinition;
import org.acegisecurity.intercept.web.FilterInvocation;
import org.acegisecurity.vote.AccessDecisionVoter;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Oct 15, 2007
 * Time: 10:26:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserVoter implements AccessDecisionVoter
{
    protected String userPrefix = "USER_";
    protected String userIdPattern;

    public boolean supports(ConfigAttribute attribute)
    {
        if ((attribute.getAttribute() != null) && attribute.getAttribute().startsWith( userPrefix ))
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
        Pattern pattern = Pattern.compile( userIdPattern );
        Iterator<ConfigAttribute> iter = config.getConfigAttributes();
        while( iter.hasNext() )
        {
            ConfigAttribute attr = iter.next();
            if( attr.getAttribute().startsWith( userPrefix ))
            {
                String idAttribute = attr.getAttribute().substring( userPrefix.length() );
                FilterInvocation fi = (FilterInvocation)object;
                String url = fi.getRequestUrl();

                // if the principal isn't a FullUser - then we assume it's an anonymous user, which should always be denied
                if( authentication.getPrincipal() instanceof FullUser )
                {
                    FullUser user = (FullUser)authentication.getPrincipal();
                    Matcher matcher = pattern.matcher( url );
                    if( matcher.matches() && matcher.groupCount() > 0 )
                    {
                        String resourceUserId = matcher.group(1);
                        String actualUserId = user.getAdditionalProperties().get( idAttribute ).toString();
                        if( resourceUserId.equals( actualUserId ))
                        {
                            return ACCESS_GRANTED;
                        }
                        else
                        {
                            rval = ACCESS_DENIED;
                        }
                    }
                }
                else
                {
                    rval = ACCESS_DENIED;
                }
            }
        }
        return rval;
    }

    public String getUserPrefix() {
        return userPrefix;
    }

    public void setUserPrefix(String userPrefix) {
        this.userPrefix = userPrefix;
    }

    public String getUserIdPattern() {
        return userIdPattern;
    }

    public void setUserIdPattern(String userIdPattern) {
        this.userIdPattern = userIdPattern;
    }
}
