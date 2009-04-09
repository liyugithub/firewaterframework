package org.firewaterframework.test;
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
import junit.framework.Assert;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.xpath.XPathAPI;
import org.firewaterframework.mappers.RouteMapper;
import org.firewaterframework.rest.Method;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Response;
import org.firewaterframework.rest.Status;
import org.firewaterframework.rest.representation.XMLRepresentation;
import org.firewaterframework.rest.representation.Representation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests Against RouteMapper
 * 
 * @author tspurway
 * @see RouteMapper
 */
public class TestRouteMapper extends Assert
{
    protected static final Logger log = LoggerFactory.getLogger( TestRouteMapper.class );
    private static ApplicationContext appContext;

    @BeforeClass
    public static void init()
    {
        appContext = new ClassPathXmlApplicationContext( "rest-base.xml" );
        BasicDataSource ds = (BasicDataSource)appContext.getBean( "dataSource" );
        JdbcTemplate template = new JdbcTemplate( ds );
        String sql =   "create table user(id int auto_increment primary key, first_name varchar(255), last_name varchar(255)," +
                "city varchar(255), state char(2), email varchar(255), password varchar(255), zip int)";
        template.execute( sql );

        sql = "create table user_role( user_id int, role_id int )";
        template.execute( sql );

        sql = "create table role( id int, name varchar(64) )";
        template.execute( sql );

        sql = "create table pet(id int auto_increment primary key, name varchar(255), species_id int, owner_id int)";
        template.execute( sql );

        sql = "create table species(id int auto_increment primary key, name varchar(255))";
        template.execute( sql );

        sql = "insert into user( id, first_name, last_name, city, state, email, password, zip ) " +
                "values( 0, 'joe', 'who', 'new york', 'NY', 'joe@who.com', 'yahoo', 10012 )," +
                "( 1, 'willie', 'who', 'new york', 'NY', 'willie@who.com', 'yahoo', 10012 )," +
                "( 2, 'joe', 'wonka', 'new york', 'NY', 'joe@wonka.com', 'yahoo', 10033 )," +
                "( 3, 'jane', 'who', 'San Francisco', 'CA', 'jane@who.com', 'ziper', 28218 )," +
                "( 4, 'jim', 'morrison', 'new york', 'NY', 'whoajee@who.com', 'nutz', 10012 )," +
                "( 5, 'eddie', 'van halen', 'los angeles', 'CA', 'zorker@who.com', 'yahoo', 90210 )";
        template.update( sql );

        sql = "insert into user_role( user_id, role_id ) " +
                "values( 0, 0 ), ( 1, 0 ), ( 2, 1 ), ( 3, 1 ), ( 4, 0 ), ( 5, 0 )";
        template.update( sql );

        sql = "insert into role( id, name ) values " +
                "(0, 'User'),(1, 'Admin')";
        template.update( sql );
        
        sql = "insert into pet( id, name, species_id, owner_id ) " +
                "values( 0, 'trixie', 1, 1 )," +
                "( 1, 'wixie', 3, 1 )," +
                "( 2, 'jimmy', 1, 3 )," +
                "( 3, 'flopsy', 2, 4 )," +
                "( 4, 'mixie', 2, 1 )";
        template.update( sql );

        sql = "insert into species( id, name ) " +
                "values( 0, 'cat' )," +
                "( 1, 'dog' )," +
                "( 2, 'birdy' )," +
                "( 3, 'fish' )";
        template.update( sql );
    }

    @Test
    /**
     * Tests getting all users from database using REST /users url
     */
    public void testSortedGetAll()
    {
        // default sort is by email address
        Response response = get( "/users" );
        assertEquals( response.getStatus(), Status.STATUS_OK );
        Document rval = (Document)response.getRepresentation().getUnderlyingRepresentation();
        print(response.getRepresentation());
        assertEquals( selectNodes( rval,  "/result/user" ).getLength(), 6 );
        assertEquals( selectSingleNode( rval,  "/result/user[1]/@email" ).getNodeValue(), "jane@who.com" );
        assertEquals( selectSingleNode( rval,  "/result/user[6]/@email" ).getNodeValue(), "zorker@who.com" );

        // sort again, by last name
        response = get( "/users?sort=last_name" );
        assertEquals( response.getStatus(), Status.STATUS_OK );
        rval = (Document)response.getRepresentation().getUnderlyingRepresentation();
        print(response.getRepresentation());
        assertEquals( selectNodes( rval,  "/result/user" ).getLength(), 6 );
        assertEquals( selectSingleNode( rval,  "/result/user[1]/@last_name" ).getNodeValue(), "morrison" );
        assertEquals( selectSingleNode( rval,  "/result/user[6]/@last_name" ).getNodeValue(), "wonka" );
    }

