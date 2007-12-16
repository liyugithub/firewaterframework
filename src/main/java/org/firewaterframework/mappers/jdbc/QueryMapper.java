package org.firewaterframework.mappers.jdbc;

import org.antlr.stringtemplate.StringTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
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
    protected int defaultPageSize = 0; // paging is off by default
    protected int pageWindowSize = -1;
    protected String pageCountQuery = "select max( q.row_num ) from ($query$) as q";
    protected String pageTokenFragment = "limit $page_size$ offset $low_row$";

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

        // apply templates
        Map<String,Object> translatedArgs = (Map<String,Object>)dataBinder.getBindingResult().getTarget();
        queryTemplate.setAttributes( translatedArgs );

        // set up the query to count all rows returned
        String baseQuery = queryTemplate.toString();
        StringTemplate pageCountTemplate = new StringTemplate( pageCountQuery );
        pageCountTemplate.setAttribute( "query", baseQuery );
        String pageCountQueryString = pageCountTemplate.toString();

        int pageNum = getPageNum( translatedArgs );
        int pageSize = getPageSize( translatedArgs );
        if( defaultPageSize > 0 )
        {
            StringTemplate pageTokenFragmentTemplate = new StringTemplate( pageTokenFragment );
            int low_row = (pageNum - 1) * pageSize + 1;

            pageTokenFragmentTemplate.setAttribute( "low_row", low_row );
            pageTokenFragmentTemplate.setAttribute( "page_size", pageSize );
            queryTemplate.setAttribute( "page_token", pageTokenFragmentTemplate.toString() );
        }

        //String realQuery = getPagingQuery( baseQuery, pageNum, pageSize );

        List<Map<String,Object>> rows;
        try
        {
            rows = template.queryForList( queryTemplate.toString() );
            Document resultDOM = rowTreeMapper.process( rows );

            // add paging tags, if required
            addPagingTags( resultDOM, pageCountQueryString, pageNum, pageSize, request.getUrl() );
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

    /*protected String getPagingQuery( String baseQuery, int pageNum, int pageSize )
    {
        String rval = baseQuery;
        if( defaultPageSize > 0 )
        {
            // set the query to the paging query
            StringTemplate pageTemplate = new StringTemplate( pageQuery );

            int low_row = (pageNum - 1) * pageSize + 1;
            int high_row = (low_row + pageSize) - 1;

            pageTemplate.setAttribute( "query", baseQuery );
            pageTemplate.setAttribute( "low_row", low_row );
            pageTemplate.setAttribute( "high_row", high_row );
            rval = pageTemplate.toString();
        }
        return rval;
    }*/

    protected String buildPageURL( int pageNum, String resourceURL )
    {
        StringBuffer rval = new StringBuffer( resourceURL );
        if( resourceURL.indexOf( '?' ) > -1 )
        {
            rval.append( "%26" );
        }
        else
        {
            rval.append( '?' );
        }
        rval.append( "page_num" ).append( '=' ).append( pageNum );
        return rval.toString();
    }

    protected void addPagingTags( Document resultDOM, String pageCountQueryString,
                                  Integer pageNum, Integer pageSize, String resourceURL )
    {
        if( defaultPageSize > 0 )
        {
            Integer totalRows = template.queryForInt( pageCountQueryString );
            Integer pageCount = (totalRows / pageSize) + ((totalRows % pageSize > 0) ? 1 : 0);
            Element pages = resultDOM.getRootElement().addElement( "pages" );

            pages.addAttribute( "num_pages", pageCount.toString() );
            pages.addAttribute( "page_number", pageNum.toString() );
            pages.addAttribute( "page_size", pageSize.toString() );

            // add the URL of the current page
            pages.addAttribute( "url", buildPageURL( pageNum, resourceURL ));

            // add the prev and next page links
            if( pageNum < pageCount )
            {
                pages.addAttribute( "next_page", buildPageURL( pageNum + 1, resourceURL ));
            }
            if( pageNum > 1 )
            {
                pages.addAttribute( "prev_page", buildPageURL( pageNum - 1, resourceURL ));
            }

            // now, add a <page> tag for REST links for each of the page that exist.
            int pageWindowStart = 1;
            int pageWindowEnd = pageCount;

            // now do the page 'window' - try to center the current page in the window
            if( pageWindowSize != -1 )
            {
                int tryStart = pageNum - (pageWindowSize / 2);
                if( tryStart < 1 ) tryStart = 1;

                int tryEnd = (tryStart + pageWindowSize) - 1;
                if( tryEnd > pageCount )
                {
                    tryEnd = pageCount;
                    tryStart = (pageCount - pageWindowSize) + 1;
                    if( tryStart < 1 ) tryStart = 1;
                }
                pageWindowStart = tryStart;
                pageWindowEnd = tryEnd;
            }

            for( Integer i = pageWindowStart; i <= pageWindowEnd; ++i )
            {
                Element page = pages.addElement( "page" );
                page.addAttribute( "url", buildPageURL( i, resourceURL ));
                page.addAttribute ( "page_number", i.toString() );

                // add the 'current' page flag - this makes it easy to add conditionals in page templates, etc
                if( i.intValue() == pageNum.intValue() )
                {
                    page.addAttribute( "current_page", "true" );
                }
            }
        }
    }

    protected Integer getPageNum( Map argMap )
    {
        Integer pageNum = 1;
        try { pageNum = Integer.valueOf((String)argMap.get( "page_num" )); }
        catch( Exception e ){};

        return pageNum;
    }

    protected Integer getPageSize( Map argMap )
    {
        Integer pageSize = defaultPageSize;
        try { pageSize = Integer.valueOf((String)argMap.get( "page_size" )); }
        catch( Exception e ){};

        return pageSize;
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

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    public int getPageWindowSize() {
        return pageWindowSize;
    }

    public void setPageWindowSize(int pageWindowSize) {
        this.pageWindowSize = pageWindowSize;
    }

    public String getPageCountQuery() {
        return pageCountQuery;
    }

    public void setPageCountQuery(String pageCountQuery) {
        this.pageCountQuery = pageCountQuery;
    }
}
