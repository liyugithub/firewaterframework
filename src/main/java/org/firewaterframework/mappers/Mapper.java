package org.firewaterframework.mappers;

import org.firewaterframework.rest.Response;
import org.firewaterframework.rest.Request;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 3, 2007
 * Time: 11:44:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Mapper
{
    Response handle( Request request );
}
