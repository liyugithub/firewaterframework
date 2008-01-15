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
