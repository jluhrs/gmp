package edu.gemini.giapi.tool.arguments;

import edu.gemini.giapi.tool.parser.FlagArgument;

/**
 * The flag argument to monitor for file events
 */
public class MonitorFileEventsArgument extends FlagArgument {
    public MonitorFileEventsArgument() {
        super("fileEvents");
    }
}
