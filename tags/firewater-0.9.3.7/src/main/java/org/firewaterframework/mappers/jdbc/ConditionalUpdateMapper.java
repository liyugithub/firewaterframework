package org.firewaterframework.mappers.jdbc;

import org.firewaterframework.rest.Response;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Status;
import org.firewaterframework.mappers.validation.MapPropertyEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.antlr.stringtemplate.StringTemplate;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: May 6, 2009
 * Time: 4:14:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConditionalUpdateMapper extends JDBCMapper
{
    protected static final Logger log = LoggerFactory.getLogger( ConditionalUpdateMapper.class );
    protected static final String conditionalCountQuery = "select count(*) from ($conditionalQuery$) as q";

    protected String conditionQuery;
    protected UpdateMapper existsMapper = new UpdateMapper();
    protected UpdateMapper noneMapper = new UpdateMapper();

    @Override
    public void setDataSource(DataSource ds) {
        super.setDataSource(ds);    //To change body of overridden methods use File | Settings | File Templates.
        existsMapper.setDataSource( ds );
        noneMapper.setDataSource( ds );
    }

    @Override
    public void setFields(Map<String, MapPropertyEditor> fields) {
        super.setFields(fields);    //To change body of overridden methods use File | Settings | File Templates.
        existsMapper.setFields( fields );
        noneMapper.setFields( fields );
    }

    public void setExistsQueries( QueryHolder[] queries )
    {
        existsMapper.setQueries( queries );
    }

    public void setNoneQueries( QueryHolder[] queries )
    {
        noneMapper.setQueries( queries );
    }

    /**
     * This mapper will conditionally execute one of two UpdateMappers based on the row count of a supplied 'conditional' query.
     * It is typically used for 'update or insert' type functionality in a portable manner.
     *
     * @param request the REST request to process
     * @return
     */
    @Transactional( readOnly=false,isolation= Isolation.READ_COMMITTED )
    public Response handle(Request request)
    {
        // set up and execute the conditional query
        StringTemplate queryTemplate = new StringTemplate( conditionQuery );
        Map<String,Object> translatedArgs = bind( request );

        for( Map.Entry<String,Object> entry: translatedArgs.entrySet() )
        {
            queryTemplate.setAttribute( entry.getKey(), entry.getValue() );
        }

        // set up the query to count all rows returned
        String baseQuery = queryTemplate.toString();
        StringTemplate pageCountTemplate = new StringTemplate( conditionalCountQuery );
        pageCountTemplate.setAttribute( "conditionalQuery", baseQuery );

        String conditionCountQueryString = pageCountTemplate.toString();
        log.debug( "Executing conditional update query: " + conditionCountQueryString );
        int numberOfRows = template.queryForInt( conditionCountQueryString );

        // decide on execution of the 'exists' or 'none' mappers
        if( numberOfRows > 0 )
        {
            if( existsMapper.getQueries() != null )
                return existsMapper.handle( request );
        }
        else if( noneMapper.getQueries() != null )
        {
            return noneMapper.handle( request );
        }
        return new Response( Status.STATUS_OK );

    }

    public String getConditionQuery() {
        return conditionQuery;
    }

    public void setConditionQuery(String conditionQuery) {
        this.conditionQuery = conditionQuery;
    }

    public UpdateMapper getExistsMapper() {
        return existsMapper;
    }

    public void setExistsMapper(UpdateMapper existsMapper) {
        this.existsMapper = existsMapper;
    }

    public UpdateMapper getNoneMapper() {
        return noneMapper;
    }

    public void setNoneMapper(UpdateMapper noneMapper) {
        this.noneMapper = noneMapper;
    }
}
