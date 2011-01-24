package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

/**
 * A Sequence Command argument
 */
public class TypeArgument extends AbstractArgument {


    private SetStatusArgument.StatusType type;

    public TypeArgument() {
        super("type");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        try {
            type = SetStatusArgument.StatusType.parse(arg);
        } catch (IllegalArgumentException ex) {
            Util.die("Illegal type: " + arg + ".\nOptions are: " + Util.getValues(
                    SetStatusArgument.StatusType.class));
        }
    }

    public String getInvalidArgumentMsg() {
        return "What type? Try -type <type>";
    }

    public SetStatusArgument.StatusType getType(){
        return type;
    }
}
