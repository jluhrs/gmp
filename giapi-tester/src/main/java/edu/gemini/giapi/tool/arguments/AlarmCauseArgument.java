package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.status.AlarmCause;
import edu.gemini.aspen.giapi.status.AlarmSeverity;
import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

/**
 * A Sequence Command argument
 */
public class AlarmCauseArgument extends AbstractArgument {


    private AlarmCause _cause;

    public AlarmCauseArgument() {
        super("cause");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
         try {
            _cause = AlarmCause.valueOf(arg.toUpperCase());
        } catch (IllegalArgumentException ex) {
            Util.die("Illegal Alarm Severity: " + arg + ".\nOptions are: " + Util.getValues(
                    AlarmCause.class));
        }
    }

    public String getInvalidArgumentMsg() {
        return "What alarm cause? Try -cause <cause>";
    }

    public AlarmCause getCause(){
        return _cause;
    }
}
