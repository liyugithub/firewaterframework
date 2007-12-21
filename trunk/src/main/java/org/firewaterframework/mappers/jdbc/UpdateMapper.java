package org.firewaterframework.mappers.jdbc;

import org.antlr.stringtemplate.StringTemplate;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.firewaterframework.WSException;
import org.firewaterframework.rest.*;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for servicing the PUT, POST and DELETE method Requests for
 * JDBC resources.  It allows for more than one statement to be executed and for keys
 * generated or accessed in earlier SQL statements to be available to later statements.
 * It uses the StringTemplate template engine and binds incoming Request arguments as
 * template variables.
 *
 * @author Tim Spurway
 */
public class UpdateMapper extends JDBCMapper
{
    public static DocumentFactory factory = DocumentFactory.getInstance();

    protected String[] queries;

    /**
     * Handle a REST Request by 'executing' a sequence of SQL statements.  Bind the Request
     * arguments before assigning them as StringTemplate variables.  This allows validation
     * and data conversions before execution of the actual SQL against the database.
     * <p>
     * This method keeps track of all generated keys from the execution of each SQL
     * statement.  These keys can be accessed in the StringTemplate query using <code>$_keys.key_0$, $_keys.key_1$, ... </code>
     * where the number refers to the index of the query in the queryTemplates variable.  Obviously, you can only
     * access keys 0 through n - 1 where n is the currently executing query.
     * 
     * @param request the incoming REST Request, typically indicating a PUT, POST, or DELETE operation
     * @return a DocumentResponse object containg a simple XML document describing the sucessful execution
     * @throws WSException on error conditions, typically a 500 if, for some reason, the executions fail
     */
    @Transactional( readOnly=false,isolation=Isolation.READ_COMMITTED )
    public Response handle( Request request )
    {
        StringTemplate[] queryTemplates = new StringTemplate[queries.length];
        for( int i = 0; i < queries.length; i++ )
        {
            queryTemplates[i] = new StringTemplate( queries[i] );
        }

        Document rval = factory.createDocument();
        Element root = rval.addElement( "result" );
        Map<String,Object> keys = new HashMap<String,Object>();

        Map<String,Object> translatedArgs = bind( request );

        try
        {
            Integer i = 0;
            for( StringTemplate queryTemplate: queryTemplates )
            {
                KeyHolder keyHolder = new GeneratedKeyHolder();

                Integer rowsAffected = template.update(
                    new UpdateMapperStatementCreator( queryTemplate, translatedArgs, keys ),
                    keyHolder );

                Element element = root.addElement( "update" );
                element.addAttribute( "rowsAffected", rowsAffected.toString() );
                element.addAttribute( "updateNumber", i.toString() );

                if( keyHolder.getKey() != null )
                {
                    Object key = keyHolder.getKeys().values().toArray()[0];
                    keys.put( "key_" + i, key );
                    element.addAttribute( "key", key.toString() );
                }
                i++;
            }
            DocumentResponse response = new DocumentResponse( Status.STATUS_OK, MIMEType.application_xml );
            response.setDocument( rval );
            return response;
        }
        catch( WSException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            throw new WSException( "Internal Error processing update.", e );
        }
    }

    /**
     * Used to create the prepared statement for execution by the Mapper.
     */
    public class UpdateMapperStatementCreator implements PreparedStatementCreator
    {
        protected Map<String,Object> args;
        protected StringTemplate queryTemplate;
        protected Map<String,Object> keys;

        public UpdateMapperStatementCreator( StringTemplate queryTemplate, Map<String,Object> args, Map<String,Object> keys )
        {
            this.args = args;
            this.queryTemplate = queryTemplate;
            this.keys = keys;
        }

        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException
        {
            // substitute stringTemplate values

            // apply template
            for( Map.Entry<String, Object> entry: args.entrySet() )
            {
                queryTemplate.setAttribute( entry.getKey(), entry.getValue() );
            }

            // also substitute all keys that have been generated thusfar
            queryTemplate.setAttribute( "_keys", keys );

            return connection.prepareStatement( queryTemplate.toString(), Statement.RETURN_GENERATED_KEYS );
        }
    }

    public String[] getQueries() {
        return queries;
    }

    /**
     * The queries will be executed in array order.  Each subsequent query will have access to the keys generated
     * by the preceding queries (if any).
     *
     * @param queries an array of StringTemplate query strings
     */
    public void setQueries(String[] queries) {
        this.queries = queries;
    }

    @Override
    public Element getOptions( Request request )
    {
        Element rval = documentFactory.createElement( "update" );
        Element fieldOptions = super.getOptions( request );
        if( fieldOptions != null )
        {
            rval.add( fieldOptions );
        }
        return rval;
    }

}
