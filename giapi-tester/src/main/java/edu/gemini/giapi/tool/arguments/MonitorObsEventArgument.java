package edu.gemini.giapi.tool.arguments;

import edu.gemini.giapi.tool.parser.FlagArgument;

/**
 * Argument representing a flag to set the event observation loop
 */
public class MonitorObsEventArgument extends FlagArgument {

    public MonitorObsEventArgument() {
        super("obsEvents");
    }
}
