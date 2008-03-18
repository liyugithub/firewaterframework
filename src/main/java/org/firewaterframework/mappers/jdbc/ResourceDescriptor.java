package org.firewaterframework.mappers.jdbc;

import java.util.*;

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

    /**
     * The references are links to other resources and come in two forms: absolute and relative.  An absolute
     * reference will point to the base URL of the resource, while the relative reference URL will be build
     * by adding a suffix to the current resource's URL.
     */
    Map<String,ResourceDescriptor> absoluteReferences;
    Map<String,String> relativeReferences;

    List<String> attributes;

    //TODO: we need to trim the whitespace off of the attributeString
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

    public Map<String, ResourceDescriptor> getAbsoluteReferences() {
        return absoluteReferences;
    }

    public void setAbsoluteReferences(Map<String, ResourceDescriptor> absoluteReferences) {
        this.absoluteReferences = absoluteReferences;
    }

    public void setRelativeReferencesString( String csvString )
    {
        relativeReferences = new HashMap<String,String>();
        for( String ref: Arrays.asList(csvString.split(",")) )
        {
            String[] mapElements = ref.split( ":" );
            if( mapElements.length > 1 )
            {
                relativeReferences.put( mapElements[0].trim(), mapElements[1].trim() );
            }
            else
            {
                relativeReferences.put( mapElements[0].trim(), mapElements[0].trim() );
            }
        }
    }

    public Map<String, String> getRelativeReferences() {
        return relativeReferences;
    }

    public void setRelativeReferences(Map<String, String> relativeReferences) {
        this.relativeReferences = relativeReferences;
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
