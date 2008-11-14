package org.firewaterframework.mappers;
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
import org.firewaterframework.WSException;
import org.firewaterframework.mappers.validation.MapDataBinder;
import org.firewaterframework.mappers.validation.MapPropertyEditor;
import org.firewaterframework.rest.Request;
import org.firewaterframework.rest.Response;
import org.firewaterframework.rest.Status;
import org.firewaterframework.rest.representation.Representation;
import org.firewaterframework.rest.representation.XMLRepresentation;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.beans.MutablePropertyValues;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    protected static final Log log = LogFactory.getLog( Mapper.class );
    /**
     * Allows for 'declaring' of Request parameters, and for their validation
     */
    protected Map<String, MapPropertyEditor> fields;
    protected Map<String, Class> representations;
    protected Class preferredRepresentation = XMLRepresentation.class;

    /**
     * Process a REST Request.  In subclasses this method will typically delegate to
     * the handle method of downstream Mapper objects (eg. {@link RouteMapper}), or will
     * actually handle the resource Request and generate a Response object (eg. {@link org.firewaterframework.mappers.jdbc.QueryMapper})
     * @param request the REST request to process
     * @return the REST response
     */
    public abstract Response handle( Request request );

    public Representation getRepresentation( Request request )
    {
        try
        {
            if( representations != null && representations.size() > 0 )
            {
                String acceptString = request.getHeaders().get( Request.Header.Accept );

                if( acceptString != null )
                {
                    // parse this - the accept header is a comma separated list of Mime types
                    String[] mimes = acceptString.split( "," );

                    // return the first one we find that matches our representations
                    for( String mime: mimes )
                    {
                        if( representations.containsKey( mime ))
                        {
                            return (Representation)representations.get( mime ).newInstance();
                        }
                    }
                }
            }
            return (Representation)preferredRepresentation.newInstance();
        }
        catch( Exception e )
        {
            throw new WSException( "Error creating Representation for Mapper: ", e );
        }

    }

    public void setRepresentations( Map<String,Class> representations )
    {
        this.representations = representations;
    }

    public Class getPreferredRepresentation() {
        return preferredRepresentation;
    }

    public void setPreferredRepresentation(Class preferredRepresentation) {
        this.preferredRepresentation = preferredRepresentation;
    }

    public Representation getOptions( Request request )
    {
        Representation rval = getRepresentation( request );
        if( fields != null )
        {
            rval.setName( "fields" );
            for( Map.Entry<String,MapPropertyEditor> entry: fields.entrySet() )
            {
                Representation field = rval.addChild( "field" );
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

        // all declared fields should be bound - so create a new Map with the values of the incoming args.  This will
        // also ensure that any undeclared args in the request are ignored by the handling mapper.
        /*MutablePropertyValues args = new MutablePropertyValues();
        for( String field: fields.keySet() )
        {
            PropertyValue value = request.getArgs().getPropertyValue( field );
            if( value == null )
            {
                args.addPropertyValue( field, null );
            }
            else
            {
                args.addPropertyValue( value );
            }
        }*/
        
        DataBinder dataBinder = getDataBinder();
        dataBinder.bind( new MutablePropertyValues( request.getArgs() ));
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
