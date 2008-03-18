package org.firewaterframework.util;

import org.dom4j.DocumentFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.ProcessingInstruction;

import java.util.Map;

public class PrettyDocumentFactory
{
    protected static DocumentFactory df = DocumentFactory.getInstance();
    protected static PrettyDocumentFactory instance;
    protected ProcessingInstruction processingInstruction;

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
        Document rval = df.createDocument();
        rval.add( processingInstruction );
        return rval;
    }

    public Element createElement( String name )
    {
        return df.createElement( name );
    }

    public ProcessingInstruction getProcessingInstruction() {
        return processingInstruction;
    }

    public void setProcessingInstruction(ProcessingInstruction processingInstruction) {
        this.processingInstruction = processingInstruction;
    }
}
