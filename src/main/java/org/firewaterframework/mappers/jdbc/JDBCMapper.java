package org.firewaterframework.mappers.jdbc;

import org.firewaterframework.mappers.Mapper;
import org.firewaterframework.rest.Request;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.dom4j.Element;

import javax.sql.DataSource;

/**
 * The superclass for all Mappers that handle JDBC database resources.  This class expects
 * a {@link DataSource} object on configuration, which creates a Spring {@link JdbcTemplate}
 * object.
 *
 * @author Tim Spurway
 */
public abstract class JDBCMapper extends Mapper
{
    protected JdbcTemplate template;

    /**
     * @param ds the DataSource that queries and executions will be executed against
     * in response to REST Requests 
     */
    @Required
    public void setDataSource( DataSource ds )
    {
        template = new JdbcTemplate( ds );
    }

}
