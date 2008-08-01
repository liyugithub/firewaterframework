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
import org.dom4j.Document;
import org.dom4j.Element;
import org.firewaterframework.WSException;
import org.firewaterframework.rest.*;
import org.springframework.beans.factory.annotation.Required;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This Mapper will delegate incoming REST Requests to downstream Mappers based on the
 * baseUrl of Request.  It is typically the top Mapper in the chain.  It's purpose is
 * to parse the Request URL, building a symbol table of arguments contained in the URL
 * and to dispatch the Request to another Mapper for handling the actual Request.
 * <p>
 * A Spring configuration file for creating a top level RouteMapper for handling REST
 * Requests for a simple Pet Store may look like:
 * <code>
 *      <bean id="urlMap" class="java.util.HashMap">
 *       <constructor-arg index="0">
 *           <map>
 *               <entry key="/users" value-ref="usersMapper"/>
 *               <entry key="/users/{userID}" value-ref="userMapper"/>
 *               <entry key="/users/{userID}/pets" value-ref="userPetsMapper"/>
 *               <entry key="/pets" value-ref="petsMapper"/>
 *               <entry key="/pets/{petID}" value-ref="petMapper"/>
 *           </map>
 *       </constructor-arg>
 *   </bean>
 * </code>
 * <p>
 * Here we have a RouteMapper that handles five URL patterns.  If, for example, the
 * RouteMapper were requested to handle an incoming URL pattern like /users/123, it
 * would parse the '123' out of the request, assigning it to the Request object's
 * arguments Map (as userID=123), and delegate the Request onto the userMapper bean.
 * <p>
 * It is important to realize that the variables inside of the URL patterns must be
 * consistent with each other in name.  An error would be raised if the 'userPetsMapper'
 * pattern above contained the URL pattern "/users/{myUserId}/pets".
 * 
 * @author Tim Spurway
 */
public class RouteMapper extends Mapper
{
    protected static final Log log = LogFactory.getLog( RouteMapper.class );
    /**
     * A Map keyed by URL patterns who's values are the delegate Mapper objects to
     * handle matching incoming Request URLs
     */
    protected Map<String, Mapper> urlMap;

    /**
     * All URL patterns are combined together into a cached tree structure for efficient
     * lookup.
     */
    protected ParseNode parseTree;

    /**
     * Process incoming URLs, and delegate them to downstream Mappers based on pattern
     * matching the URL against the stored urlMap.
     * <p>
     * In addition, all OPTIONS requests are handled by this mapper.  A special
     * OPTIONS request on the root '/' of the web service heirarchy indicates to
     * get all of the meta-data for the entire dictionary.
     *
     * @param request the REST Request
     * @return the Response
     */
    public Response handle( Request request )
    {

        // handle OPTIONS on the root RouteMapper (me)
        if( request.getUrl() == null || request.getUrl().equals( "/" ) )
        {
            if( request.getMethod() == Method.OPTIONS )
            {
                return doOptions( request, this );
            }
            throw new WSException( "Can't access root web service", Status.STATUS_NOT_FOUND );
        }
        
        // match the incoming URL against the parseTree
        ParseResult result = parseTree.find( request.getBaseUrl() );
        if( result == null || result.getMapper() == null )
        {
            throw new WSException( "URL: " + request.getBaseUrl() + " not found.", Status.STATUS_NOT_FOUND );
        }

        if( result.getRestArguments() != null )
        {
            request.getArgs().addPropertyValues( result.getRestArguments() );
        }

        // we need to handle a request for OPTIONS as a special case
        Response restResponse;
        if( request.getMethod() == Method.OPTIONS )
        {
            restResponse = doOptions( request, result.getMapper() );
        }
        else
        {
            restResponse = result.getMapper().handle( request );
        }
        return restResponse;
    }

    /**
     * Handle the special case for an OPTIONS request.
     *
     * @param request the incoming OPTIONS Request
     * @param mapper
     * @return the handled Response
     */
    protected Response doOptions( Request request, Mapper mapper )
    {
        //TODO: these can be cached as well
        Document doc = documentFactory.createDocument();
        Element root = doc.addElement( "options" );
        root.add( mapper.getOptions( request ));
        DocumentResponse rval = new DocumentResponse( MIMEType.application_xml );
        rval.setDocument( doc );
        return rval;
    }

    @Override
    public Element getOptions( Request request )
    {
        Element rval = documentFactory.createElement( "routes" );
        for( Map.Entry<String,Mapper> entry: urlMap.entrySet() )
        {
            Element route = entry.getValue().getOptions( request );
            route.addAttribute( "url", entry.getKey() );
            rval.add( route );
        }
        return rval;
    }

    /**
     * @return the Map containing URL patterns and their associated Mapper
     */
    public Map<String, Mapper> getUrlMap()
    {
        return urlMap;
    }

    /**
     * set the urlMap, and compile the parseTree based on patterns contained in the map
     * @param urlMap
     */
    @Required
    public void setUrlMap(Map<String, Mapper> urlMap)
    {
        this.urlMap = urlMap;

        // we 'compile' the urlMap into a ParseNode tree here
        this.parseTree = ParseNode.compile( urlMap );
    }

    /**
     * An internal tree representation of the urlMap to provide rapid pattern matching
     * for incoming REST Request URLs
     */
    protected static class ParseNode
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
         * @param url the baseUrl of the Request
         * @return the symbol table of URL arguments and the corresponding Mapper to handle the Request
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

    /**
     * An aggregate return class for wrapping the resultant Mapper that can handle the
     * incoming URL and the arguments that were collected from the incoming pattern.
     */
    protected static class ParseResult
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
