package edu.gemini.aspen.gmp.broker.jms;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.broker.commands.Action;
import edu.gemini.aspen.gmp.broker.commands.ActionSender;

/**
 * An Action Sender specific to handle the complexity of the Apply Sequence Command.
 */
public class ApplyActionSender implements ActionSender {

    public HandlerResponse send(Action action) {
        return null;  
    }
}
