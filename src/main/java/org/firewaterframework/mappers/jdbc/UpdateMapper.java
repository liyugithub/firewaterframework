package org.firewaterframework.mappers.jdbc;

import org.antlr.stringtemplate.StringTemplate;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.firewaterframework.WSException;
import org.firewaterframework.mappers.Mapper;
import org.firewaterframework.rest.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Jun 4, 2007
 * Time: 2:47:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateMapper implements Mapper
{
    public static DocumentFactory factory = DocumentFactory.getInstance();

    protected JdbcTemplate template;
    protected String[] queries;

    @Transactional( readOnly=false,isolation=Isolation.READ_COMMITTED )
    public Response handle( Request request )
    {
        StringTemplate[] queryTemplates = new StringTemplate[queries.length];
        for( int i = 0; i < queries.length; i++ )
        {
            queryTemplates[i] = new StringTemplate( queries[i] );
        }

        try
        {
            Document rval = factory.createDocument();            
            Element root = rval.addElement( "result" );
            Object[] keys = new Object[ queryTemplates.length ];
            Integer i = 0;
            for( StringTemplate queryTemplate: queryTemplates )
            {
                KeyHolder keyHolder = new GeneratedKeyHolder();

                Integer rowsAffected = template.update(
                    new UpdateMapperStatementCreator( queryTemplate, request.getArgs(), keys ),
                    keyHolder );

                Element element = root.addElement( "update" );
                element.addAttribute( "rowsAffected", rowsAffected.toString() );
                element.addAttribute( "updateNumber", i.toString() );

                if( keyHolder.getKey() != null )
                {
                    Object key = keyHolder.getKeys().values().toArray()[0];
                    keys[i] = key;
                    element.addAttribute( "key", key.toString() );
                }
                i++;
            }
            Response response = new DocumentResponse( Status.STATUS_OK, MIMEType.application_xml, rval );
            return response;
        }
        catch( Exception e )
        {
            throw new WSException( "Internal Error processing update.", e );
        }
    }

    public class UpdateMapperStatementCreator implements PreparedStatementCreator
    {
        protected Map<String,Object> args;
        protected StringTemplate queryTemplate;
        protected Object[] keys;

        public UpdateMapperStatementCreator( StringTemplate queryTemplate, Map<String,Object> args, Object[] keys )
        {
            this.args = args;
            this.queryTemplate = queryTemplate;
            this.keys = keys;
        }

        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException
        {
            // substitute stringTemplate values
            for( Map.Entry<String,Object> arg: args.entrySet() )
            {
                queryTemplate.setAttribute( arg.getKey(), arg.getValue() );
            }

            // also substitute all keys that have been generated thusfar
            queryTemplate.setAttribute( "_keys", keys );

            return connection.prepareStatement( queryTemplate.toString(), Statement.RETURN_GENERATED_KEYS );
        }
    }

    @Required
    public void setDataSource( DataSource ds )
    {
        template = new JdbcTemplate( ds );
    }

    @Required
    public void setQueries(String[] queries)
    {
        this.queries = queries;
    }
}
