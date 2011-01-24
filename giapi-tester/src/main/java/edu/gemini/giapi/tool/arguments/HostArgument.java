package edu.gemini.giapi.tool.arguments;

import edu.gemini.giapi.tool.parser.AbstractArgument;

/**
 * The argument to define the host where the GMP is located
 */
public class HostArgument extends AbstractArgument {

    private String _host = "localhost";

    public HostArgument() {
        super("h");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        _host = arg;
    }

    public String getInvalidArgumentMsg() {
        return "What host? Try -h <hostname>";
    }

    public String getHost() {
        return _host;
    }
}
