package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.status.AlarmSeverity;
import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

/**
 * A Sequence Command argument
 */
public class AlarmSeverityArgument extends AbstractArgument {


    private AlarmSeverity _severity;

    public AlarmSeverityArgument() {
        super("severity");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
         try {
            _severity = AlarmSeverity.valueOf(arg.toUpperCase());
        } catch (IllegalArgumentException ex) {
            Util.die("Illegal Alarm Severity: " + arg + ".\nOptions are: " + Util.getValues(
                    AlarmSeverity.class));
        }
    }

    public String getInvalidArgumentMsg() {
        return "What alarm severity? Try -severity <severity>";
    }

    public AlarmSeverity getSeverity(){
        return _severity;
    }
}
