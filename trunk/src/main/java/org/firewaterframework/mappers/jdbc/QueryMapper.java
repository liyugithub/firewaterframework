package org.firewaterframework.mappers.jdbc;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.firewaterframework.WSException;
import org.firewaterframework.rest.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Map;

public class QueryMapper extends JDBCMapper
{
    protected static final Log log = LogFactory.getLog( QueryMapper.class );

    protected String query;
    protected RowTreeBuilder rowTreeMapper;

    @Transactional( readOnly=true, isolation=Isolation.READ_COMMITTED )
    public Response handle( Request request )
    {
        StringTemplate queryTemplate = new StringTemplate( query );

        // bind the Request args before applying to template
        DataBinder dataBinder = getDataBinder();
        dataBinder.bind( request.getArgs() );
        if( dataBinder.getBindingResult().getErrorCount() > 0 )
        {
            StringBuffer errors = new StringBuffer( "Request Parameter error: " );
            for( ObjectError error: (List<ObjectError>)dataBinder.getBindingResult().getAllErrors() )
            {
                errors.append( error.toString() ).append( ',' );
            }
            throw new WSException( errors.toString(), Status.STATUS_SERVER_ERROR );
        }

        // apply template
        queryTemplate.setAttributes( (Map)dataBinder.getBindingResult().getTarget() );

        List<Map<String,Object>> rows;
        try
        {
            rows = template.queryForList( queryTemplate.toString() );
            Document resultDOM = rowTreeMapper.process( rows );
            DocumentResponse response = new DocumentResponse( Status.STATUS_OK, MIMEType.application_xml );
            response.setDocument( resultDOM );
            return response;
        }
        catch( WSException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            // this is categorized as a 500
            throw new WSException( "Caught error executing SQL statement", e );
        }
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

    public RowTreeBuilder getRowTreeMapper() {
        return rowTreeMapper;
    }

    @Required
    public void setRowTreeMapper(RowTreeBuilder rowTreeMapper) {
        this.rowTreeMapper = rowTreeMapper;
    }

}
