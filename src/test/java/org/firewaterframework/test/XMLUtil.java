package org.firewaterframework.test;

import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Writer;

/**
 * Pretty print the XML
 */
public class XMLUtil
{
    protected static final Log log = LogFactory.getLog( XMLUtil.class );

    public static void prettyPrint( Document doc, Writer outWriter )
    {
        try
        {
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(outWriter , format );

            writer.write( doc );
            writer.close();
        }
        catch( Exception e )
        {
            log.error( "Error printing XML document: ", e );
        }
    }
}
