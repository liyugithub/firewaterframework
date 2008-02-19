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
 * This PropertyEditor is the most general one defined for the framework.  It will match the incoming value
 * with a configured regular expression and fail if they don't match.  Additionally, it allows the output string
 * to be conditionally quoted (with single quotes) as it is bound to the output.
 * @see java.util.regex.Pattern
 * @author Tim Spurway
 */
public class Pattern extends MapPropertyEditor
{
    protected String pattern;
    protected boolean quote = false;

    public void setAsText(String text) throws IllegalArgumentException
    {
        if( text.matches( pattern ))
        {
            this.setValue( quoteText( text ));
        }
        else
        {
            throw new IllegalArgumentException( "Text: " + text + " does not match specified pattern: " + pattern );
        }
    }

    protected String quoteText( String text )
    {
        if( quote )
        {
            text = text.replaceAll( "\'", "''" );
            return '\'' + text + '\'';
        }
        else
        {
            return text;
        }
    }

    @Override
    public MapPropertyEditor copy()
    {
        Pattern rval = (Pattern)super.copy();
        rval.setPattern( pattern );
        rval.setQuote( quote );
        return rval;
    }

    @Override
    public String toString()
    {
        return "Pattern: " + pattern + " quoted: " + quote;
    }
    
    public String getPattern() {
        return pattern;
    }

    /**
     *
     * @param pattern the regular expression {@link Pattern} string to be used for validation.
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isQuote() {
        return quote;
    }

    /**
     *
     * @param quote indicated whether or not to wrap sucessfully validated results in single quotes before
     * binding to the output.
     */
    public void setQuote(boolean quote) {
        this.quote = quote;
    }
}
