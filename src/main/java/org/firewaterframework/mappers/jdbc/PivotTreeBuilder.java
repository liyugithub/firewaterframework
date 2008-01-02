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
 *              <property name="idColumn" value="user"/>
 *              <property name="urlPrefix" value="users"/>
 *              <property name="attributeColumnList">
 *                  <list>
 *                      <value>name</value>
 *                     <value>city</value>
 *                  </list>
 *              </property>
 *              <property name="subNodes">
 *                  <list>
 *                      <bean class="org.firewaterframework.mappers.jdbc.PivotTreeBuilder">
 *                          <property name="idColumn" value="pet"/>
 *                          <property name="urlPrefix" value="pets"/>
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
     * The name of the XML tag to generate for this resource.  If unset, the tag will default to the idColumn value.
     */
    protected String tagname;

    /**
     * This flag sets wheter or not to include the parent PivotTreeBuilder's URL as a prefix in this nodes generated
     * URL.
     */
    protected boolean subResource = true;

    /**
     * this property is used to select a name for the 'url' attribute for each node.  As
     * the builder recursively descends the subnodes, URLs are built up by concatenating
     * their id and urlPrefix values.
     */
    protected String urlPrefix;

    /**
     * this identifies which column in the result set to 'pivot' on.  It is typically
     * the primary key column of the underlying table/resource
     */
    protected String idColumn;

    /**
     * this maps the name of the column in the result set (the key) with the name of
     * the XML attribute in the result.  If the names are exactly the same, use the
     * convenience method setAttributeColumnList()
     */
    protected Map<String,String> attributeColumns;

    /**
     * these represent the PivotTreeBuilders that will create the subnodes in our
     * response XML document.  When we find adjacent rows with duplicate values for
     * the idColumn, delegate to the subNodes to process.
     */
    protected PivotTreeBuilder[] subNodes;

    public PivotTreeBuilder(){}

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
        Object topPivotValue = startRow.get(idColumn);
        Element rval = df.createElement( getTagname() );

        if( urlPrefix != null )
        {
            if( !subResource )
            {
                url = "";
            }
            url += '/' + urlPrefix + '/' + topPivotValue.toString();
            rval.addAttribute( "url", url );
        }

        // first, populate the current node with the properties and attributes pertaining to it
        processCurrentRow( startRow, rval );

        // process each subnode, loop through the current range for this node and recurse on each unique subnode
        if( subNodes != null )
        {
            for( PivotTreeBuilder subNode: subNodes )
            {
                String subNodeColumnName = subNode.getIdColumn();
                Set<Object> processedSubPivotValues = new HashSet<Object>();
                int rangeRowIndex = startRowIndex;
                while( rangeRowIndex < rows.size() && topPivotValue.equals( rows.get( rangeRowIndex ).get(idColumn)))
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
        while( rowNum[0] < rows.size() && topPivotValue.equals( rows.get( rowNum[0] ).get(idColumn)))
        {
            rowNum[0]++;
        }

        return rval;
    }

    protected void processCurrentRow( Map<String,Object> row, Element element )
    {
        Object pivotValue = row.get(idColumn);

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

    public String getIdColumn() {
        return idColumn;
    }

    @Required
    public void setIdColumn(String idColumn) {
        this.idColumn = idColumn;
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

    public String getTagname() {
        if( tagname == null )
        {
            return idColumn;
        }
        else
        {
            return tagname;
        }
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public boolean isSubResource() {
        return subResource;
    }

    public void setSubResource(boolean subResource) {
        this.subResource = subResource;
    }
}
