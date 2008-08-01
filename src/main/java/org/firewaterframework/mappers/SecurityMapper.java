package org.firewaterframework.mappers;

import org.firewaterframework.rest.Response;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Method;
import org.springframework.util.AntPathMatcher;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Mar 28, 2008
 * Time: 8:01:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class SecurityMapper extends Mapper
{
    protected static final Log log = LogFactory.getLog( SecurityMapper.class );

    protected List<ACLEntry> acl;
    protected AntPathMatcher matcher = new AntPathMatcher();
    protected String credentialURL;
    protected Mapper subMapper;
    protected Mapper credentialsMapper;

    public Response handle(Request request)
    {
        // see if the request has an ACL against it, if so enforce credentials
        for( ACLEntry aclEntry: acl )
        {
            if( matcher.match( aclEntry.pattern, request.getUrl() ))
            { 
                // walk the permissions, if any match, we're OK
                Document token = authenticate( request.getParameter( Request.Header.Authorization.getAttributeName() ));
                if( token != null )
                {
                    
                }
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected Document authenticate( String credentials )
    {
        if( credentials != null )
        {
            // credentials must be decoded from base 64
            credentials = String.valueOf( Base64.decodeBase64( credentials.getBytes() ));
            try
            {
                /*String[] userid_password = credentials.split( ":" );
                log.info( "Attempting authentication for user: " + userid_password[0] );
                List<Map<String,Object>> roles = template.queryForList( credentialQuery, userid_password);
                if( roles.size() > 0 )
                {
                    // check for the password
                    String password = (String) roles.get(0).get( PASSWORD_COLUMN );
                    if( password.equals( userid_password[1] ))
                    {
                        AuthToken rval = new AuthToken( (Integer) roles.get(0).get( USER_ID_COLUMN ),  password);
                        for( Map<String,Object> role: roles )
                        {
                            rval.grantedRoles.add( (String) role.get( ROLE_COLUMN ));
                        }
                        return rval;
                    }
                }*/
            }
            catch( Exception e )
            {
                log.error( "Error executing authentication for user: ", e );
            }
        }
        return null;
    }

    public class ACLEntry
    {
        String pattern;
        List<ACLPermission> permissions;
    }

    public class ACLPermission
    {
        Method method;
        String role;        
    }
}
