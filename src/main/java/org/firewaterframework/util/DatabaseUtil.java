package org.firewaterframework.util;

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
