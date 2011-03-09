package edu.gemini.aspen.gmp.gw.jms;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionInformation;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;

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

    @Override
    public void onHandlerResponse(HandlerResponse response, Command command) {
        try {
            MessageProducer producer = _session.createProducer(_destination);

            CompletionInformation info = new CompletionInformation(
                    response,
                    command.getSequenceCommand(),
                    command.getActivity(),
                    command.getConfiguration()
            );

            Message reply = MessageBuilder.buildCompletionInformationMessage(_session, info);
            producer.send(reply);
            producer.close();
        } catch (InvalidDestinationException ex) {
            LOG.info("Client disconnected before receiving completion information: " + ex.getMessage());
        } catch (javax.jms.IllegalStateException ex) {
           LOG.info("Can't contact client to send completion information: " + ex.getMessage());
        } catch (JMSException ex) {
           LOG.info("Error sending completion information to clients: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void onHandlerResponse(HandlerResponse response, SequenceCommand command, Activity activity, Configuration config) {
        onHandlerResponse(response, new Command(command, activity, config));
    }
}
