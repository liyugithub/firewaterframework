package org.firewaterframework.mappers.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.firewaterframework.WSException;
import org.firewaterframework.mappers.Mapper;
import org.firewaterframework.rest.*;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

/**
 * Created By: tspurway
 */
public class XSLMapper implements Mapper
{
    protected static final Log log = LogFactory.getLog( XSLMapper.class );
    public static DocumentFactory docFactory = DocumentFactory.getInstance();
    public static TransformerFactory txFactory = TransformerFactory.newInstance();

    private Document source;
    private String stylesheet;

    public String getStylesheet() {
        return stylesheet;
    }

    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    public Response handle( Request request )
    {
        try
        {
            Source styleSource = new StreamSource( new StringReader( stylesheet ));
            Transformer transformer = txFactory.newTransformer( styleSource );
            DocumentSource docSource = new DocumentSource( source );
            DocumentResult result = new DocumentResult();

            // add all of the attributes of the request as parameters to the XSL stylesheet
            for( String attr: request.getArgs().keySet() )
            {
                transformer.setParameter( attr, request.getArgs().get( attr ));
            }
            transformer.transform( docSource, result );

            Response rval = new DocumentResponse( Status.STATUS_OK, MIMEType.application_xml, result.getDocument() );
            return rval;

        }
        catch( Exception e )
        {
            throw new WSException(  "Unhappy XSL transformation", e );
        }
    }

    public Document getSource() {
        return source;
    }

    public void setSource(Document source) {
        this.source = source;
    }
}
