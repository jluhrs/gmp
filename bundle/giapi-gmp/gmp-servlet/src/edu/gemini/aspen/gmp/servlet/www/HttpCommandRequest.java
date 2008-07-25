package edu.gemini.aspen.gmp.servlet.www;

import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.commands.api.Activity;

import javax.servlet.http.HttpServletRequest;

/**
 * Process the servlet requests associated to a sequence command, and
 * gets the important arguments.
 */
public class HttpCommandRequest {


    public static final String SEQUENCE_COMMAND_PARAM = "sequenceCommand";
    public static final String ACTIVITY_PARAM = "activity";


    private SequenceCommand _sequenceCommand;
    private Activity _activity;

    public HttpCommandRequest(HttpServletRequest req) throws BadRequestException {


        String val = req.getParameter(SEQUENCE_COMMAND_PARAM);

        if (val == null) {
            throw new BadRequestException("Missing \"" + SEQUENCE_COMMAND_PARAM + "\" parameter");
        }

        try {
            _sequenceCommand = SequenceCommand.valueOf(val);
        } catch (IllegalArgumentException ex) {
             throw new BadRequestException("Illegal Sequence Command \"" + val + "\"");
        }

        val = req.getParameter(ACTIVITY_PARAM);

        if (val == null) {
            throw new BadRequestException("Missing \"" + ACTIVITY_PARAM + "\" parameter");
        }

        try {
            _activity = Activity.valueOf(val);
        } catch (IllegalArgumentException ex) {
             throw new BadRequestException("Illegal Activity \"" + val + "\"");
        }
    }
    

    public SequenceCommand  getSequenceCommand() {
        return _sequenceCommand;
    }

    public Activity getActivity() {
        return _activity;
    }


}
