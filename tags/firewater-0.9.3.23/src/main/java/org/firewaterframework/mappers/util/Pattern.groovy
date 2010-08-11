package org.firewaterframework.mappers.util
/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Nov 14, 2008
 * Time: 4:03:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pattern
{
    String rootPath
    PatternArg[] qualifiers
    String[] subPaths
    PatternArg[] filters

    Pattern( String pattern )
    {
        String[] pathAndFilter = pattern.split( '?' )
        if( pathAndFilter.length > 0 )
        {
            // parse out the path part
            String[] path = pathAndFilter[0].split( '/' )
            if( path.length > 0 )
            {
                rootPath = path[0]
                if( path.length > 1 )
                {

                }
            }
        }
        else
        {
            //error
        }
    }
}