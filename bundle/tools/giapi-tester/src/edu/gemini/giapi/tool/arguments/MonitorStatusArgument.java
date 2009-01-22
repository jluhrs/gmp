package edu.gemini.giapi.tool.arguments;

import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

/**
 * Argument to monitor value of an status
 */
public class MonitorStatusArgument extends AbstractArgument {

    private String _statusName;

    public MonitorStatusArgument() {
        super("monitor");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        if (arg == null)  Util.die("I don't understand null status name. Try -monitor <status-name>");

        _statusName = arg;

    }

    public String getInvalidArgumentMsg() {
        return "What is the status to monitor? Try -monitor <status-name>";
    }

    public String getStatusName() {
        return _statusName;
    }
}