    @Test
    public void testSelectFilteredGetAll()
    {
        // get all users in 10033 zipcode
        Response response = get( "/users?zipcode=10033" );
        assertEquals( response.getStatus(), Status.STATUS_OK );
        Document rval = (Document)response.getRepresentation().getUnderlyingRepresentation();
        print(response.getRepresentation());
        // should only return joe
        assertEquals( selectNodes( rval,  "/result/user" ).getLength(), 1 );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='2']/@first_name" ).getNodeValue(), "joe" );

        // get all users in 10012 zipcode
        response = get( "/users?zipcode=10012" );
        assertEquals( response.getStatus(), Status.STATUS_OK );
        rval = (Document)response.getRepresentation().getUnderlyingRepresentation();
        print(response.getRepresentation());
        // should only return joe, willie, and jim
        assertEquals( selectNodes( rval,  "/result/user" ).getLength(), 3 );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='0']/@first_name" ).getNodeValue(), "joe" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='1']/@first_name" ).getNodeValue(), "willie" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='4']/@first_name" ).getNodeValue(), "jim" );
    }

    @Test
    public void testMultiFilteredGetAll()
    {
        // get all users with either dogs and/or fishes
        Response response = get( "/users?species_list=1,3" );
        assertEquals( response.getStatus(), Status.STATUS_OK );
        Document rval = (Document)response.getRepresentation().getUnderlyingRepresentation();
        //print(rval);
        // should only return willie and jane and their dogs and fishes
        assertEquals( selectNodes( rval,  "/result/user" ).getLength(), 2 );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='1']/@first_name" ).getNodeValue(), "willie" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='3']/@first_name" ).getNodeValue(), "jane" );
        // willie should have only two pets which are dogs and/or fishes, jane should have 1
        assertEquals( selectNodes( rval,  "/result/user[@id='1']/pet" ).getLength(), 2 );
        assertEquals( selectNodes( rval,  "/result/user[@id='3']/pet" ).getLength(), 1 );
    }

    @Test
    /**
     * Tests getting all users from database using REST /users url
     */
    public void testSimpleGetAll()
    {
        Response response = get( "/users" );
        assertEquals( response.getStatus(), Status.STATUS_OK );
        Document rval = (Document)response.getRepresentation().getUnderlyingRepresentation();
        //print(rval);
        assertEquals( selectNodes( rval,  "/result/user" ).getLength(), 6 );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='1']/@first_name" ).getNodeValue(), "willie" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='2']/@last_name" ).getNodeValue(), "wonka" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='4']/@email" ).getNodeValue(), "whoajee@who.com" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='5']/@city" ).getNodeValue(), "los angeles" );
    }

    @Test
    /**
     * Tests getting all users from database using REST /users url - specifically test the subnodes of the users
     */
    public void testNestedGetAll()
    {
        Response response = get( "/users" );
        assertEquals( response.getStatus(), Status.STATUS_OK );
        Document rval = (Document)response.getRepresentation().getUnderlyingRepresentation();
        //print(rval);
        assertEquals( selectNodes( rval,  "/result/user[@id='1']/pet" ).getLength(), 3 );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='1']/pet[@id='0']/@name" ).getNodeValue(), "trixie" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='1']/pet[@id='0']/@type" ).getNodeValue(), "dog" );
    }
    

    @Test
    public void testSimpleGetSingle()
    {
        Response response = get( "/users/1" );
        assertEquals( response.getStatus(), Status.STATUS_OK );
        Document rval = (Document)response.getRepresentation().getUnderlyingRepresentation();
        assertEquals( selectNodes( rval,  "/result/user" ).getLength(), 1 );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='1']/@first_name" ).getNodeValue(), "willie" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='1']/@last_name" ).getNodeValue(), "who" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='1']/@email" ).getNodeValue(), "willie@who.com" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='1']/@city" ).getNodeValue(), "new york" );
    }

    @Test
    public void testSimplePut()
    {
        Map<String,Object> args = new HashMap<String,Object>();
        args.put( "first_name","hanky" );
        args.put( "last_name","winters" );
        args.put( "email","mesuthela@hell.com" );
        args.put( "password","yahoo" );
        args.put( "zip","12345" );
        args.put( "city","nashville" );
        args.put( "state","TN" );

        // write the user to the database
        Response response = put( "/users", args );
        //Document res = response.toDocument();
        //print( res );
        assertEquals( response.getStatus(), Status.STATUS_OK );

        // fetch it back and ensure it's there
        response = get( "/users/6" );
        Document rval = (Document)response.getRepresentation().getUnderlyingRepresentation();
        assertEquals( response.getStatus(), Status.STATUS_OK );
        assertEquals( selectNodes( rval,  "/result/user" ).getLength(), 1 );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='6']/@first_name" ).getNodeValue(), "hanky" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='6']/@last_name" ).getNodeValue(), "winters" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='6']/@email" ).getNodeValue(), "mesuthela@hell.com" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='6']/@city" ).getNodeValue(), "nashville" );
    }

    @Test
    public void testSimplePost()
    {
        Map<String,Object> args = new HashMap<String,Object>();
        args.put( "first_name","newby" );
        args.put( "last_name","summers" );
        args.put( "email","crap@shoot.com" );
        args.put( "password","yippie" );
        args.put( "zip","54321" );
        args.put( "city","miami" );
        args.put( "state","FL" );

        // write the user to the database
        Response response = post( "/users/2", args );
        assertEquals( response.getStatus(), Status.STATUS_OK );

        // fetch it back and ensure it's there
        response = get( "/users/2" );
        assertEquals( response.getStatus(), Status.STATUS_OK );
        Document rval = (Document)response.getRepresentation().getUnderlyingRepresentation();
        assertEquals( selectNodes( rval,  "/result/user" ).getLength(), 1 );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='2']/@first_name" ).getNodeValue(), "newby" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='2']/@last_name" ).getNodeValue(), "summers" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='2']/@email" ).getNodeValue(), "crap@shoot.com" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='2']/@city" ).getNodeValue(), "miami" );
    }

    @Test
    public void testSimplePostWithOptionalArgs()
    {
        Map<String,Object> args = new HashMap<String,Object>();
        args.put( "first_name","timby" );

        // write the user to the database
        Response response = post( "/users/2", args );
        assertEquals( response.getStatus(), Status.STATUS_OK );

        // fetch it back and ensure it's there
        response = get( "/users/2" );
        assertEquals( response.getStatus(), Status.STATUS_OK );
        Document rval = (Document)response.getRepresentation().getUnderlyingRepresentation();
        assertEquals( selectNodes( rval,  "/result/user" ).getLength(), 1 );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='2']/@first_name" ).getNodeValue(), "timby" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='2']/@last_name" ).getNodeValue(), "summers" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='2']/@email" ).getNodeValue(), "crap@shoot.com" );
        assertEquals( selectSingleNode( rval,  "/result/user[@id='2']/@city" ).getNodeValue(), "miami" );
    }

    private NodeList selectNodes( Document rval, String xpath )
    {
        try
        {
            return XPathAPI.selectNodeList( rval, xpath );
        }
        catch( Exception e )
        {
            //gulp
            return null;
        }
    }

    private Node selectSingleNode( Document rval, String xpath )
    {
        try
        {
            return XPathAPI.selectSingleNode( rval, xpath );
        }
        catch( Exception e )
        {
            //gulp
            return null;
        }
    }

    private Response get( String url )
    {
        return handle( Method.GET, url, null );
    }

    private Response put( String url, Map<String, Object> args )
    {
        return handle( Method.PUT, url, args );
    }

    private Response post( String url, Map<String, Object> args )
    {
        return handle( Method.POST, url, args );
    }

    private Response handle( Method method, String url, Map<String,Object> args )
    {
        RouteMapper service = (RouteMapper) appContext.getBean( "routingService" );
        MutablePropertyValues mpvs = new MutablePropertyValues( args );
        Request request = new Request( url, method, mpvs );
        return service.handle( request );
    }

    private void print( Representation doc )
    {
        StringWriter writer = new StringWriter( );
        try
        {
            doc.write( writer );
        }
        catch( Exception e ){
        }
        log.debug( writer.toString() );
    }
}
