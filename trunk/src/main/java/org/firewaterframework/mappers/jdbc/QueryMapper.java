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

import java.util.List;
import java.util.Map;

/**
 * This class is responsible for servicing GET Rest requests against a back-end relational
 * database (through JDBC).  It is responsible for executing a configured query template and returning
 * it as a REST Response.  Note that currently this Mapper (and all JDBCMappers) return only
 * {@link DocumentResponse}s - which means that the only response type when using these
 * Mappers are XML documents.
 *
 * @author Tim Spurway
 * @see PivotTreeBuilder
 */
public class QueryMapper extends JDBCMapper
{
    protected static final Log log = LogFactory.getLog( QueryMapper.class );
    protected static final String pageCountQuery = "select count(distinct q.$pivot_column$) from ($query$) as q";

    protected String query;
    protected PivotTreeBuilder pivotTreeBuilder;
    protected Integer defaultPageSize = 0; // paging is off by default
    protected Integer pageWindowSize = 10;
    protected String pageCountPivot;
    protected String pageTokenFragment = "limit $page_size$ offset $low_row$";

    /**
     * Handle a REST Request by querying a back-end relational database.
     *
     * Before the query is executed, the Request arguments are 'bound' to an
     * intermediate Map.  This gives the opportunity to do validation and
     * type conversions.
     * @see org.firewaterframework.mappers.validation.MapDataBinder
     * @param request incoming REST GET Request
     * @return a DocumentResponse with the XML document of the result set
     */
    @Transactional( readOnly=true, isolation=Isolation.READ_COMMITTED )
    public Response handle( Request request )
    {
        StringTemplate queryTemplate = new StringTemplate( query );

        Map<String,Object> translatedArgs = bind( request );

        for( Map.Entry<String,Object> entry: translatedArgs.entrySet() )
        {
            queryTemplate.setAttribute( entry.getKey(), entry.getValue() );
        }
        
        // set up the query to count all rows returned
        String baseQuery = queryTemplate.toString();
        StringTemplate pageCountTemplate = new StringTemplate( pageCountQuery );
        pageCountTemplate.setAttribute( "query", baseQuery );
        pageCountTemplate.setAttribute( "pivot_column", getPageCountPivot() );
        String pageCountQueryString = pageCountTemplate.toString();

        int pageNum = getPageNum( translatedArgs );
        int pageSize = getPageSize( translatedArgs );
        if( defaultPageSize > 0 )
        {
            StringTemplate pageTokenFragmentTemplate = new StringTemplate( pageTokenFragment );
            int low_row = (pageNum - 1) * pageSize;

            pageTokenFragmentTemplate.setAttribute( "low_row", low_row );
            pageTokenFragmentTemplate.setAttribute( "page_size", pageSize );
            queryTemplate.setAttribute( "page_token", pageTokenFragmentTemplate.toString() );
        }

        List<Map<String,Object>> rows;
        try
        {
            rows = template.queryForList( queryTemplate.toString() );
            Document resultDOM = pivotTreeBuilder.process( rows );

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

    /**
     *
     * @param pageNum
     * @param resourceURL
     * @return
     */
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
            Integer totalRows = 0;
            Element pages = resultDOM.getRootElement().addElement( "pages" );
            pages.addAttribute( "page_number", pageNum.toString() );
            pages.addAttribute( "page_size", pageSize.toString() );
            // add the URL of the current page
            pages.addAttribute( "url", buildPageURL( pageNum, resourceURL ));
            pages.addAttribute( "next_page", buildPageURL( pageNum + 1, resourceURL ));
            pages.addAttribute( "current_page", buildPageURL( pageNum, resourceURL ));
            if( pageNum > 1 )
            {
                pages.addAttribute( "prev_page", buildPageURL( pageNum - 1, resourceURL ));
            }

            // if we fail adding the <page> tag (ie. the count query craps out), don't sweat it,
            // catch and log the error, and continue
            try
            {
                totalRows = template.queryForInt( pageCountQueryString );
                Integer pageCount = (totalRows / pageSize) + ((totalRows % pageSize > 0) ? 1 : 0);

                // don't bother adding the pages tag if the pageCount isn't > 1
                if( pageCount <= 1 )
                {
                    resultDOM.getRootElement().remove( pages );
                    return;
                }
                
                pages.addAttribute( "num_pages", pageCount.toString() );
                pages.addAttribute( "num_rows", totalRows.toString() );

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
                }
            }
            catch( Exception e)
            {
                log.warn( "Couldn't generate page tags for URL request: " + resourceURL, e );
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

    /**
     * The QueryMapper uses the <a href="http://www.stringtemplate.org/">StringTemplate</a> template
     * engine to assemble the SQL queries for execution.  The Request arguments are made available
     * to the template engine when processing the SQL templates, as well as other configuration
     * parameters.  This allows for very flexible and powerful SQL processing, without resorting
     * to SQL generation.  It is easy to set up filtering
     * and sorting using a combination of Request arguments and StringTemplated queries.  For
     * example, assume we have the REST URL "/users?sort=city&region=14", then in our SQL template we can
     * access the argument using a StringTemplate variable:
     * <p>
     * <code><pre>
     *        SELECT u.id as user, u.city, u.state, u.zip
     *        FROM user u
     *        WHERE u.region_id = $region$
     *        ORDER BY $sort$
     * </pre></code>
     *
     * <p>
     * StringTemplate offers rich collection primitives and basic conditionals.  The problem
     * with the above query is if the Request arguments 'sort' and 'region' aren't supplied,
     * we would get a runtime SQL exception.  Use StringTemplate to provide default values:
     *
     * <p>
     * <code><pre>
     *        SELECT u.id as user, u.city, u.state, u.zip
     *        FROM user u
     *        $if(region)$
     *          WHERE u.region_id = $region$
     *        $endif$
     *        ORDER BY
     *          $if(sort)$
     *              u.$sort$
     *          $else$
     *              u.id
     *          $endif$
     * </pre></code>
     *
     * @param query the source for the StringTemplate query for fetching result sets for this mapper
     */
    @Required
    public void setQuery(String query)
    {
        this.query = query;
    }

    public PivotTreeBuilder getPivotTreeBuilder() {
        return pivotTreeBuilder;
    }

    /**
     * <p>
     * This class collaborates with the PivotTreeBuilder to construct an XML document based
     * on the result set of the query.  Because of the flat nature of SQL result sets, and the
     * typically desired heirarchical XML document Response, this class expects that it's query
     * will return a 'pivotTree' style response.  This means that when writing queries for
     * this Mapper, you need to select all properties for all joined tables and order the
     * result set based on the 'pivot' columns (ie. the id columns) to reflect the tree-structure
     * of the resultant XML document.  This sounds complicated, but it's easier to explain with
     * an example :)
     * <p>
     * Assume you have the following Tables in your database:
     * <p>
     * <table border="1">
     * <caption>Owner Table</caption>
     * <tr><td><b>id</b><td><b>name</b><td><b>city</b></td><td><b>state</b></td><td><b>zip</b></td></tr>
     * <tr><td>1</td><td>Tim Spurway</td><td>New York</td><td>NY</td><td>10002</td></tr>
     * <tr><td>2</td><td>Joe Smith</td><td>Los Angeles</td><td>CA</td><td>90210</td></tr>
     * <tr><td>3</td><td>Jane Doe</td><td>New York</td><td>NY</td><td>10012</td></tr>
     * </table>
     *
     * <p>
     * <table border="1">
     * <caption>Pet Table</caption>
     * <tr><td><b>id</b></td><td><b>name</b></td><td><b>owner_id</b></td></tr>
     * <tr><td>1</td><td>Trixie</td><td>1</td></tr>
     * <tr><td>2</td><td>Mixie</td><td>2</td></tr>
     * <tr><td>3</td><td>Spot</td><td>2</td></tr>
     * <tr><td>4</td><td>Herbie</td><td>2</td></tr>
     * </table>
     *
     * <p>
     * A SQL select statement to return all owners and their pets in 'pivotTree' style would
     * look like this:
     * <p>
     * <code><pre>
     * SELECT o.id as owner, o.name as owner_name, o.city, o.state, o.zip, p.id as pet, p.name as pet_name
     * FROM owner o
     * LEFT OUTER JOIN pet p on o.id = p.owner_id
     * ORDER BY o.name
     * </pre></code>
     *
     * <p>
     * The point of using this pivotTree scheme is to fetch all related objects in a single
     * query.  The above query will return, on each row, all information for both owners and
     * their pets.  Notice that we have two 'pivot' columns (owner and pet) in the result set.
     * These columns are used by the {@link org.firewaterframework.mappers.jdbc.PivotTreeBuilder}
     * to build the tree struture.  Also note the 'LEFT OUTER JOIN' on the pet table.  This
     * will ensure that owners with no pets will also be present in the result set.  Also
     * note that we are ordering the result set on the 'owner' pivot column.  The PivotTreeBuilder
     * uses this ordering to know when to create a new XML node for the result document.
     *
     * @param pivotTreeBuilder
     */
    @Required
    public void setPivotTreeBuilder(PivotTreeBuilder pivotTreeBuilder) {
        this.pivotTreeBuilder = pivotTreeBuilder;
    }

    public int getDefaultPageSize() {
        return defaultPageSize;
    }
    /**
     * This class handles paging to allow for handling of large result sets.  This feature will
     * print a set of XML tags at the end of the Response that will 'point' to other pages in
     * the result set.  To enable this feature, set the defaultPageSize of the Mapper to a value
     * greater than zero, then append the special template variable $page_token$ to the pivot
     * dimension that you want to page on.  For example, in our query above, if we wanted to
     * limit the paging to 10 users per page, we would set the defaultPageSize of our Mapper
     * to 10, then modify the query in the following manner:
     *
     * <p>
     * <code><pre>
     * SELECT o.id as owner, o.name as owner_name, o.city, o.state, o.zip, p.id as pet, p.name as pet_name
     * FROM (SELECT id, name, city, state, zip FROM owner $page_token$) as o
     * LEFT OUTER JOIN pet p on o.id = p.owner_id
     * ORDER BY o.id
     * </pre></code>
     *
     * <p>
     * We use a nested SELECT here to indicate that the paging should limit the results on the
     * owner pivot.  The <code>$page_token$</code> is simply translated into a LIMIT statement (which ensures
     * database independence) over a set of page count calculations performed by the Mapper.
     * @param defaultPageSize the number of rows to include in each page of paged output (set to 0 to turn off paging)
     */
    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    public int getPageWindowSize() {
        return pageWindowSize;
    }

    /**
     * The page window size is the number of pages to return in the <pages> XML tag
     * when paging is enabled.  This is to limit the result set size for very large
     * result sets, when even the number of pages is very large.
     * @param pageWindowSize the maximum number of pages allowed in the <pages> tag
     */
    public void setPageWindowSize(int pageWindowSize) {
        this.pageWindowSize = pageWindowSize;
    }

    /**
     *
     * @return the column in the result set to count the total number of pivot results.  Defaults
     * to the pivotAttribute in the top level PivotTreeBuilder of this mapper.
     */
    public String getPageCountPivot()
    {
        if( pageCountPivot == null )
        {
            return pivotTreeBuilder.getResourceDescriptor().getPivotAttribute();
        }
        return pageCountPivot;
    }

    /**
     * This is the column who's distinct elements will be counted to get the total number
     * of elements in the result set.  This is used by the pageCountQuery to calculate the
     * total number of results in the result set so that the <pages> structure can be built
     * properly.  The default value for this property is the pivotAttribute of the mapper's
     * pivotTreeBuilder - which is usually the correct setting for this.
     *
     * @param pageCountPivot the column id in the query result set used for counting the number of
     * pivot objects in the result set.
     */
    public void setPageCountPivot(String pageCountPivot) {
        this.pageCountPivot = pageCountPivot;
    }

    @Override
    public Element getOptions( Request request )
    {
        Element rval = documentFactory.createElement( "get" );
        Element fieldOptions = super.getOptions( request );
        if( fieldOptions != null )
        {
            rval.add( fieldOptions );
        }
        rval.addAttribute( "defaultPageSize", defaultPageSize.toString() );
        rval.addAttribute( "pageWindowSize", pageWindowSize.toString() );
        return rval;
    }
    
}
