package org.firewaterframework.mappers.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * This PropertyEditor will validate a comma-separated list (CSV) argument into SQL string values.  Each individual
 * value will bewrapped with single quotes.
 * @author Tim Spurway
 */
public class SQLStringList extends MapPropertyEditor
{
    public void setAsText(String text) throws IllegalArgumentException
    {
        List<String> newValue = new ArrayList<String>();
        String[] segments = text.split( "," );
        for( String segment: segments )
        {
            String stripSegment = segment.trim();
            newValue.add( '\'' + stripSegment + '\'' );

        }
        setValue( newValue );
    }
}