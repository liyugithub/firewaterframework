package org.firewaterframework.mappers.jdbc;
/*
    Copyright 2008 John TW Spurway
    Licensed under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with the License.
    You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software distributed under the
    License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
    either express or implied. See the License for the specific language governing permissions
    and limitations under the License.
*/
import javax.sql.DataSource;

import org.firewaterframework.mappers.Mapper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

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
