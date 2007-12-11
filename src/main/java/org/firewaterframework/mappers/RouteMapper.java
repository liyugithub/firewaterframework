package org.firewaterframework.mappers;

import org.firewaterframework.rest.*;
import org.firewaterframework.mappers.Mapper;
import org.firewaterframework.WSException;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: May 11, 2007
 * Time: 10:48:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class RouteMapper implements Mapper
{
    protected Map<String, Mapper> urlMap;
    protected ParseNode parseTree;

    public Response handle( Request request )
    {
        // match the incoming URL against the parseTree
        ParseResult result = parseTree.find( request.getBaseUrl() );
        if( result == null )
        {
            return new Response( Status.STATUS_NOT_FOUND );
        }

        if( result.getRestArguments() != null )
        {
            request.getArgs().putAll( result.getRestArguments() );
        }
        Response restResponse = result.getMapper().handle( request );
        return restResponse;
    }

    public Map<String, Mapper> getUrlMap()
    {
        return urlMap;
    }

    @Required
    public void setUrlMap(Map<String, Mapper> urlMap)
    {
        this.urlMap = urlMap;

        // we 'compile' the urlMap into a ParseNode tree here
        this.parseTree = ParseNode.compile( urlMap );
    }

    public static class ParseNode
    {
        protected Mapper mapper;
        protected String variable;
        protected ParseNode variableChild;
        protected Map<String,ParseNode> children;

        public static ParseNode compile( Map<String, Mapper> urlMap )
        {
            ParseNode rval = new ParseNode();

            // create a top level ParseNode for every object in the list
            for( Map.Entry<String, Mapper> entry: urlMap.entrySet() )
            {
                String urlPattern = entry.getKey();
                Mapper disp = entry.getValue();

                // split out the url pattern - build the parse tree - note we need to skip the beginning '/' character
                String subUrlPattern = urlPattern.substring( 1 );
                String[] frags = subUrlPattern.split( "/" );
                ParseNode currentNode = rval;
                for( String frag: frags )
                {
                    // handle the special case where we want a 'variable' parse node (ie. a variable in the url pattern)
                    if( frag.startsWith( "{" ))
                    {
                        if( frag.endsWith( "}") && frag.length() > 2 )
                        {
                            currentNode = currentNode.getOrCreateVariableChild( frag.substring( 1, frag.length() - 1 ));
                        }
                        else
                        {
                            // fail fast - this is an illegal pattern and should stop Spring initialization
                            throw new WSException( "Illegal variable expression URL REST pattern in: " + urlPattern );
                        }
                    }
                    else
                    {
                        // normal case - add or return the child node
                        currentNode = currentNode.getOrCreateChild( frag );
                    }
                }
                currentNode.setMapper( disp );
            }
            return rval;
        }

        public ParseNode( )
        {
            this.children = new HashMap<String,ParseNode>();
        }

        /**
         * The guts of the ParseNode - find the node that matches the url, building the argument symbol table along the way
         *
         * @param url
         * @return
         */
        public ParseResult find( String url )
        {
            ParseResult rval = new ParseResult();

            // split the URL - note that the URL should always have a beginning '/' - so skip over it...
            assert url != null && url.indexOf( '/' ) == 0;
            String subUrl = url.substring( 1 );
            String[] frags = subUrl.split( "/" );
            ParseNode currentNode = this;
            for( String frag: frags )
            {
                // first try to find the frag in the children - prefer normal children over variable children
                ParseNode matchingChild = currentNode.children.get( frag );
                if( matchingChild == null )
                {
                    // now check for variable child
                    if( currentNode.variableChild != null )
                    {
                        rval.addArg( currentNode.variableChild.getVariable(), frag );
                        currentNode = currentNode.variableChild;
                    }
                    else
                    {
                        return null; // non matching case - the URL is a 404 against our list of REST urls
                    }
                }
                else
                {
                    currentNode = matchingChild;
                }
            }
            rval.setMapper( currentNode.getMapper() );
            return rval;
        }

        public ParseNode getOrCreateChild( String key )
        {
            // if a parse node doesn't exist for this frag, create one
            ParseNode rval = children.get( key );
            if( rval == null )
            {
                rval = new ParseNode();
                children.put( key, rval );
            }
            return rval;
        }

        public ParseNode getOrCreateVariableChild( String variable )
        {
            if( variableChild != null )
            {
                if( variableChild.getVariable().equals( variable ))
                {
                    return variableChild;
                }
                else
                {
                    throw new WSException( "Illegal URL REST pattern detected.  Variable expressions must match.  Found unmatching variables: " +
                        variable + " and " + variableChild.getVariable() );
                }
            }
            ParseNode rval = new ParseNode( );
            rval.setVariable( variable );
            variableChild = rval;
            return rval;
        }

        public Mapper getMapper() {
            return mapper;
        }

        public void setMapper(Mapper mapper) {
            this.mapper = mapper;
        }

        public String getVariable() {
            return variable;
        }

        public void setVariable(String variable) {
            this.variable = variable;
        }

        public Map<String, ParseNode> getChildren() {
            return children;
        }

        public void setChildren(Map<String, ParseNode> children) {
            this.children = children;
        }
    }

    public static class ParseResult
    {
        protected Mapper mapper;
        protected Map<String,Object> restArguments;

        public Mapper getMapper() {
            return mapper;
        }

        public void setMapper( Mapper mapper ) {
            this.mapper = mapper;
        }

        public Map<String, Object> getRestArguments() {
            return restArguments;
        }

        public void addArg( String key, Object value )
        {
            if( restArguments == null )
            {
                restArguments = new HashMap<String,Object>();
            }
            restArguments.put( key, value );
        }
    }
}
