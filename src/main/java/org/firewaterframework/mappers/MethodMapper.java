package org.firewaterframework.mappers;
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
import org.firewaterframework.WSException;
import org.firewaterframework.rest.Method;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Response;
import org.firewaterframework.rest.Status;
import org.firewaterframework.rest.representation.Representation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * Responsible for parsing out the REST Method (GET, PUT, POST, etc.) and dispatching to
 * delegate Mappers based on this information.  This class is also responsible for implementing
 * a cacheing scheme based on OSCache.  The reason for putting the cacheing here is that
 * the REST Method implies a cacheing action as follows:
 * <ul>
 * <li>GET,HEAD,OPTIONS - fetch from the cache if possible - otherwise delegate and cache response</li>
 * <li>PUT,POST,DELETE - flush the cache and cache groups for this URL</li>
 * </ul>
 * <p>
 * Note that each mapper can specify the cacheGroups for resources.  This allows Mappers that
 * are related to flush each other on PUT,POST, or DELETE operations to this resource.
 *
 * @author Tim Spurway
 */
public class MethodMapper extends Mapper
{
    protected static final Logger log = LoggerFactory.getLogger( RouteMapper.class );
    public static final String NOCACHE = "__nocache";

    protected GeneralCacheAdministrator cache;
    protected String[] cacheGroups;
    protected EntryRefreshPolicy entryRefreshPolicy;

    // mappers do the heavy lifting, actually performing the data operations on back-end stores
    protected Mapper getMapper;
    protected Mapper putMapper;
    protected Mapper postMapper;
    protected Mapper deleteMapper;

    /**
     * Process the REST Request by delegating to downstream Mappers based on the Request.method
     * attribute.  Note that this method is responsible for flushing the cache.
     *
     * @param request the REST Request
     * @return the processed Response
     */
    public Response handle( Request request )
    {
        Response rval;
        if( request.getMethod() == Method.GET )
        {
            rval = processGet( request );
            
            // conditionally flush cache if NOCACHE is specified
            if( request.getParameter( NOCACHE ) != null ) flushCache( request );
            return rval;
        }
        else if( request.getMethod() == Method.POST )
        {
            rval = doPost( request );
        }
        else if( request.getMethod() == Method.PUT )
        {
            rval = doPut( request );
        }
        else if( request.getMethod() == Method.DELETE )
        {
            rval = doDelete( request );
        }
        else if( request.getMethod() == Method.HEAD )
        {
            rval = processGet( request );
            // head doesn't include the actual content, create a new response excluding it
            return new Response( rval.getStatus() );
        }
        else
        {
            throw new WSException( "Method: " + request.getMethod() + " not allowed.", Status.STATUS_METHOD_NOT_ALLOWED );
        }

        // note that the POST, PUT and DELETE will flush the entry and groups for this URL
        flushCache( request );
        return rval;
    }

    protected void flushCache( Request request )
    {
        if( cache != null )
        {
            cache.flushEntry( request.getUrl() );
            if( cacheGroups != null )
            {
                for( String cacheGroup: cacheGroups )
                {
                    cache.flushGroup( cacheGroup );
                }
            }
        }
    }

    /**
     * Process a GET REST Request.  If a cache is configured for this Mapper, try to fetch
     * the Response from the cache based on the Request baseUrl.  Otherwise, delegate to
     * the getMapper and cache the response.
     *
     * @param request
     * @return
     */
    protected Response processGet( Request request )
    {
        Response rval;
        if( cache != null && request.getParameter( NOCACHE ) == null )
        {
            // note that the entries are cached using the full URL of the request
            try
            {
                rval = (Response)cache.getFromCache( request.getUrl() );
            }
            catch( NeedsRefreshException ex )
            {
                // we  need to fetch the resource and cache it
                try
                {
                    rval = doGet( request );
                    cache.putInCache( request.getUrl(), rval, cacheGroups, entryRefreshPolicy );
                }
                catch( WSException e )
                {
                    log.error( "Caught exception trying to update cache for requestURL: " + request.getUrl(), e );
                    cache.cancelUpdate( request.getUrl() );
                    throw e;
                }
                catch( Exception e )
                {
                    log.error( "Caught exception trying to update cache for requestURL: " + request.getUrl(), e );
                    cache.cancelUpdate( request.getUrl() );
                    throw new WSException( "Caught exception trying to update cache for requestURL: " + request.getUrl(), e );
                }
            }
        }
        else
        {
            rval = doGet( request );
        }
        return rval;
    }

    /**
     * Perform the actual delegation to the getMapper
     *
     * @param request the incoming GET Request
     * @return the handled Response
     */
    protected Response doGet( Request request )
    {
        if( getMapper != null )
        {
            return getMapper.handle( request );
        }
        throw new WSException( "Method: " + request.getMethod() + " not allowed.", Status.STATUS_METHOD_NOT_ALLOWED );
    }

