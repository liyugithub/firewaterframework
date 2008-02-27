package org.firewaterframework.mappers.jdbc;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Feb 25, 2008
 * Time: 9:38:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceDescriptor
{
    /**
     * The name of the XML tag to generate for this resource.  If unset, the tag will default to the pivotAttribute value.
     */
    String tagname;

    /**
     * This flag sets wheter or not to include the parent PivotTreeBuilder's URL as a prefix in this nodes generated
     * URL.
     */
    boolean subResource = true;

    /**
     * this property is used to select a name for the 'url' attribute for each node.  As
     * the builder recursively descends the subnodes, URLs are built up by concatenating
     * their id and urlPrefix values.
     */
    String urlPrefix;

    /**
     * this identifies which column in the result set to 'pivot' on.  It is typically
     * the primary key column of the underlying table/resource
     */
    String pivotAttribute;

    List<String> attributes;

    public void setAttributesString( String csvString )
    {
        attributes = new ArrayList<String>();
        attributes.addAll(Arrays.asList(csvString.split(",")));
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public String getTagname()
    {
        if( tagname == null )
        {
            return pivotAttribute;
        }
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public boolean isSubResource() {
        return subResource;
    }

    public void setSubResource(boolean subResource) {
        this.subResource = subResource;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public String getPivotAttribute() {
        return pivotAttribute;
    }

    public void setPivotAttribute(String pivotAttribute) {
        this.pivotAttribute = pivotAttribute;
    }
}
