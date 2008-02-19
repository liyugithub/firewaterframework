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
/**
 * This PropertyEditor is a catch-all SQL Literal validator.  It will match any string, but if that string is
 * numeric, it will not wrap it in single quotes before output binding, and will wrap all other values in single
 * quotes.
 * @author Tim Spurway
 * 
 */
public class SqlLiteral extends MapPropertyEditor
{
    public void setAsText(String text) throws IllegalArgumentException
    {
        if( text.matches( "[0-9][0-9]*" ))
        {
            this.setValue( text );
        }
        else
        {
            this.setValue( '\'' + text.replaceAll( "\'", "''" ) + '\'' );
        }
    }

}
