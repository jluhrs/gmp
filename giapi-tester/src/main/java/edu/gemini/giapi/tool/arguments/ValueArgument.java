package edu.gemini.giapi.tool.arguments;

import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

/**
 * A Sequence Command argument
 */
public class ValueArgument extends AbstractArgument {


    private String value;

    public ValueArgument() {
        super("value");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        value=arg;
    }

    public String getInvalidArgumentMsg() {
        return "What value? Try -value <value>";
    }

    public String getValue(){
        return value;
    }
}
