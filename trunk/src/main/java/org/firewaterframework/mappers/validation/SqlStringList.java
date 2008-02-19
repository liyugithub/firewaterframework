package org.firewaterframework.mappers.validation;
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
import java.util.ArrayList;
import java.util.List;

/**
 * This PropertyEditor will validate a comma-separated list (CSV) argument into SQL string values.  Each individual
 * value will bewrapped with single quotes.
 * @author Tim Spurway
 */
public class SqlStringList extends MapPropertyEditor
{
    public void setAsText(String text) throws IllegalArgumentException
    {
        List<String> newValue = new ArrayList<String>();
        String[] segments = text.split( "," );
        for( String segment: segments )
        {
            String stripSegment = segment.trim();
            newValue.add( '\'' + stripSegment.replaceAll( "\'", "''" )+ '\'' );
        }
        setValue( newValue );
    }
}