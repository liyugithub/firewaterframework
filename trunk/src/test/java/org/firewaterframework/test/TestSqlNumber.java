package org.firewaterframework.test;

import junit.framework.Assert;
import org.firewaterframework.mappers.validation.SqlNumber;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Aug 30, 2010
 * Time: 6:08:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSqlNumber extends Assert
{
    @Test
    public void testIt()
    {
        SqlNumber num = new SqlNumber();
        try
        {
            num.setAsText( "154" );
            assertTrue( "154".equals( num.getAsText() ));
            num.setAsText( "-274.344" );
            assertTrue( "-274.344".equals( num.getAsText() ));
            num.setAsText( "-375.39E-5" );
            assertTrue( "-375.39E-5".equals( num.getAsText() ));
        }
        catch( IllegalArgumentException e )
        {
            assertTrue( false );
        }
    }
}
