package edu.gemini.giapi.tool.arguments;

import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

/**
 * Timeout argument; used to express a period of time
 * (in milliseconds) for an operation to complete.
 */
public class TimeoutArgument extends AbstractArgument {

    private long timeout = 0;

    public TimeoutArgument() {
        super("timeout");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        try {
            timeout = Long.parseLong(arg);
        } catch (NumberFormatException ex) {
            Util.die("Timeout is a long integer number. Try -timeout <timeout>");
        }
        if (timeout < 0) {
            Util.die("The timeout must be zero or a positive number. Please try again");
        }
    }

    public String getInvalidArgumentMsg() {
        return "What is the timeout? Try -timeout <timeout>";
    }

    public long getTimeout() {
        return timeout;
    }
}
