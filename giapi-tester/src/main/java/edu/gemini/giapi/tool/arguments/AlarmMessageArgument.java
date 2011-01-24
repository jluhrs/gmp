package edu.gemini.giapi.tool.arguments;

import edu.gemini.giapi.tool.parser.AbstractArgument;

/**
 * A Sequence Command argument
 */
public class AlarmMessageArgument extends AbstractArgument {


    private String value;

    public AlarmMessageArgument() {
        super("message");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        value=arg;
    }

    public String getInvalidArgumentMsg() {
        return "What message? Try -message <message>";
    }

    public String getMessage(){
        return value;
    }
}
