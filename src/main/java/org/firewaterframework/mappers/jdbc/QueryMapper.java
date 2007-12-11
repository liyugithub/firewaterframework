package org.firewaterframework.mappers.jdbc;

import org.firewaterframework.WSException;
import org.firewaterframework.mappers.Mapper;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Response;
import org.firewaterframework.rest.Status;
import org.firewaterframework.rest.MIMEType;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class QueryMapper implements Mapper
{
    protected static final Log log = LogFactory.getLog( QueryMapper.class );

    private JdbcTemplate template;
    protected String query;
    protected PivotNode pivotMap;

    @Transactional( readOnly=true, isolation=Isolation.READ_COMMITTED )
    public Response handle( Request request )
    {
        StringTemplate queryTemplate = new StringTemplate( query );
        for (Map.Entry<String,Object> arg : request.getArgs().entrySet() )
        {
            // apply each request arg to the string template
            queryTemplate.setAttribute( arg.getKey(), arg.getValue() );
        }

        List<Map<String,Object>> rows = null;
        try
        {
            rows = template.queryForList( queryTemplate.toString() );
            Document resultDOM = pivotMap.process( rows );
            Response response = new Response( Status.STATUS_OK, MIMEType.application_xml );
            response.setStatus( Status.STATUS_OK );
            response.setContent( resultDOM );
            return response;
        }
        catch( Exception e )
        {
            // this is categorized as a 500
            throw new WSException( "Caught error executing SQL statement", e );
        }
    }

    @Required
    public void setDataSource( DataSource ds )
    {
        template = new JdbcTemplate( ds );
    }

    public String getQuery()
    {
        return query;
    }

    @Required
    public void setQuery(String query)
    {
        this.query = query;
    }

    public PivotNode getPivotMap() {
        return pivotMap;
    }

    @Required
    public void setPivotMap(PivotNode pivotMap) {
        this.pivotMap = pivotMap;
    }

}