    /**
     * Perform the actual delegation to the postMapper
     *
     * @param request the incoming POST Request
     * @return the handled Response
     */
    protected Response doPost( Request request )
    {
        if( postMapper != null )
        {
            return postMapper.handle( request );
        }
        throw new WSException( "Method: " + request.getMethod() + " not allowed.", Status.STATUS_METHOD_NOT_ALLOWED );
    }

    /**
     * Perform the actual delegation to the putMapper
     *
     * @param request the incoming PUT Request
     * @return the handled Response
     */
    protected Response doPut( Request request )
    {
        if( putMapper != null )
        {
            return putMapper.handle( request );
        }
        throw new WSException( "Method: " + request.getMethod() + " not allowed.", Status.STATUS_METHOD_NOT_ALLOWED );
    }

    /**
     * Perform the actual delegation to the deleteMapper
     *
     * @param request the incoming DELETE Request
     * @return the handled Response
     */
    protected Response doDelete( Request request )
    {
        if( deleteMapper != null )
        {
            return deleteMapper.handle( request );
        }
        throw new WSException( "Method: " + request.getMethod() + " not allowed.", Status.STATUS_METHOD_NOT_ALLOWED );
    }

    /**
     * Process the OPTIONS method.  This will delegate to all of it's method
     * mappers to build the options response.
     *
     * @param request
     * @return
     */
    @Override
    public Representation getOptions( Request request )
    {
        Representation rval = getRepresentation( request );
        rval.setName( "methods" );
        if( getMapper != null )
        {
            rval.addChild( getMapper.getOptions( request ));
        }
        if( putMapper != null )
        {
            Representation element = putMapper.getOptions( request );
            element.setName( "put" );
            rval.addChild( element );
        }
        if( postMapper != null )
        {
            Representation element = postMapper.getOptions( request );
            element.setName( "post" );
            rval.addChild( element );
        }
        if( deleteMapper != null )
        {
            Representation element = deleteMapper.getOptions( request );
            element.setName( "delete" );
            rval.addChild( element );
        }
        return rval;
    }

    /**
     *
     * @return the OSCache administrator for this Mapper
     */
    public GeneralCacheAdministrator getCache() {
        return cache;
    }

    /**
     * Setting a cache for a Mapper will 'turn on' cacheing for Responses emitted from
     * this Mapper.
     *
     * @param cache
     */
    public void setCache(GeneralCacheAdministrator cache) {
        this.cache = cache;
    }

    public String[] getCacheGroups() {
        return cacheGroups;
    }

    /**
     * Setting cacheGroups for a Mapper allows several Mappers to in effect share caches.
     * When a cache is flushed (during a POST,PUT, or DELETE), all cacheGroups associated
     * with this Mapper will also be flushed.
     *
     * @param cacheGroups a collection of cacheGroup names
     * @see com.opensymphony.oscache.base.Cache
     *
     */
    public void setCacheGroups(String[] cacheGroups) {
        this.cacheGroups = cacheGroups;
    }

    public void setCacheGroups(String cacheGroups) {
        this.cacheGroups = cacheGroups.split( "," );
    }

    public EntryRefreshPolicy getEntryRefreshPolicy() {
        return entryRefreshPolicy;
    }

    /**
     * Allows for creating a cache flushing policy based on a time period.  This allows
     * a time based cache flushing policy for those times where external processes may
     * modify the underlying resources without the REST layer knowing about it.
     * @param entryRefreshPolicy a specification of a time for periodic flushing of this
     * Mapper's cache
     */
    public void setEntryRefreshPolicy(EntryRefreshPolicy entryRefreshPolicy) {
        this.entryRefreshPolicy = entryRefreshPolicy;
    }

    public Mapper getGetMapper() {
        return getMapper;
    }

    /**
     * @param getMapper The Mapper that will handle Rest GET Requests on this Mapper
     */
    public void setGetMapper(Mapper getMapper) {
        this.getMapper = getMapper;
    }

    public Mapper getPutMapper() {
        return putMapper;
    }

    /**
     * @param putMapper The Mapper that will handle Rest PUT Requests on this Mapper
     */
    public void setPutMapper(Mapper putMapper) {
        this.putMapper = putMapper;
    }

    public Mapper getPostMapper() {
        return postMapper;
    }

    /**
     * @param postMapper The Mapper that will handle Rest POST Requests on this Mapper
     */
    public void setPostMapper(Mapper postMapper) {
        this.postMapper = postMapper;
    }

    public Mapper getDeleteMapper() {
        return deleteMapper;
    }

    /**
     * @param deleteMapper The Mapper that will handle Rest DELETE Requests on this Mapper
     */
    public void setDeleteMapper(Mapper deleteMapper) {
        this.deleteMapper = deleteMapper;
    }
}
