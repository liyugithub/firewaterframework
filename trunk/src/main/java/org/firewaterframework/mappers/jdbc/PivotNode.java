package org.firewaterframework.mappers.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: May 7, 2007
 * Time: 10:32:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class PivotNode
{
    protected static final Log log = LogFactory.getLog( PivotNode.class );
    protected static DocumentFactory df = DocumentFactory.getInstance();

    // the pivotTag and pivotTagAttribute work together to create the XML element name for each
    // 'row' in the result set.  To always create the same name (ie. a heterogeneous collection of rows) just
    // statically set the pivotTag value.  If you want to determine the name of the element dynamically
    // set the pivotTagAttribute to the name of the property in the row that contains the element name.
    protected String pivotTag;
    protected String pivotTagAttribute;

    protected String pivotURLSelector;
    protected String pivotColumn;
    protected Map<String,String> attributeColumns;
    protected PivotNode[] subNodes;

    public PivotNode(){}
    
    public PivotNode( String pivotTag,
                      String pivotURLSelector,
                      String pivotColumn,
                      String[] attributeColumnColumnNames,
                      String[] attributeColumnTagNames,
                      PivotNode[] subNodes )
    {
        this.pivotTag = pivotTag;
        this.pivotURLSelector = pivotURLSelector;
        this.pivotColumn = pivotColumn;

        this.attributeColumns = new HashMap<String,String>();
        for( int i = 0; i < attributeColumnColumnNames.length; i++ )
        {
            attributeColumns.put( attributeColumnColumnNames[i], attributeColumnTagNames[i] );
        }

        this.subNodes = subNodes;
    }

    public Document process( List<Map<String,Object>> rows )
    {
        Document rval = df.createDocument( );

        Element currentElement = df.createElement( "result" );
        rval.add( currentElement );

        int[] currRow = new int[] { 0 };
        while( currRow[0] < rows.size() )
        {
            currentElement.add( processNextElement( rows, currRow, "" ));
        }
        return rval;
    }

    public Element processNextElement( List<Map<String,Object>> rows, int[] rowNum, String url )
    {
        // stop if we are out of rows
        int startRowIndex = rowNum[0];
        Map<String,Object> startRow = rows.get( startRowIndex );
        Object topPivotValue = startRow.get( pivotColumn );
        Element rval = df.createElement( getInternalPivotTag( startRow ));

        if( pivotURLSelector != null )
        {
            url += '/' + pivotURLSelector + '/' + topPivotValue.toString();
            rval.addAttribute( "url", url );
        }

        // first, populate the current node with the properties and attributes pertaining to it
        processCurrentRow( startRow, rval );

        // process each subnode, loop through the current range for this node and recurse on each unique subnode
        if( subNodes != null )
        {
            for( PivotNode subNode: subNodes )
            {
                String subNodeColumnName = subNode.getPivotColumn();
                Set<Object> processedSubPivotValues = new HashSet<Object>();
                int rangeRowIndex = startRowIndex;
                while( rangeRowIndex < rows.size() && topPivotValue.equals( rows.get( rangeRowIndex ).get( pivotColumn )))
                {
                    Object subPivotValue = rows.get( rangeRowIndex ).get( subNodeColumnName );
                    if( subPivotValue != null && !processedSubPivotValues.contains( subPivotValue ))
                    {
                        rval.add( subNode.processNextElement( rows, new int[] { rangeRowIndex }, url ));
                        processedSubPivotValues.add( subPivotValue );
                    }
                    rangeRowIndex++;
                }
            }
        }

        // we're done processing the row range for this pivot value - skip over all of the rest of the rows in this range
        while( rowNum[0] < rows.size() && topPivotValue.equals( rows.get( rowNum[0] ).get( pivotColumn )))
        {
            rowNum[0]++;
        }

        return rval;
    }

    protected void processCurrentRow( Map<String,Object> row, Element element )
    {
        Object pivotValue = row.get( pivotColumn );

        if( pivotValue != null )
        {
            // include the 'id' attribute
            element.addAttribute( "id", pivotValue.toString() );

            // now process the attributes
            if( attributeColumns != null )
            {
                for( Map.Entry<String,String> attributeEntry: attributeColumns.entrySet() )
                {
                    String attributeColumnName = attributeEntry.getKey();
                    String attributeTagName = attributeEntry.getValue();
                    if( attributeTagName == null ) attributeTagName = attributeColumnName;
                    Object attributeValue = row.get( attributeColumnName );

                    if( attributeValue != null )
                    {
                        element.addAttribute( attributeTagName, attributeValue.toString() );
                    }
                }
            }
        }
    }

    public String getPivotColumn() {
        return pivotColumn;
    }

    @Required
    public void setPivotColumn(String pivotColumn) {
        this.pivotColumn = pivotColumn;
    }

    public Map<String,String> getAttributeColumns() {
        return attributeColumns;
    }

    public void setAttributeColumns(Map<String,String> attributeColumns) {
        this.attributeColumns = attributeColumns;
    }

    public void setAttributeColumnList(List<String> attributeColumns)
    {
        this.attributeColumns = new HashMap<String,String>();
        for( String attr: attributeColumns )
        {
            this.attributeColumns.put( attr, null );
        }
    }

    public PivotNode[] getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(PivotNode[] subNodes)
    {
        this.subNodes = subNodes;
    }

    public String getPivotTag() {
        return pivotTag;
    }

    public void setPivotTag(String pivotTag) {
        this.pivotTag = pivotTag;
    }

    public String getPivotURLSelector() {
        return pivotURLSelector;
    }

    public void setPivotURLSelector(String pivotURLSelector) {
        this.pivotURLSelector = pivotURLSelector;
    }

    public String getPivotTagAttribute() {
        return pivotTagAttribute;
    }

    public void setPivotTagAttribute(String pivotTagAttribute) {
        this.pivotTagAttribute = pivotTagAttribute;
    }

    protected String getInternalPivotTag( Map<String,Object> row )
    {
        if( pivotTagAttribute != null && row.get( pivotTagAttribute ) != null )
        {
            return row.get( pivotTagAttribute ).toString();
        }
        else if( pivotTag == null )
        {
            return pivotColumn;
        }
        return pivotTag;
    }
}
