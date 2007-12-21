package org.firewaterframework.mappers;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.firewaterframework.WSException;
import org.firewaterframework.mappers.validation.MapDataBinder;
import org.firewaterframework.mappers.validation.MapPropertyEditor;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Response;
import org.firewaterframework.rest.Status;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is the central abstraction in Firewater for converting a REST Request into
 * a Response.  Subclasses must implement the handle method to actually perform the
 * transformation.
 * <p>
 * A Mapper may be configured to validate Request argments.  This is done by utilizing
 * {@link MapDataBinder}.
 *
 * @author Tim Spurway
 * @see Request
 * @see Response
 */
public abstract class Mapper
{
    protected static DocumentFactory documentFactory = DocumentFactory.getInstance();
    /**
     * Allows for 'declaring' of Request parameters, and for their validation
     */
    protected Map<String, MapPropertyEditor> fields;

    /**
     * Process a REST Request.  In subclasses this method will typically delegate to
     * the handle method of downstream Mapper objects (eg. {@link RouteMapper}), or will
     * actually handle the resource Request and generate a Response object (eg. {@link org.firewaterframework.mappers.jdbc.QueryMapper})
     * @param request the REST request to process
     * @return the REST response
     */
    public abstract Response handle( Request request );

    public Element getOptions( Request request )
    {
        Element rval = null;
        if( fields != null )
        {
            rval = documentFactory.createElement( "fields" );
            for( Map.Entry<String,MapPropertyEditor> entry: fields.entrySet() )
            {
                Element field = rval.addElement( "field" );
                field.addAttribute( "name", entry.getKey() );
                field.addAttribute( "propertyEditor", entry.getValue().toString() );
                Boolean required = entry.getValue().isRequired();
                field.addAttribute( "required", required.toString() );
            }
        }
        return rval;
    }
    /**
     * Set validation fields for the Request.  By default, Request arguments are simply
     * passed, unchecked, into the handle method for he Mapper.  A subclass may explicity
     * force the binding of Request arguments by calling the bind method.
     *
     * @param fields a Map keyed by the Request argument name who's value is the PropertyEditor
     * that will edit and validate this argument
     *
     * @see org.firewaterframework.mappers.validation.MapPropertyEditor
     */
    public void setFields( Map<String, MapPropertyEditor> fields )
    {
        this.fields = fields;
    }

    /**
     * Return the MapDataBinder for this Mapper.  Used in conjuction with the bind method
     * to allow for optional validation and binding of Request arguments before handle processing.
     * @return the MapDataBinder for this Mapper
     * @see DataBinder
     *
     */
    protected MapDataBinder getDataBinder()
    {
        MapDataBinder dataBinder = new MapDataBinder( new HashMap<String,Object>() );
        if( fields != null )
        {
            for( Map.Entry<String,MapPropertyEditor> entry: fields.entrySet() )
            {
                dataBinder.registerCustomEditor( String.class, entry.getKey(), entry.getValue() );
            }
        }
        
        return dataBinder;
    }

    /**
     * Performs binding of Request arguments.
     *
     * @param request the REST Request containing arguments to be bound
     * @return a Map of the bound and validated Request arguments
     * @throws WSException on failed validation
     * @see org.firewaterframework.mappers.validation.MapPropertyEditor
     */
    protected Map<String,Object> bind( Request request ) throws WSException
    {
        DataBinder dataBinder = getDataBinder();
        dataBinder.bind( request.getArgs() );
        if( dataBinder.getBindingResult().getErrorCount() > 0 )
        {
            StringBuffer errors = new StringBuffer( "Request Parameter error: " );
            for( ObjectError error: (List<ObjectError>)dataBinder.getBindingResult().getAllErrors() )
            {
                errors.append( error.toString() ).append( ',' );
            }
            throw new WSException( errors.toString(), Status.STATUS_SERVER_ERROR );
        }
        return (Map<String,Object>)dataBinder.getBindingResult().getTarget();
    }
}
