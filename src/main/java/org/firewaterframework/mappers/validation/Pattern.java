package org.firewaterframework.mappers.validation;

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
