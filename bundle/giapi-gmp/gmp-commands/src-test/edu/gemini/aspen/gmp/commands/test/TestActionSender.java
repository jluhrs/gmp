package edu.gemini.aspen.gmp.commands.test;

import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;

/**
 * A Stub Action Sender, where you can define the expected answer. 
 */
public class TestActionSender implements ActionSender {

    private HandlerResponse _response;

    public void defineAnswer(HandlerResponse response) {
        _response = response;
    }

    public HandlerResponse send(ActionMessage message) {
        //for testing, don't send anything, just reply with the predefined answer...
        return _response;
    }
}
