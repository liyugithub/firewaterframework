package com.pumpframework.test;

import com.pumpframework.test.XMLUtil;
import org.firewaterframework.mappers.RouteMapper;
import org.firewaterframework.rest.Method;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Response;
import junit.framework.Assert;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.io.SAXReader;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Sep 6, 2007
 * Time: 10:13:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestRouteMapper extends Assert
{
    protected static final Log log = LogFactory.getLog( TestRouteMapper.class );
    public static DocumentFactory docFactory = DocumentFactory.getInstance();
    private static ApplicationContext appContext;

    @BeforeClass
    public static void init()
    {
        appContext = new ClassPathXmlApplicationContext( "rest-base.xml" );
        BasicDataSource ds = (BasicDataSource)appContext.getBean( "dataSource" );
        JdbcTemplate template = new JdbcTemplate( ds );
        String sql =
                "create table user(id int auto_increment primary key, first_name varchar(255), last_name varchar(255)," +
                "city varchar(255), state char(2), email varchar(255), password varchar(255), zip_small int)";
        template.execute( sql );

        sql = "create table pet(id int auto_increment primary key, name varchar(255), species_id int, owner_id int)";
        template.execute( sql );

        sql = "create table species(id int auto_increment primary key, name varchar(255))";
        template.execute( sql );

        sql = "insert into user( id, first_name, last_name, city, state, email, password, zip_small ) " +
                "values( 0, 'joe', 'who', 'new york', 'NY', 'joe@who.com', 'yahoo', 10012 )," +
                "( 1, 'willie', 'who', 'new york', 'NY', 'willie@who.com', 'yahoo', 10012 )," +
                "( 2, 'joe', 'wonka', 'new york', 'NY', 'joe@wonka.com', 'yahoo', 10033 )," +
                "( 3, 'jane', 'who', 'San Francisco', 'CA', 'jane@who.com', 'ziper', 28218 )," +
                "( 4, 'jim', 'morrison', 'new york', 'NY', 'whoajee@who.com', 'nutz', 10012 )," +
                "( 5, 'eddie', 'van halen', 'los angeles', 'CA', 'zorker@who.com', 'yahoo', 90210 )";
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
    public void testSimpleGetAll()
    {
        Document rval = get( "/users" );
        //print(rval);
        assertEquals( rval.selectNodes( "/result/user" ).size(), 6 );
        assertEquals( rval.selectSingleNode( "/result/user[@id='1']/@first_name" ).getStringValue(), "willie" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='2']/@last_name" ).getStringValue(), "wonka" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='4']/@email" ).getStringValue(), "whoajee@who.com" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='5']/@city" ).getStringValue(), "los angeles" );
    }

    @Test
    /**
     * Tests getting all users from database using REST /users url - specifically test the subnodes of the users
     */
    public void testNestedGetAll()
    {
        Document rval = get( "/users" );
        //print(rval);
        assertEquals( rval.selectNodes( "/result/user[@id='1']/pet" ).size(), 3 );
        assertEquals( rval.selectSingleNode( "/result/user[@id='1']/pet[@id='0']/@name" ).getStringValue(), "trixie" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='1']/pet[@id='0']/@type" ).getStringValue(), "dog" );
    }
    

    @Test
    public void testSimpleGetSingle()
    {
        Document rval = get( "/users/1" );
        assertEquals( rval.selectNodes( "/result/user" ).size(), 1 );
        assertEquals( rval.selectSingleNode( "/result/user[@id='1']/@first_name" ).getStringValue(), "willie" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='1']/@last_name" ).getStringValue(), "who" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='1']/@email" ).getStringValue(), "willie@who.com" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='1']/@city" ).getStringValue(), "new york" );
    }

    @Test
    public void testSimplePut()
    {
        Map<String,String> args = new HashMap<String,String>();
        args.put( "first_name","hanky" );
        args.put( "last_name","winters" );
        args.put( "email","mesuthela@hell.com" );
        args.put( "password","yahoo" );
        args.put( "zip_small","12345" );
        args.put( "city","nashville" );
        args.put( "state","TN" );

        // write the user to the database
        Document res = put( "/users", args );
        print( res );

        // fetch it back and ensure it's there
        Document rval = get( "/users/6" );
        assertEquals( rval.selectNodes( "/result/user" ).size(), 1 );
        assertEquals( rval.selectSingleNode( "/result/user[@id='6']/@first_name" ).getStringValue(), "hanky" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='6']/@last_name" ).getStringValue(), "winters" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='6']/@email" ).getStringValue(), "mesuthela@hell.com" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='6']/@city" ).getStringValue(), "nashville" );
    }

    @Test
    public void testSimplePost()
    {
        Map<String,String> args = new HashMap<String,String>();
        args.put( "first_name","newby" );
        args.put( "last_name","summers" );
        args.put( "email","crap@shoot.com" );
        args.put( "password","yippie" );
        args.put( "zip_small","54321" );
        args.put( "city","miami" );
        args.put( "state","FL" );

        // write the user to the database
        post( "/users/2", args );

        // fetch it back and ensure it's there
        Document rval = get( "/users/2" );
        assertEquals( rval.selectNodes( "/result/user" ).size(), 1 );
        assertEquals( rval.selectSingleNode( "/result/user[@id='2']/@first_name" ).getStringValue(), "newby" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='2']/@last_name" ).getStringValue(), "summers" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='2']/@email" ).getStringValue(), "crap@shoot.com" );
        assertEquals( rval.selectSingleNode( "/result/user[@id='2']/@city" ).getStringValue(), "miami" );
    }

    private Document get( String url )
    {
        return handle( Method.GET, url, null );
    }

    private Document put( String url, Map args )
    {
        return handle( Method.PUT, url, args );
    }

    private Document post( String url, Map args )
    {
        return handle( Method.POST, url, args );
    }

    private Document handle( Method method, String url, Map<String,Object> args )
    {
        RouteMapper service = (RouteMapper) appContext.getBean( "routingService" );
        Request request = new Request( url, method, args );
        try
        {
            Response response = service.handle( request );
            Document rval = response.asDocument();
            return rval;
        }
        catch( Exception e )
        {
            return null;
        }
    }

    private void print( Document doc )
    {
        StringWriter writer = new StringWriter( );
        XMLUtil.prettyPrint( doc, writer );
        writer.flush();
        log.debug( writer.toString() );
    }
}
