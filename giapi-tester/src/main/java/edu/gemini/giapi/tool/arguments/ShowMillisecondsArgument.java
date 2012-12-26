package edu.gemini.giapi.tool.arguments;

import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

/**
 * Argument to define if a timestamp in miliseconds should be displayed
 */
public class ShowMillisecondsArgument extends AbstractArgument {

    private boolean _expectedValue = false;

    public ShowMillisecondsArgument() {
        super("millis");
    }

    public boolean requireParameter() {
        return false;
    }

    public void parseParameter(String arg) {
        _expectedValue = true;
    }

    public String getInvalidArgumentMsg() {
        return "What is the expected value? Try -expected <expected-value>";
    }

    public Boolean getExpectedValue() {
        return _expectedValue;
    }
}
