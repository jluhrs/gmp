package edu.gemini.aspen.gmp.broker.jms;

import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.broker.impl.GMPKeys;
import edu.gemini.aspen.gmp.broker.commands.HandlerResponseImpl;
import edu.gemini.aspen.gmp.broker.commands.Action;
import edu.gemini.aspen.gmp.commands.api.ConfigPath;

import javax.jms.*;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * The JMSSequenceCommandProducer is in charge of generating JMS messages that
 * will contain an Action and dispatch them to the clients via JMS
 *
 * This class is a singleton, using the enum singleton pattern.
 */
public enum JMSActionMessageProducer implements ExceptionListener {

    /**
     * The singleton
     */
    INSTANCE;

    private final Logger LOG = Logger.getLogger(JMSActionMessageProducer.class.getName());

    /**
     * Map to store topics associtated to each sequence command
     */
    private static final Map<SequenceCommand, String> TOPIC_MAP = new HashMap<SequenceCommand, String>();
    /**
     * Topic map static initialization
     */
    static {
        for (SequenceCommand sc : SequenceCommand.values()) {
            TOPIC_MAP.put(sc,
                    GMPKeys.GMP_SEQUENCE_COMMAND_PREFIX + sc.getName());
        }
    }

    private Connection _connection;
    private Session _session;
    private MessageProducer _producer;
    private MessageConsumer _replyConsumer;
    private Queue _replyQueue;

    /**
     * Private constructor, initialize the connection, session, consumers and producers
     * to be used when sending Action Messages 
     */
    private JMSActionMessageProducer() {
        ConnectionFactory connectionFactory = JMSProvider.getConnectionFactory();
        try {
            _connection = connectionFactory.createConnection();
            _connection.setClientID("Sequence Command Producer");
            _connection.start();
            _connection.setExceptionListener(this);
            _session = _connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            _producer = _session.createProducer(null);
            //this improves performance by avoiding to store the messages
            //_producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            _replyQueue = _session.createTemporaryQueue();
            _replyConsumer = _session.createConsumer(_replyQueue);
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
        LOG.info("Action Message Producer started");
    }


    /**
     * Creates a Map Message to be used by this producer
     * @return a new Map Message created by this Session
     * @throws JMSException if there is an error creating the Map Message
     */
    public MapMessage createMapMessage() throws JMSException {
        MapMessage m = _session.createMapMessage();
        m.setJMSReplyTo(_replyQueue);
        return m;
    }


    /**
     * Create a destination for the given Action.
     * @param action The destination is specific to the given action
     * @return a new destination for the given action.
     * @throws JMSException if it is not possible to create
     *         a destination for the specified action.
     */
    public Destination createDestination(Action action) throws JMSException {
        return createDestination(action, null);
    }

    public Destination createDestination(Action action, ConfigPath path) throws JMSException {
        StringBuilder sb = new StringBuilder(TOPIC_MAP.get(action.getSequenceCommand()));
        if (path != null) {
            sb.append(GMPKeys.GMP_SEPARATOR);
            sb.append(path.toString());
        }
        return _session.createTopic(sb.toString());
    }


    /**
     * Send the given message to the specified destination.
     * @param destination Destination where the message should be sent
     * @param message Message to be sent
     * @return A HandlerResponse containing the ack info
     * @throws JMSException if there was a problem while sending the message
     */
    public HandlerResponse send(Destination destination, Message message) throws JMSException {
        _producer.send(destination, message);
        Message reply = _replyConsumer.receive(500); //500 msec to answer.
        if (reply instanceof MapMessage) {
            MapMessage replyMap = (MapMessage) reply;
            return JMSUtil.buildHandlerResponse(replyMap);
        } else {
            LOG.warning("No answer received for action ");
        }
        return HandlerResponseImpl.create(HandlerResponse.Response.NOANSWER);
    }

    /**
     * Close all the resources allocated by this producer
     */
    public void close() {
        try {
            _replyConsumer.close();
            _producer.close();
            _session.close();
            _connection.close();
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
    }

    public void onException(JMSException e) {
        LOG.warning("onException: " + e);
        e.printStackTrace();

    }


}
