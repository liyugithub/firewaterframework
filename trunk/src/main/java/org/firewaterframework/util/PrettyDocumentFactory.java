package org.firewaterframework.util;

import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;

public class PrettyDocumentFactory
{
    protected static DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
    protected static PrettyDocumentFactory instance;
    private String instruction;
    private String details;

    private PrettyDocumentFactory()
    {

    }

    public static PrettyDocumentFactory getInstance()
    {
        if( instance == null )
        {
            instance = new PrettyDocumentFactory();
        }
        return instance;
    }

    public Document createDocument()
    {
        try
        {
            Document rval = df.newDocumentBuilder().newDocument();
            if( instruction != null && details != null )
            {
                ProcessingInstruction processingInstruction = rval.createProcessingInstruction( instruction, details );
                rval.appendChild( processingInstruction );
            }
            return rval;
        }
        catch( Exception e )
        {
            //gulp -
            return null;
        }
    }

    public void setProcessingInstruction( String instruction, String details )
    {
        this.instruction = instruction;
        this.details = details;
    }

}
