package edu.gemini.aspen.gmp.gw.jms;

import edu.gemini.aspen.gmp.commands.api.CommandSender;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.commands.api.CompletionListener;
import edu.gemini.aspen.gmp.util.jms.GmpJmsUtil;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *  This class receives and processes command requests from clients.  
 */
public class CommandConsumer implements ExceptionListener, MessageListener {

    private static final Logger LOG = Logger.getLogger(CommandConsumer.class.getName());

    private CommandSender _service;

    private Connection _connection;

    private Session _session;

    private MessageConsumer _consumer;


    public CommandConsumer(JmsProvider provider, CommandSender service) {

        _service = service;
        try {
            ConnectionFactory  factory = provider.getConnectionFactory();
            _connection = factory.createConnection();
            _connection.setClientID("Gateway Command Consumer");
            _connection.start();
            _connection.setExceptionListener(this);
            _session = _connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            //Commands come from a predefined topic
            Destination destination = _session.createTopic(
                    GmpKeys.GW_COMMAND_TOPIC);
            _consumer = _session.createConsumer(destination);
            _consumer.setMessageListener(this);
            LOG.info("Gateway Command Consumer Started");
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
    }

    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on Gateway Command Consumer: ", e);
    }

    public void onMessage(Message message) {

        if (message instanceof MapMessage) {

            try {
                CommandMessage commandMessage =
                        new CommandMessage((MapMessage)message);

                CompletionListener completionListener =
                        new GatewayCompletionListener(_session,
                                message.getJMSReplyTo());

                HandlerResponse response = _service.sendSequenceCommand(
                        commandMessage.getSequenceCommand(),
                        commandMessage.getActivity(),
                        commandMessage.getConfiguration(),
                        completionListener
                );

                //send response back to the client
                sendResponse(message.getJMSReplyTo(), response);
            } catch (FormatException e) {
                LOG.log(Level.WARNING, "Message did not contain a command message: " + e.getMessage());
            } catch (JMSException e) {
                //this is produced when sending reply back to the client
                LOG.log(Level.WARNING, "Problem sending response back to client ", e);
            }
        }
    }


    private void sendResponse(Destination destination, HandlerResponse response) throws JMSException {

        MessageProducer producer = _session.createProducer(destination);
        Message reply = GmpJmsUtil.buildHandlerResponseMessage(_session, response);
        producer.send(reply);
        producer.close();
    }

    public void close() {
        try {
            _consumer.close();
            _session.close();
            _connection.close();
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem closing command consumer ", e);
        }
    }

}
