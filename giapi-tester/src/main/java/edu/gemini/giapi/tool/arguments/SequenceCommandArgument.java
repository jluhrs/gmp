package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

/**
 * A Sequence Command argument
 */
public class SequenceCommandArgument extends AbstractArgument {
    private SequenceCommand sc;

    public SequenceCommandArgument() {
        super("sc");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        try {
            sc = SequenceCommand.valueOf(arg);
        } catch (IllegalArgumentException ex) {
            Util.die("Illegal sequence command: " + arg + ".\nOptions are: " + Util.getValues(
                    SequenceCommand.class));
        }
    }

    public String getInvalidArgumentMsg() {
        return "What sequence command? Try -sc <command>";
    }

    public SequenceCommand getSequenceCommand() {
        return sc;
    }

}
