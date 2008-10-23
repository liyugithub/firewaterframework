package org.firewaterframework.rest.representation;

import org.firewaterframework.WSException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Oct 22, 2008
 * Time: 3:37:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapRepresentation extends AbstractRepresentation
{
    protected static final Log log = LogFactory.getLog( MapRepresentation.class );

    public class Entry
    {
        private String key;
        private Object value;

        public Entry( String key, Object value )
        {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    private Entry children;

    public MapRepresentation()
    {
        this( "root" );
    }

    public MapRepresentation( String name )
    {
        children = new Entry( name, new ArrayList<Entry>());
    }

    public String getName()
    {
        return children.getKey();
    }

    public void setName(String name)
    {
        children.setKey( name );
    }

    protected void addChild( String name, Object value )
    {
        ((List<Entry>)children.getValue()).add( new Entry( name, value ));
    }

    public void addChild(Representation child)
    {
        //need to make sure this is a MapRepresentation
        if( child instanceof MapRepresentation )
        {
            MapRepresentation mapChild = (MapRepresentation)child;
            ((List<Entry>)children.getValue()).add( mapChild.children );
        }
        else
        {
            throw new WSException( "Cannot mix representations" );
        }
    }

    public Representation addChild(String name)
    {
        Representation rval = new MapRepresentation( name );
        addChild( name, rval );
        return rval;
    }

    public void addAttribute(String key, Object value)
    {
        addChild( key, value );
    }

    protected String getContent() {
        StringBuffer buf = new StringBuffer();
        buf.append( children.getKey() ).append( ':' ).append( transcode( children ));
        return buf.toString();
    }

    private StringBuffer transcode( Object value )
    {
        StringBuffer rval = new StringBuffer( );

        if( value == null || value instanceof Number || value instanceof Boolean )
        {
            rval.append( value );
        }
        else if( value instanceof String )
        {
            rval.append( ((String)value).replace( "\"", "\\\"" ));
        }
        else if( value instanceof List)
        {
            List valueList = (List)value;

            // we can have another List<Entry> here - which would indicate { } - or another list - which indicates [ ]
            if( valueList.size() > 0 && valueList.get(0) instanceof Entry )
            {
                // assume all entries are instances of Entry
                rval.append( '{' );
                for( Entry entry: (List<Entry>)valueList )
                {
                    rval.append( entry.getKey() ).append( ':' ).append( transcode( entry.getValue() )).append( ',' );
                }
                rval.append( '}' );
            }
            else
            {
                rval.append( '[' );
                for( Object obj: valueList )
                {
                    rval.append( transcode( obj )).append( ',' );
                }
                rval.append( ']' );
            }
        }
        else
        {
            log.error( "Couldn't transcode value: " + value + " of type: " + value.getClass() + " ... continuing ..." );
        }
        return rval;
    }

    public Entry getUnderlyingRepresentation()
    {
        return children;
    }
}
