package org.firewaterframework.mappers.xml;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;
import org.firewaterframework.WSException;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: May 4, 2007
 * Time: 11:53:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class DocumentResolver
{
    public static Document resolve( Resource resource )
    {
        try
        {
            SAXReader reader = new SAXReader();
            return reader.read( resource.getInputStream() );
        }
        catch( Exception e )
        {
            throw new WSException( "Could not parse XML file: " + resource.getFilename(), e );
        }
    }
}
