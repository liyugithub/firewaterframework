package com.pumpframework.test;

import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;

import java.util.*;
import java.io.StringWriter;

import org.firewaterframework.mappers.jdbc.PivotNode;
import com.pumpframework.test.XMLUtil;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: May 8, 2007
 * Time: 9:38:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestPivotNode extends Assert
{
    protected static final Log log = LogFactory.getLog( TestPivotNode.class );

    @Test
    public void onePivotJustAttributes()
    {
        List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        Map<String,Object> map = null;

        map = new HashMap<String,Object>();
        map.put( "message", 1 );
        map.put( "view_count", 107 );
        map.put( "subject", "subject.1" );
        map.put( "body", "body.1" );
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 2 );
        map.put( "view_count", 207 );
        map.put( "subject", "subject.2" );
        map.put( "body", "body.2" );
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 3 );
        map.put( "view_count", 307 );
        map.put( "subject", "subject.3" );
        map.put( "body", "body.3" );
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 4 );
        map.put( "view_count", 407 );
        map.put( "subject", "subject.4" );
        map.put( "body", "body.4" );
        rows.add( map );

        PivotNode pn = new PivotNode(
                null,
                "messages",
                "message",
                new String[]{ "view_count", "subject", "body" },
                new String[]{ null, null, null },
                null );
        Document doc = pn.process( rows );
        assertNotNull( doc );

        //print(doc);

        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/@view_count" ).getText(), "107" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/@body" ).getText(), "body.1" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/@subject" ).getText(), "subject.2" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='4']/@view_count" ).getText(), "407" );
    }

    @Test
    public void twoPivotsJustAttributes()
    {
        List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        Map<String,Object> map = null;

        map = new HashMap<String,Object>();
        map.put( "message", 1 );
        map.put( "view_count", 107 );
        map.put( "subject", "subject.1" );
        map.put( "body", "body.1" );
        map.put( "reply", null );
        map.put( "r_view_count", null );
        map.put( "r_subject", null );
        map.put( "r_body", null );
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 2 );
        map.put( "view_count", 207 );
        map.put( "subject", "subject.2" );
        map.put( "body", "body.2" );
        map.put( "reply", 30 );
        map.put( "r_view_count", 399 );
        map.put( "r_subject", "subject.2.30" );
        map.put( "r_body", "body.2.30." );
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 2 );
        map.put( "view_count", 207 );
        map.put( "subject", "subject.2" );
        map.put( "body", "body.2" );
        map.put( "reply", 40 );
        map.put( "r_view_count", 499 );
        map.put( "r_subject", "subject.2.40" );
        map.put( "r_body", "body.2.40" );
        rows.add( map );

        PivotNode pn = new PivotNode(
                "message",
                "messages",
                "message",
                new String[]{ "view_count", "subject", "body" },
                new String[]{ "view_count", "subject", "body" },
                new PivotNode[] { new PivotNode(
                        "reply",
                        "replies",
                        "reply",
                        new String[] { "r_view_count", "r_subject", "r_body" },
                        new String[] { "view_count", "subject", "body" },
                        null)});

        Document doc = pn.process( rows );
        //print( doc );
        assertNotNull( doc );

        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/@view_count" ).getText(), "107" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/@body" ).getText(), "body.1" );
        assertNull( doc.selectSingleNode( "/result/message[@id='1']/reply" ) );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/@view_count" ).getText(), "207" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/reply[@id='30']/@view_count" ).getText(), "399" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/reply[@id='40']/@subject" ).getText(), "subject.2.40" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/reply[@id='30']/@url" ).getText(), "/messages/2/replies/30" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/@url" ).getText(), "/messages/2" );
    }

    @Test
    public void fullyLoaded()
    {
        List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        Map<String,Object> map = null;

        map = new HashMap<String,Object>();
        map.put( "message", 1 );
        map.put( "view_count", 107 );
        map.put( "subject", "subject.1" );
        map.put( "body", "body.1" );
        map.put( "user", 10 );
        map.put( "name", "name.10");
        map.put( "region", 100 );
        map.put( "zipcode", "110100");
        map.put( "reply", null );
        map.put( "r_view_count", null );
        map.put( "r_subject", null );
        map.put( "r_body", null );
        map.put( "r_user", null );
        map.put( "r_name", null);
        map.put( "r_region", null );
        map.put( "r_zipcode", null);
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 2 );
        map.put( "view_count", 207 );
        map.put( "subject", "subject.2" );
        map.put( "body", "body.2" );
        map.put( "user", 10 );
        map.put( "name", "name.10");
        map.put( "region", 100 );
        map.put( "zipcode", "10011");
        map.put( "reply", 1000 );
        map.put( "r_view_count", 100 );
        map.put( "r_subject", "subject.1000" );
        map.put( "r_body", "body.1000" );
        map.put( "r_user", 20 );
        map.put( "r_name", "name.20");
        map.put( "r_region", 200 );
        map.put( "r_zipcode", "20011");
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 2 );
        map.put( "view_count", 207 );
        map.put( "subject", "subject.2" );
        map.put( "body", "body.2" );
        map.put( "user", 10 );
        map.put( "name", "name.2.10");
        map.put( "region", 100 );
        map.put( "zipcode", "10011");
        map.put( "reply", 2000 );
        map.put( "r_view_count", 200 );
        map.put( "r_subject", "subject.2000" );
        map.put( "r_body", "body.2000" );
        map.put( "r_user", 30 );
        map.put( "r_name", "name.30");
        map.put( "r_region", 300 );
        map.put( "r_zipcode", "30011");
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 2 );
        map.put( "view_count", 207 );
        map.put( "subject", "subject.2" );
        map.put( "body", "body.2" );
        map.put( "user", 10 );
        map.put( "name", "name.2.10");
        map.put( "region", 100 );
        map.put( "zipcode", "10011");
        map.put( "reply", 3000 );
        map.put( "r_view_count", 300 );
        map.put( "r_subject", "subject.3000" );
        map.put( "r_body", "body.3000" );
        map.put( "r_user", 10 );
        map.put( "r_name", "name.10");
        map.put( "r_region", 100 );
        map.put( "r_zipcode", "10011");
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 3 );
        map.put( "view_count", 307 );
        map.put( "subject", "subject.3" );
        map.put( "body", "body.3" );
        map.put( "user", 10 );
        map.put( "name", "name.10");
        map.put( "region", 100 );
        map.put( "zipcode", "10011");
        map.put( "reply", 4000 );
        map.put( "r_view_count", 400 );
        map.put( "r_subject", "subject.4000" );
        map.put( "r_body", "body.4000" );
        map.put( "r_user", 10 );
        map.put( "r_name", "name.10");
        map.put( "r_region", 100 );
        map.put( "r_zipcode", "10011");
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 4 );
        map.put( "view_count", 407 );
        map.put( "subject", "subject.4" );
        map.put( "body", "body.4" );
        map.put( "user", 90 );
        map.put( "name", "name.90");
        map.put( "region", 500 );
        map.put( "zipcode", "50011");
        map.put( "reply", null );
        map.put( "r_view_count", null );
        map.put( "r_subject", null );
        map.put( "r_body", null );
        map.put( "r_user", null );
        map.put( "r_name", null);
        map.put( "r_region", null );
        map.put( "r_zipcode", null);
        rows.add( map );

        PivotNode pn = new PivotNode(
                "message",
                "messages",
                "message",
                new String[]{ "view_count", "subject", "body" },
                new String[]{ "view_count", "subject", "body" },
                new PivotNode[] { new PivotNode(
                    "user",
                    null,
                    "user",
                    new String[]{ "name" },
                    new String[]{ "name" },
                    new PivotNode[] { new PivotNode (
                        "region",
                        null,
                        "region",
                        new String[] { "zipcode" },
                        new String[] { "zipcode" },
                        null)}),
                new PivotNode(
                        "reply",
                        "replies",
                        "reply",
                        new String[] { "r_view_count", "r_subject", "r_body" },
                        new String[] { "view_count", "subject", "body" },
                        new PivotNode[] { new PivotNode(
                            "user",
                            null,
                            "r_user",
                            new String[]{ "r_name" },
                            new String[]{ "name" },
                            new PivotNode[] { new PivotNode (
                                "region",
                                null,
                                "r_region",
                                new String[] { "r_zipcode" },
                                new String[] { "zipcode" },
                                null)})})});

        Document doc = pn.process( rows );

        //print( doc );

        assertNotNull( doc );

        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/@view_count" ).getText(), "107" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/@body" ).getText(), "body.1" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/@body" ).getText(), "body.2" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/@view_count" ).getText(), "207" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/reply[@id='2000']/@view_count" ).getText(), "200" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/reply[@id='2000']/@subject" ).getText(), "subject.2000" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/reply[@id='3000']/user/@name" ).getText(), "name.10" );
        assertNull( doc.selectSingleNode( "/result/message[@id='2']/reply[@id='4000']" ));
        assertNotNull( doc.selectSingleNode( "/result/message[@id='3']/reply[@id='4000']" ));
    }

    @Test
    public void onePivotWithNullAttributesAndProperties()
    {
        List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        Map<String,Object> map = null;

        map = new HashMap<String,Object>();
        map.put( "message", 1 );
        map.put( "view_count", 107 );
        map.put( "subject", null );
        map.put( "body", "body.1" );
        map.put( "user", 10 );
        map.put( "name", "name.1.10");
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 2 );
        map.put( "view_count", 207 );
        map.put( "subject", "subject.2" );
        map.put( "body", "body.2" );
        map.put( "user", null );
        map.put( "name", null);
        rows.add( map );


        PivotNode pn = new PivotNode(
                "message",
                "messages",
                "message",
                new String[]{ "view_count", "subject", "body" },
                new String[]{ "view_count", "subject", "body" },
                new PivotNode[] { new PivotNode( "user", null, "user", new String[]{ "name" }, new String[]{ "name" }, null) }
                );

        Document doc = pn.process( rows );
        //print( doc );
        assertNotNull( doc );

        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/@view_count" ).getText(), "107" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/@body" ).getText(), "body.1" );
        assertNull( doc.selectSingleNode( "/result/message[@id='1']/@subject" ) );
        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/user/@id" ).getText(), "10");
        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/user/@name" ).getText(), "name.1.10");
        assertNull( doc.selectSingleNode( "/result/message[@id='2']/user" ));
    }

    @Test
    public void onePivotAttributesAndProperties()
    {
        List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        Map<String,Object> map = null;

        map = new HashMap<String,Object>();
        map.put( "message", 1 );
        map.put( "view_count", 107 );
        map.put( "subject", "subject.1" );
        map.put( "body", "body.1" );
        map.put( "user", 10 );
        map.put( "name", "name.1.10");
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 2 );
        map.put( "view_count", 207 );
        map.put( "subject", "subject.2" );
        map.put( "body", "body.2" );
        map.put( "user", 20 );
        map.put( "name", "name.2.20");
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 3 );
        map.put( "view_count", 307 );
        map.put( "subject", "subject.3" );
        map.put( "body", "body.3" );
        map.put( "user", 30 );
        map.put( "name", "name.3.30");
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 4 );
        map.put( "view_count", 407 );
        map.put( "subject", "subject.4" );
        map.put( "body", "body.4" );
        map.put( "user", 40 );
        map.put( "name", "name.4.40");
        rows.add( map );

        PivotNode pn = new PivotNode(
                "message",
                "messages",
                "message",
                new String[]{ "view_count", "subject", "body" },
                new String[]{ "view_count", "subject", "body" },
                new PivotNode[] {
                        new PivotNode( "user", null, "user", new String[]{ "name" }, new String[]{ "name" }, null )});

        Document doc = pn.process( rows );
        assertNotNull( doc );
        //print(doc);

        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/@view_count" ).getText(), "107" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/@body" ).getText(), "body.1" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/@subject" ).getText(), "subject.2" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='4']/@view_count" ).getText(), "407" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='3']/user/@id" ).getText(), "30");
        assertEquals( doc.selectSingleNode( "/result/message[@id='3']/user/@name" ).getText(), "name.3.30");
    }

    @Test
    public void onePivotNestedProperties()
    {
        List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
        Map<String,Object> map = null;

        map = new HashMap<String,Object>();
        map.put( "message", 1 );
        map.put( "view_count", 107 );
        map.put( "subject", "subject.1" );
        map.put( "body", "body.1" );
        map.put( "user", 10 );
        map.put( "name", "name.1.10");
        map.put( "region", 100 );
        map.put( "zipcode", "110100");
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 2 );
        map.put( "view_count", 207 );
        map.put( "subject", "subject.2" );
        map.put( "body", "body.2" );
        map.put( "user", 20 );
        map.put( "name", "name.2.20");
        map.put( "region", 200 );
        map.put( "zipcode", "220200");
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 3 );
        map.put( "view_count", 307 );
        map.put( "subject", "subject.3" );
        map.put( "body", "body.3" );
        map.put( "user", 30 );
        map.put( "name", "name.3.30");
        map.put( "region", 300 );
        map.put( "zipcode", "330300");
        rows.add( map );

        map = new HashMap<String,Object>();
        map.put( "message", 4 );
        map.put( "view_count", 407 );
        map.put( "subject", "subject.4" );
        map.put( "body", "body.4" );
        map.put( "user", 40 );
        map.put( "name", "name.4.40");
        map.put( "region", 400 );
        map.put( "zipcode", "440400");
        rows.add( map );


        PivotNode pn = new PivotNode(
                "message",
                "messages",
                "message",
                new String[]{ "view_count", "subject", "body" },
                new String[]{ "view_count", "subject", "body" },
                new PivotNode[] { new PivotNode(
                    "user",
                    null,
                    "user",
                    new String[]{ "name" },
                    new String[]{ "name" },
                    new PivotNode[] { new PivotNode (
                        "region",
                        null,
                        "region",
                        new String[] { "zipcode" },
                        new String[] { "zipcode" },
                        null)})});

        Document doc = pn.process( rows );
        assertNotNull( doc );
        //print(doc);

        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/@view_count" ).getText(), "107" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='1']/@body" ).getText(), "body.1" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='2']/@subject" ).getText(), "subject.2" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='4']/@view_count" ).getText(), "407" );
        assertEquals( doc.selectSingleNode( "/result/message[@id='3']/user/@id" ).getText(), "30");
        assertEquals( doc.selectSingleNode( "/result/message[@id='3']/user/@name" ).getText(), "name.3.30");
        assertEquals( doc.selectSingleNode( "/result/message[@id='4']/user/region/@id" ).getText(), "400");
        assertEquals( doc.selectSingleNode( "/result/message[@id='4']/user/region/@zipcode" ).getText(), "440400");
    }

    private void print( Document doc )
    {
        StringWriter writer = new StringWriter( );
        XMLUtil.prettyPrint( doc, writer );
        writer.flush();
        log.debug( writer.toString() );
    }
}
