package edu.gemini.giapi.tool.arguments;

import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

/**
 * Argument to define the value the application expects. It is compared verbatim
 */
public class ExpectedValueArgument extends AbstractArgument {

    private String _expectedValue;

    public ExpectedValueArgument() {
        super("expected");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        if (arg == null)  Util.die("I don't understand null expected value. Try -expected <expected-value>");
        _expectedValue = arg;
    }

    public String getInvalidArgumentMsg() {
        return "What is the expected value? Try -expected <expected-value>";
    }

    public String getExpectedValue() {
        return _expectedValue;
    }
}
