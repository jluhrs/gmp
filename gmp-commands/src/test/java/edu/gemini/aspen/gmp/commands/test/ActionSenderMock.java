package edu.gemini.aspen.gmp.commands.test;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.ActionSender;

/**
 * A Stub Action Sender, where you can define the expected answer. 
 */
public class ActionSenderMock implements ActionSender {
    private final HandlerResponse _response;

    public ActionSenderMock(HandlerResponse response) {
        Preconditions.checkNotNull(response, "Handler Response cannot be null");
        _response = response;
    }


    public HandlerResponse send(ActionMessage message) {
        return send(message, 0);
    }

    @Override
    public HandlerResponse send(ActionMessage message, long timeout) {
        //for testing, don't send anything, just reply with the predefined answer...
        return _response;
    }
}
