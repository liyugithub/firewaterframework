package org.firewaterframework.mappers.util

import org.firewaterframework.mappers.validation.MapPropertyEditor

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Nov 14, 2008
 * Time: 4:17:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class PatternArg
{
    String name
    MapPropertyEditor propertyEditor

    PatternArg( String patternArg )
    {
        // pattern args look like name[:number|string|literal|stringList|literalList|numberList]
    }
}