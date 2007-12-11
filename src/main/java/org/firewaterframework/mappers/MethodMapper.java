package org.firewaterframework.mappers;

import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.base.NeedsRefreshException;
import org.firewaterframework.rest.*;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Nov 28, 2007
 * Time: 10:29:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class MethodMapper implements Mapper
{
    // these settings control the OSCache
    protected GeneralCacheAdministrator cache;
    protected String[] cacheGroups;
    protected EntryRefreshPolicy entryRefreshPolicy;

    // mappers do the heavy lifting, actually performing the data operations on back-end stores
    protected Mapper getMapper;
    protected Mapper putMapper;
    protected Mapper postMapper;
    protected Mapper deleteMapper;

    public Response handle( Request request )
    {
        Response rval = null;
        if( request.getMethod() == Method.GET )
        {
            if( cache != null )
            {
                // note that the entries are cached using the full URL of the request
                try
                {
                    rval = (Response)cache.getFromCache( request.getUrl() );
                }
                catch( NeedsRefreshException ex )
                {
                    // we  need to fetch the resource and cache it
                    rval = doGet( request );
                    cache.putInCache( request.getUrl(), rval, cacheGroups, entryRefreshPolicy );
                }
            }
            else
            {
                rval = doGet( request );
            }
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
        else
        {
            return getMethodNotAllowedResponse( request );
        }

        // note that the POST, PUT and DELETE will flush the entry and groups for this URL
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
        return rval;
    }

    protected Response doGet( Request request )
    {
        if( getMapper != null )
        {
            return getMapper.handle( request );
        }
        return getMethodNotAllowedResponse( request );
    }

    protected Response doPost( Request request )
    {
        if( postMapper != null )
        {
            return postMapper.handle( request );
        }
        return getMethodNotAllowedResponse( request );
    }

    protected Response doPut( Request request )
    {
        if( putMapper != null )
        {
            return putMapper.handle( request );
        }
        return getMethodNotAllowedResponse( request );
    }

    protected Response doDelete( Request request )
    {
        if( deleteMapper != null )
        {
            return deleteMapper.handle( request );
        }
        return getMethodNotAllowedResponse( request );
    }

    protected Response getMethodNotAllowedResponse( Request request )
    {
        return new Response( Status.STATUS_METHOD_NOT_ALLOWED );
    }

    public GeneralCacheAdministrator getCache() {
        return cache;
    }

    public void setCache(GeneralCacheAdministrator cache) {
        this.cache = cache;
    }

    public String[] getCacheGroups() {
        return cacheGroups;
    }

    public void setCacheGroups(String[] cacheGroups) {
        this.cacheGroups = cacheGroups;
    }

    public EntryRefreshPolicy getEntryRefreshPolicy() {
        return entryRefreshPolicy;
    }

    public void setEntryRefreshPolicy(EntryRefreshPolicy entryRefreshPolicy) {
        this.entryRefreshPolicy = entryRefreshPolicy;
    }

    public Mapper getGetMapper() {
        return getMapper;
    }

    public void setGetMapper(Mapper getMapper) {
        this.getMapper = getMapper;
    }

    public Mapper getPutMapper() {
        return putMapper;
    }

    public void setPutMapper(Mapper putMapper) {
        this.putMapper = putMapper;
    }

    public Mapper getPostMapper() {
        return postMapper;
    }

    public void setPostMapper(Mapper postMapper) {
        this.postMapper = postMapper;
    }

    public Mapper getDeleteMapper() {
        return deleteMapper;
    }

    public void setDeleteMapper(Mapper deleteMapper) {
        this.deleteMapper = deleteMapper;
    }
}
