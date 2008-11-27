package edu.gemini.aspen.gmp.gw.jms;

import edu.gemini.aspen.gmp.commands.api.*;

import javax.jms.Destination;
import java.util.logging.Logger;

/**
 *
 */
public class GatewayCompletionListener implements CompletionListener {

    private static final Logger LOG = Logger.getLogger(GatewayCompletionListener.class.getName());
    private Destination _destination;

    public GatewayCompletionListener(Destination destination) {
        _destination = destination;
    }


    public void onHandlerResponse(HandlerResponse response, SequenceCommand command, Activity activity, Configuration config) {

        //TODO: Send this information back to the requestor.
        LOG.info("Completion information received. To send back to the requestor using destination " + _destination);


    }
}
