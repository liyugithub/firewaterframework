package org.firewaterframework.util;

import java.io.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;

/**
 */
public class ZipUtil
{

    public static void zipDir( String dir2zip, String zipFilePath ) throws IOException
    {
        zipDir( new File( dir2zip ), new File( zipFilePath ));
    }

    public static void zipDir( File dir2zip, File zipFilePath ) throws IOException
    {
        ZipOutputStream zos = new ZipOutputStream( new FileOutputStream( zipFilePath ));

        // add the root level directory to the zip file - always add the 'relative' path
        zos.putNextEntry( new ZipEntry( dir2zip.getName() + '/' ));
        zipDir( dir2zip, zos );
        zos.close();
    }

    public static void unZipDir( File zipFile, File destinationPath ) throws IOException
    {
        ZipFile zip = new ZipFile( zipFile );
        Enumeration entries = zip.entries();
        File currentDirectory = destinationPath;

        // loop through all the elements of the zip file
        while( entries.hasMoreElements() )
        {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if( entry.isDirectory() )
            {
                currentDirectory = new File(currentDirectory, entry.getName());
                if( !currentDirectory.mkdir() )
                {
                    throw new IOException( "Failed creating zipfile component: " + currentDirectory.getAbsolutePath() );
                }
                continue;
            }

            // if we got here, we've got a 'normal' file - open it and read from the zip entry
            byte[] buffer = new byte[2156];
            int len;
            InputStream in = zip.getInputStream( entry );
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream( new File( currentDirectory, entry.getName())));

            while( (len = in.read( buffer )) >= 0 )
              out.write( buffer, 0, len );

            in.close();
            out.close();
        }
    }

    public static File createTempDir( ) throws IOException
    {
        File rval = File.createTempFile( "tmpdir", null );
        if( !(rval.delete()) || !(rval.mkdir()) )
        {
            throw new IOException( "Couldn't create temporary directory: " + rval.getAbsolutePath() );
        }
        return rval;
    }

    public static String contents( String filename ) throws IOException
    {
        return contents( new File( filename ));
    }

    public static String contents( File file ) throws IOException
    {
        return contents( new FileInputStream( file ));
    }

    public static String contents( InputStream in ) throws IOException
    {
        StringBuffer rval = new StringBuffer();
        BufferedReader reader = new BufferedReader( new InputStreamReader( in ));
        String inputLine;
        while( (inputLine = reader.readLine()) != null )
        {
            rval.append( inputLine );
        }
        return rval.toString();
    }

    protected static void zipDir( File zipDir, ZipOutputStream zos ) throws IOException
    {
        //get a listing of the directory content
        String[] dirList = zipDir.list();
        byte[] readBuffer = new byte[2156];

        //loop through dirList, and zip the files
        for( String file: dirList )
        {
            File f = new File( zipDir, file );

            if( f.isDirectory() )
            {
                ZipEntry anEntry = new ZipEntry( f.getName() + '/');
                zos.putNextEntry( anEntry );
                zipDir( f, zos );
            }
            else
            {

                ZipEntry anEntry = new ZipEntry( f.getName() );
                zos.putNextEntry( anEntry );

                FileInputStream fis = new FileInputStream( f );
                int bytesIn;
                while ( (bytesIn = fis.read( readBuffer )) != -1 )
                {
                    zos.write( readBuffer, 0, bytesIn );
                }

                //close the Stream
                fis.close();
            }
        }
    }
}
