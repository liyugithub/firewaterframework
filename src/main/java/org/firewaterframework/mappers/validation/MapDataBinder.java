package org.firewaterframework.mappers.validation;
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
import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.validation.DataBinder;

import java.util.Map;

/**
 * This class extends Spring's {@link DataBinder} to allow for binding onto Maps, instead of just POJOs.
 * @author tspurway
 */
public class MapDataBinder extends DataBinder
{
    protected MapPropertyBindingResult myBindingResult;

    public MapDataBinder( Map target )
    {
        super( target );
    }

    @Override
    protected AbstractPropertyBindingResult getInternalBindingResult() {
        if (this.myBindingResult == null)
        {
            myBindingResult = new MapPropertyBindingResult( (Map)getTarget(), getObjectName() );
        }
        return this.myBindingResult;
    }

}
