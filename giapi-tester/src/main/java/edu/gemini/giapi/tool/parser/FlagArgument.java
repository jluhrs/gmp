package edu.gemini.giapi.tool.parser;

/**
 * A flag argument definition. 
 */
public class FlagArgument extends AbstractArgument {

    public FlagArgument(String key) {
        super(key);
    }

    public boolean requireParameter() {
        return false;
    }

    public void parseParameter(String arg) {
        //no arguments here, this is just a flag
    }

    public String getInvalidArgumentMsg() {
        return null;
    }
}
