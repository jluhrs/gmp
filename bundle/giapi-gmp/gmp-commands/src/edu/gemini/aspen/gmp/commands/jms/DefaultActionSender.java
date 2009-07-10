package edu.gemini.aspen.gmp.commands.jms;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.commands.Action;
import edu.gemini.aspen.gmp.commands.ActionSender;
import edu.gemini.aspen.gmp.commands.ActionMessage;

import java.util.logging.Logger;

/**
 * The default mechanism to send actions.
 */
public class DefaultActionSender implements ActionSender {

    private static final Logger LOG = Logger.getLogger(DefaultActionSender.class.getName());

    private ActionMessageFactory _factory;

    public DefaultActionSender(ActionMessageFactory factory) {
        _factory = factory;
        LOG.info("Initializing Default Action Sender");
    }


    /**
     * Default sender for sequence commands other than apply.
     * Set the configuration in the body of the message, dispatch it over the network
     * and report back the handler response.
     * @param action the action containing the sequence command information
     * @return the HandlerResponse information
     */
    public HandlerResponse send(Action action) {

        ActionMessage m = _factory.create(action);

        m.setConfiguration(action.getConfiguration());

        return m.send();
    }
}
