package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.data.ObservationEvent;
import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

/**
 * A Sequence Command argument
 */
public class SendObsEventArgument extends AbstractArgument {
    private ObservationEvent obsEvent;

    public SendObsEventArgument() {
        super("sendObsEvent");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        try {
            obsEvent = ObservationEvent.valueOf(arg);
        } catch (IllegalArgumentException ex) {
            Util.die("Illegal observation event: " + arg + ".\nOptions are: " + Util.getValues(
                    ObservationEvent.class));
        }
    }

    public String getInvalidArgumentMsg() {
        return "What sequence command? Try -sendObsEvent <ObservationEvent>";
    }

    public ObservationEvent getObservationEvent() {
        return obsEvent;
    }

}
