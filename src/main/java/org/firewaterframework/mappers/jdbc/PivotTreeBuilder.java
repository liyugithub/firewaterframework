package org.firewaterframework.mappers.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * This class maps flat SQL result sets into heirarchical XML documents.  It is used
 * by the QueryMapper to resolve SQL 'select' queries into their equivalent REST XML.  It uses
 * the central concept of a Pivot Tree as the basis of it's transformation.  A Pivot
 * Tree is simply a table containing the cross-product of properties present in every
 * resource.  The table must be ordered by 'pivot columns', which are typically the
 * primary keys of the underlying joined tables.  The PivotTreeBuilder will iterate
 * over the tabular result set, creating a single XML node for each unique value of
 * it's pivot column.  If there are duplicate values for the pivot column in consecutive
 * rows, the class will delegate the processing of these rows to it's 'subnodes', which are
 * also PivotTreeBuilders, and will append the XML element results as children of the current
 * XML node.
 * <p>
 * Assuming we have a tables Owner(id, name, address, city, state, zip) and
 * Pet(id, name, owner), here's an example of configuring a PivotTreeBuilder
 * using a Spring configuration to fetch all users and their pets (mapped to
 * the <code>/users</code> URL:
 * <p>
 * <code><pre>
 *   <bean id="usersGetMapper" class="org.firewaterframework.mappers.jdbc.QueryMapper">
 *      <property name="query">
 *          <value>
 *              select  u.id as user, p.id as pet, p.name as pet_name, u.name as name,
 *              from user u
 *              left outer join pet p on u.id = p.owner_id
 *              order by u.id,
 *          </value>
 *      </property>
 *      <property name="dataSource" ref="dataSource"/>
 *      <property name="pivotTreeBuilder">
 *          <bean class="org.firewaterframework.mappers.jdbc.PivotTreeBuilder">
 *              <property name="pivotColumn" value="user"/>
 *              <property name="pivotURLSelector" value="users"/>
 *              <property name="attributeColumnList">
 *                  <list>
 *                      <value>name</value>
 *                     <value>city</value>
 *                  </list>
 *              </property>
 *              <property name="subNodes">
 *                  <list>
 *                      <bean class="org.firewaterframework.mappers.jdbc.PivotTreeBuilder">
 *                          <property name="pivotColumn" value="pet"/>
 *                          <property name="pivotURLSelector" value="pets"/>
 *                          <property name="attributeColumns">
 *                              <map>
 *                                  <entry key="pet_name" value="name"/>
 *                              </map>
 *                          </property>
 *                      </bean>
 *                  </list>
 *              </property>
 *         </bean>
 *     </property>
 * </bean>
 *</pre></code
 * <p>
 * After a sucessful call to the GET method on the <code>/users</code> resource.  We
 * would get a Response with an XML payload like:
 * <p>
 * <code><pre>
 * <result>
 *     <user url="/users/4" id="4" name="jim morrison" city="new york">
 *         <pet url="/users/4/pets/3" id="3" name="flopsy"/>
 *     </user>
 *     <user url="/users/5" id="5" name="eddie van halen" city="los angeles"/>
 *     <user url="/users/0" id="0" name="joe who" city="new york"/>
 *     <user url="/users/1" id="1" name="willie nelson" city="chatanooga">
 *         <pet url="/users/1/pets/0" id="0" name="trixie"/>
 *         <pet url="/users/1/pets/1" id="1" name="wixie"/>
 *         <pet url="/users/1/pets/4" id="4" name="mixie"/>
 *     </user>
 *  </result>
 *
 */
public class PivotTreeBuilder
{
    protected static final Log log = LogFactory.getLog( PivotTreeBuilder.class );
    protected static DocumentFactory df = DocumentFactory.getInstance();

    /**
     * the pivotTag and pivotTagAttribute work together to create the XML element name for each
     * 'row' in the result set.  To always create the same name (ie. a heterogeneous collection of rows) just
     * statically set the pivotTag value.  If you want to determine the name of the element dynamically
     * set the pivotTagAttribute to the name of the property in the row that contains the element name.
     */
    protected String pivotTag;
    protected String pivotTagAttribute;

    /**
     * this property is used to select a name for the 'url' attribute for each node.  As
     * the builder recursively descends the subnodes, URLs are built up by concatenating
     * their id and pivotURLSelector values.
     */
    protected String pivotURLSelector;

    /**
     * this identifies which column in the result set to 'pivot' on.  It is typically
     * the primary key column of the underlying table/resource
     */
    protected String pivotColumn;

    /**
     * this maps the name of the column in the result set (the key) with the name of
     * the XML attribute in the result.  If the names are exactly the same, use the
     * convenience method setAttributeColumnList()
     */
    protected Map<String,String> attributeColumns;

    /**
     * these represent the PivotTreeBuilders that will create the subnodes in our
     * response XML document.  When we find adjacent rows with duplicate values for
     * the pivotColumn, delegate to the subNodes to process.
     */
    protected PivotTreeBuilder[] subNodes;

    public PivotTreeBuilder(){}

    /**
     * this constructor is typically only used by jUnit test cases.
     *
     * @param pivotTag
     * @param pivotURLSelector
     * @param pivotColumn
     * @param attributeColumnColumnNames
     * @param attributeColumnTagNames
     * @param subNodes
     */
    public PivotTreeBuilder( String pivotTag,
                      String pivotURLSelector,
                      String pivotColumn,
                      String[] attributeColumnColumnNames,
                      String[] attributeColumnTagNames,
                      PivotTreeBuilder[] subNodes )
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

    /**
     * top level interface to process PivotTree result sets.
     * @param rows the SQL result set, a list of maps
     * @return the resultant XML document
     */
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

    /**
     * the method to recursively parse and build a single element.
     * @param rows the result set
     * @param rowNum the current row number being processed
     * @param url the current URL for the resource
     * @return the Element to be added to the resulting XML Document
     */
    protected Element processNextElement( List<Map<String,Object>> rows, int[] rowNum, String url )
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
            for( PivotTreeBuilder subNode: subNodes )
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

    public PivotTreeBuilder[] getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(PivotTreeBuilder[] subNodes)
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
