package org.firewaterframework.mappers.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * This PropertyEditor will validate a comma-separated list (CSV) argument into SQL literal values.  Each individual
 * value will be conditionally wrapped with single quotes depending on if the value is numeric or not.  Values
 * containing only numbers will not be wrapped, whereas values containing non-numeric values will be wrapped.
 * @author Tim Spurway
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