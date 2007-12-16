package org.firewaterframework.mappers.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 13, 2007
 * Time: 8:14:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQLLiteralList extends MapPropertyEditor
{
    public void setAsText(String text) throws IllegalArgumentException
    {
        List<String> newValue = new ArrayList<String>();
        String[] segments = text.split( "," );
        for( String segment: segments )
        {
            String stripSegment = segment.trim();
            if( stripSegment.matches( "[0-9][0-9]*" ))
            {
                newValue.add( stripSegment );
            }
            else
            {
                newValue.add( '\'' + stripSegment + '\'' );
            }
        }
        setValue( newValue );
    }
}