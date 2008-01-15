package org.firewaterframework.util;
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
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 14, 2007
 * Time: 1:32:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseUtil
{
    public static String loadSQL( Resource sqlFile, DataSource ds ) throws IOException
    {
        BufferedReader br = new BufferedReader( new InputStreamReader( sqlFile.getInputStream() ));

        StringBuffer buf = new StringBuffer( );
        for( String line = br.readLine(); line != null; line = br.readLine() )
        {
            buf.append( line );
        }

        JdbcTemplate template = new JdbcTemplate( ds );
        String str = buf.toString();
        template.execute( str );
        return str;
    }
}
