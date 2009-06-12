package org.firewaterframework.test;

import junit.framework.Assert;
import org.junit.Test;
import org.firewaterframework.util.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Jun 11, 2009
 * Time: 4:26:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestZipUtil  extends Assert
{
    @Test
    public void testZipDir() throws IOException
    {
        // create the directory to zip, with two files, and a subdir with one file
        File sourceDir = ZipUtil.createTempDir();
        createFile( sourceDir, "a.txt", "all the good stuff" );
        createFile( sourceDir, "b.txt", "is in text files" );
        File c = new File( sourceDir,"c" );
        if( !c.mkdir() ) throw new IOException( "Error creating directory 'c'" );
        createFile( c, "d.txt", "and sometimes in sub-directories" );

        // create the zip file
        File zipFile = File.createTempFile( "zip", "zip" );
        ZipUtil.zipDir( sourceDir, zipFile );

        // test that the zip file looks like it should...
        ZipFile zip = new ZipFile( zipFile );
        Enumeration entries = zip.entries();
        ZipEntry entry = (ZipEntry) entries.nextElement();
        assertTrue( entry.isDirectory() );
        entry = (ZipEntry) entries.nextElement();
        assertFalse( entry.isDirectory() );
        assertEquals( "all the good stuff", ZipUtil.contents( zip.getInputStream( entry )));
        assertTrue( entry.getName().endsWith( "a.txt" ));
        entry = (ZipEntry) entries.nextElement();
        assertFalse( entry.isDirectory() );
        entry = (ZipEntry) entries.nextElement();
        assertTrue( entry.isDirectory() );
        assertTrue( entry.getName().endsWith( "c/" ));
        entry = (ZipEntry) entries.nextElement();
        assertFalse( entry.isDirectory() );
        assertEquals( "and sometimes in sub-directories", ZipUtil.contents( zip.getInputStream( entry )));
        assertTrue( entry.getName().endsWith( "d.txt" ));

        // let's move the original directory
        if( !sourceDir.renameTo( new File(sourceDir.getAbsolutePath() + ".old" ))) throw new IOException( "Couldn't rename original dir" );

        // now expand the zip file and make sure all the files and dirs are there
        ZipUtil.unZipDir( zipFile, File.createTempFile( "tmp", "tmp").getParentFile() );  // in '/tmp'

        assertTrue( sourceDir.exists() );

        // walk through the directory and make sure everything is there
        File[] fileList = sourceDir.listFiles();
        assertEquals( 3, fileList.length );
        assertTrue( fileList[0].getName().endsWith( "a.txt" ));
        assertTrue( fileList[1].getName().endsWith( "b.txt" ));
        assertTrue( fileList[2].getName().endsWith( "c" ));
        assertEquals( "all the good stuff", ZipUtil.contents( fileList[0] ));

        fileList = fileList[2].listFiles();
        assertEquals( 1, fileList.length );
        assertTrue( fileList[0].getName().endsWith( "d.txt" ));
        assertEquals( "and sometimes in sub-directories", ZipUtil.contents( fileList[0] ));

    }

    private void createFile( File dir, String filename, String content ) throws IOException
    {
        BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( new File( dir, filename )));
        out.write( content.getBytes() );
        out.close();
    }
}
