package edu.gemini.aspen.gmp.servlet.www;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.SequenceCommand;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

/**
 * Process the servlet requests associated to a sequence command, and
 * gets the important arguments.
 */
public class HttpCommandRequest {

    public static final String SEQUENCE_COMMAND_PARAM = "sequenceCommand";
    public static final String ACTIVITY_PARAM = "activity";
    

    private SequenceCommand _sequenceCommand;
    private Activity _activity;
    private Configuration _configuration;

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


        //Configuration is formed by all the parameters, except ACTIVITY and SEQUENCE COMMAND

        Map<String, String> parameters = new HashMap<String, String>();

        Enumeration keys = req.getParameterNames();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            parameters.put(key, req.getParameter(key));
        }

        parameters.remove(ACTIVITY_PARAM);
        parameters.remove(SEQUENCE_COMMAND_PARAM);
        try {
            _configuration = ConfigurationParser.parse(parameters);
        } catch (IllegalArgumentException ex) {
            _configuration = null;
            throw new BadRequestException("Illegal Configuration \"" + val + "\"");
        }
    }
    

    public SequenceCommand  getSequenceCommand() {
        return _sequenceCommand;
    }

    public Activity getActivity() {
        return _activity;
    }

    public Configuration getConfiguration() {
        return _configuration;
    }

}
