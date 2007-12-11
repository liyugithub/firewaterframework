package org.firewaterframework.http.security;

import org.acegisecurity.userdetails.jdbc.JdbcDaoImpl;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Aug 2, 2007
 * Time: 5:33:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserDaoImpl extends JdbcDaoImpl
{
    protected static Log log = LogFactory.getLog(UserDaoImpl.class);
    protected String additionalUserPropertiesQuery;
    protected List<String> additionalUserProperties;
    protected AdditionalUserDataMapping additionalUserMapping;
    protected UserMapping userMapping;
    protected AuthorityMapping authorityMapping;

    public UserDaoImpl()
    {
        super();
    }

    protected void initMappingSqlQueries() {
        this.userMapping = new UserMapping(getDataSource());
        this.authorityMapping = new AuthorityMapping(getDataSource());
        if( additionalUserPropertiesQuery != null && additionalUserProperties != null )
        {
            additionalUserMapping = new AdditionalUserDataMapping( getDataSource() );
        }
    }

    @Override
    @Transactional( readOnly=true, isolation=Isolation.READ_COMMITTED )
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException, DataAccessException
    {
        List users = userMapping.execute(username);

        if (users.size() == 0)
        {
            throw new UsernameNotFoundException("User not found");
        }

        List<Object> user = (List<Object>) users.get(0); // contains no GrantedAuthority[]
        String password = (String)user.get( 1 );
        Boolean enabled = (Boolean)user.get( 2 );
        Integer userid = (Integer)user.get( 3 );

        List dbAuths = authorityMapping.execute(username);

        addCustomAuthorities(username, dbAuths);

        if (dbAuths.size() == 0)
        {
            throw new UsernameNotFoundException("User has no GrantedAuthority");
        }

        GrantedAuthority[] arrayAuths = (GrantedAuthority[]) dbAuths.toArray(new GrantedAuthority[dbAuths.size()]);

        Map<String,Object> additionalProperties = new HashMap<String,Object>();

        if( additionalUserMapping != null )
        {
            List additionalPropertiesList = additionalUserMapping.execute( username );
            if( additionalPropertiesList.size() != 0 )
                additionalProperties = (Map<String,Object>)additionalPropertiesList.get(0);
        }
        
        return new FullUser(username, password, enabled, true, true, true, arrayAuths, additionalProperties, userid );
    }

    /**
     * Query object to look up a user.
     */
    protected class AdditionalUserDataMapping extends MappingSqlQuery
    {
        protected AdditionalUserDataMapping(DataSource ds) {
            super(ds, additionalUserPropertiesQuery);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }

        protected Object mapRow(ResultSet rs, int rownum)
            throws SQLException
        {
            Map<String,Object> rval = new HashMap<String,Object>();
            for( String property: additionalUserProperties )
            {
                rval.put( property, rs.getObject( property ));
            }
            return rval;
        }
    }

    public String getAdditionalUserPropertiesQuery() {
        return additionalUserPropertiesQuery;
    }

    public void setAdditionalUserPropertiesQuery(String additionalUserPropertiesQuery) {
        this.additionalUserPropertiesQuery = additionalUserPropertiesQuery;
    }

    public List<String> getAdditionalUserProperties() {
        return additionalUserProperties;
    }

    public void setAdditionalUserProperties(List<String> additionalUserProperties) {
        this.additionalUserProperties = additionalUserProperties;
    }

    /**
     * Query object to look up a user's authorities.
     */
    protected class AuthorityMapping extends MappingSqlQuery {
        protected AuthorityMapping(DataSource ds) {
            super(ds, getAuthoritiesByUsernameQuery());
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }

        protected Object mapRow(ResultSet rs, int rownum)
            throws SQLException {
            String roleName = getRolePrefix() + rs.getString(2);
            GrantedAuthorityImpl authority = new GrantedAuthorityImpl(roleName);

            return authority;
        }
    }

    /**
     * Query object to look up a user.
     */
    protected class UserMapping extends MappingSqlQuery {
        protected UserMapping(DataSource ds) {
            super(ds, getUsersByUsernameQuery());
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }

        protected Object mapRow(ResultSet rs, int rownum)
            throws SQLException {
            List<Object> rval = new ArrayList<Object>();
            rval.add( rs.getString(1));
            rval.add( rs.getString(2));
            rval.add(rs.getBoolean(3));
            rval.add(rs.getInt(4));

            return rval;
        }
    }
}
