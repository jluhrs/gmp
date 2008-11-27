package edu.gemini.aspen.gmp.gw.jms;

import edu.gemini.aspen.gmp.commands.api.*;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;

import javax.jms.*;
import java.util.logging.Logger;

/**
 *
 */
public class GatewayCompletionListener implements CompletionListener {

    private static final Logger LOG = Logger.getLogger(GatewayCompletionListener.class.getName());

    private Destination _destination;
    private Session _session;

    public GatewayCompletionListener(Session session, Destination destination) {
        _destination = destination;
        _session = session;
    }


    public void onHandlerResponse(HandlerResponse response, SequenceCommand command, Activity activity, Configuration config) {

        //TODO: Send this information back to the requestor.
        LOG.info("Completion information received. To send back to the requestor using destination " + _destination);

        try {
            MessageProducer producer = _session.createProducer(_destination);
            MapMessage reply = _session.createMapMessage();

            //handler response info
            reply.setStringProperty(GmpKeys.GMP_HANDLER_RESPONSE_KEY, response.getResponse().name());
            if (response.getResponse() == HandlerResponse.Response.ERROR) {
                if (response.getMessage() != null) {
                    reply.setStringProperty(GmpKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
                }
            }

            reply.setStringProperty(GmpKeys.GMP_SEQUENCE_COMMAND_KEY, command.name());

            reply.setStringProperty(GmpKeys.GMP_ACTIVITY_KEY, activity.name());

            if (config != null) {
                for (ConfigPath path: config.getKeys()) {
                    reply.setString(path.toString(), config.getValue(path));
                }
            }
            producer.send(reply);
            LOG.info("Completion information sent!");
        } catch (JMSException ex) {
            LOG.info("Error receiving completion information: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
