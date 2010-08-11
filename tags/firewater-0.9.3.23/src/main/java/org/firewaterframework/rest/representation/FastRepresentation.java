package org.firewaterframework.rest.representation;

import com.google.common.collect.ArrayListMultimap;
import org.firewaterframework.WSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Jan 7, 2010
 * Time: 9:52:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class FastRepresentation extends AbstractRepresentation
{
    protected static final Logger log = LoggerFactory.getLogger( FastRepresentation.class );

    private ArrayListMultimap<String,Object> rep;
    private String name;

    public FastRepresentation()
    {
        this( "root" );
    }

    public FastRepresentation( String name )
    {
        this.name = name;
        this.rep = ArrayListMultimap.create();
    }

    @Override
    protected String getContent()
    {
        return rep.toString();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
       this.name = name;
    }

    public Representation addChild(String name)
    {
        FastRepresentation rval = new FastRepresentation( name );
        rep.put( name, rval.rep );
        return rval;
    }

    public void addChild(Representation representation)
    {
        if( representation instanceof FastRepresentation )
        {
            FastRepresentation other = (FastRepresentation)representation;
            rep.put( other.name, other.rep );
        }
        else
        {
            throw new WSException( "Cannot mix representations" );
        }
    }

    public void addAttribute(String key, Object value)
    {
        rep.put( key, value );
    }

    public Object getUnderlyingRepresentation()
    {
        return rep;
    }
}
