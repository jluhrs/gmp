package edu.gemini.aspen.gmp.gw.jms;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.gmp.util.jms.MessageBuilder;

import javax.jms.*;
import javax.jms.IllegalStateException;
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

        try {
            MessageProducer producer = _session.createProducer(_destination);

            CompletionInformation info = new CompletionInformation(
                    response,
                    command,
                    activity,
                    config
            );

            Message reply = MessageBuilder.buildCompletionInformationMessage(_session, info);
            producer.send(reply);
            producer.close();
        } catch (InvalidDestinationException ex) {
            LOG.info("Client disconnected before receiving completion information: " + ex.getMessage());
        } catch (IllegalStateException ex) {
           LOG.info("Can't contact client to send completion information: " + ex.getMessage());
        } catch (JMSException ex) {
           LOG.info("Error sending completion information to clients: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
