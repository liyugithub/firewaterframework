package org.firewaterframework.mappers.validation;

/**
 * Created by IntelliJ IDEA.
 * User: tspurway
 * Date: Dec 12, 2007
 * Time: 11:48:11 PM
 * To change this template use File | Settings | File Templates.
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

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isQuote() {
        return quote;
    }

    public void setQuote(boolean quote) {
        this.quote = quote;
    }
}
