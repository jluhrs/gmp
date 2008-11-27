package edu.gemini.aspen.gmp.gw.jms;

import edu.gemini.aspen.gmp.broker.api.GMPService;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 */
public class CommandConsumer implements ExceptionListener, MessageListener {

    private static final Logger LOG = Logger.getLogger(CommandConsumer.class.getName());

    private GMPService _service;

    private Connection _connection;

    private Session _session;

    private MessageConsumer _consumer;


    public CommandConsumer(JmsProvider provider, GMPService service) {

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
                    GatewayKeys.COMMAND_TOPIC);
            _consumer = _session.createConsumer(destination);
            _consumer.setMessageListener(this);
            LOG.info("Gateway Command Consumer Started");
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
    }

    public void onException(JMSException e) {
        LOG.info("Exception: " + e);
    }

    public void onMessage(Message message) {
        LOG.info("A meesage was received by the gateway: " + message);

        if (message instanceof MapMessage) {

            try {
                CommandMessage commandMessage =
                        new CommandMessage((MapMessage)message);
                HandlerResponse response = _service.sendSequenceCommand(
                        commandMessage.getSequenceCommand(),
                        commandMessage.getActivity(),
                        commandMessage.getConfiguration(),
                        commandMessage.getCompletionListener()
                );

                //send response back to the client
                sendResponse(commandMessage.getReplyDestination(), response);
            } catch (FormatException e) {
                LOG.log(Level.WARNING, "Message did not contain a command message: " + e.getMessage());
            } catch (JMSException e) {
                //this is produced when sending reply back to the client
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }


    private void sendResponse(Destination destination, HandlerResponse response) throws JMSException {

        MessageProducer producer = _session.createProducer(destination);
        MapMessage reply = _session.createMapMessage();
        //fill in the message
        reply.setString(GatewayKeys.HANDLER_RESPONSE_KEY, response.getResponse().name());

        if (response.getResponse() == HandlerResponse.Response.ERROR)  {
            if (response.getMessage() != null) {
                reply.setString(GatewayKeys.HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
            }
        }
        producer.send(reply);
    }

    public void close() {
        try {
            _consumer.close();
            _session.close();
            _connection.close();
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
    }

}
