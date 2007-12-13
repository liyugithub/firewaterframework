package org.firewaterframework.mappers.jdbc;

import org.firewaterframework.mappers.Mapper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 12, 2007
 * Time: 2:37:55 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class JDBCMapper extends Mapper
{
    protected JdbcTemplate template;

    @Required
    public void setDataSource( DataSource ds )
    {
        template = new JdbcTemplate( ds );
    }
}
