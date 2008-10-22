package org.firewaterframework.mappers.jdbc;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.firewaterframework.util.PrettyDocumentFactory;

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
 * the <code>/users</code> URL):
 * <p>
 * <code><pre>
 * <bean id="usersGetMapper" class="org.firewaterframework.mappers.jdbc.QueryMapper">
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
 *                          <property name="columnMappings">
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
 * </pre></code>
 * <p>
 * After a sucessful call to the GET method on the <code>/users</code> resource.  We
 * would get a Response with an XML payload like:
 * <p>
 * <code>
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
 * </result>
 * </code>
 */
public class PivotTreeBuilder
{
    protected static final Log log = LogFactory.getLog( PivotTreeBuilder.class );
    protected ResourceDescriptor resourceDescriptor;
    protected Map<String,String> columnMappings;
    protected static final PrettyDocumentFactory df = PrettyDocumentFactory.getInstance();

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
        int startRowIndex = rowNum[0];
        Map<String,Object> startRow = rows.get( startRowIndex );
        Object topPivotValue = startRow.get( resourceDescriptor.pivotAttribute);
        Element rval = df.createElement( resourceDescriptor.getTagname() );

        if( resourceDescriptor.urlPrefix != null )
        {
            if( !resourceDescriptor.subResource )
            {
                url = "";
            }
            url += '/' + resourceDescriptor.urlPrefix + '/' + topPivotValue.toString();
            rval.addAttribute( "url", url );
        }

        // first, populate the current node with the properties and attributes pertaining to it
        processCurrentRow( startRow, rval, url );

        // process each subnode, loop through the current range for this node and recurse on each unique subnode
        if( subNodes != null )
        {
            for( PivotTreeBuilder subNode: subNodes )
            {
                String subNodeColumnName = subNode.resourceDescriptor.getPivotAttribute();
                Set<Object> processedSubPivotValues = new HashSet<Object>();
                int rangeRowIndex = startRowIndex;
                while( rangeRowIndex < rows.size() && topPivotValue.equals( rows.get( rangeRowIndex ).get(resourceDescriptor.pivotAttribute)))
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
        while( rowNum[0] < rows.size() && topPivotValue.equals( rows.get( rowNum[0] ).get(resourceDescriptor.pivotAttribute)))
        {
            rowNum[0]++;
        }

        return rval;
    }

    protected void processCurrentRow( Map<String,Object> row, Element element, String url )
    {
        Object pivotValue = row.get(resourceDescriptor.pivotAttribute);

        if( pivotValue != null )
        {
            // include the 'id' attribute
            element.addAttribute( "id", pivotValue.toString() );

            // now process the attributes
            if( resourceDescriptor.attributes != null )
            {
                for( String attribute: resourceDescriptor.attributes )
                {
                    // check if there is a column mapping
                    String columnName = attribute;
                    if( columnMappings != null && columnMappings.containsKey( attribute ))
                    {
                        columnName = columnMappings.get( attribute );
                    }

                    // check if there is a value in the row for this attribute
                    Object attributeValue = row.get( columnName );
                    if( attributeValue != null )
                    {
                        element.addAttribute( attribute, attributeValue.toString() );
                    }
                }
            }

            if( resourceDescriptor.relativeReferences != null )
            {
                // process relative references
                for( Map.Entry<String,String> relativeRef: resourceDescriptor.relativeReferences.entrySet() )
                {
                    element.addAttribute( relativeRef.getKey() + "URL", url + '/' + relativeRef.getValue() );
                }
            }
        }
    }

    public Map<String,String> getColumnMappings() {
        return columnMappings;
    }

    public void setColumnMappings(Map<String,String> columnMappings) {
        this.columnMappings = columnMappings;
    }

    /**
     * Parse a list of comma-separated key:value pairs.  The result set
     * column should be before the colon and the name of the resource attribute for
     * the resulting XML should be after.
     *
     * eg.  setAttributeColumnString( "owner-name:name,creation-date:date,owner-age:age" )
     * @param columnMappingsCsv
     */
    public void setColumnMappingsString( String columnMappingsCsv )
    {
        this.columnMappings = new HashMap<String,String>();
        String[] keyValues = columnMappingsCsv.split( "," );
        for( String keyValue: keyValues )
        {
            String[] entry = keyValue.split( ":" );
            if( entry.length > 1 )
            {
                this.columnMappings.put( entry[1], entry[0] );
            }
        }
    }

    public PivotTreeBuilder[] getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(PivotTreeBuilder[] subNodes)
    {
        this.subNodes = subNodes;
    }

    public ResourceDescriptor getResourceDescriptor() {
        return resourceDescriptor;
    }

    public void setResourceDescriptor(ResourceDescriptor resourceDescriptor) {
        this.resourceDescriptor = resourceDescriptor;
    }
}
