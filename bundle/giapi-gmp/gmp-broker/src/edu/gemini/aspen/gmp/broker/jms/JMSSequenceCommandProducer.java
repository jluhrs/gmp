package edu.gemini.aspen.gmp.broker.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import edu.gemini.aspen.gmp.broker.impl.ConfigDefaults;
import edu.gemini.aspen.gmp.broker.impl.GMPKeys;
import edu.gemini.aspen.gmp.broker.commands.HandlerResponseImpl;
import edu.gemini.aspen.gmp.broker.commands.Action;
import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;

import java.util.logging.Logger;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;

/**
 * The JMSSequenceCommandProducer is in charge of generating JMS messages
 * that will contain a sequence command and dispatch them to the clients via
 * JMS
 */

public class JMSSequenceCommandProducer implements ExceptionListener {

    private final Logger LOG = Logger.getLogger(JMSSequenceCommandProducer.class.getName());

    private static final Map<SequenceCommand, String> TOPIC_MAP = new HashMap<SequenceCommand, String>();

    static {
        for (SequenceCommand sc: SequenceCommand.values()) {
            TOPIC_MAP.put(sc, GMPKeys.GMP_SEQUENCE_COMMAND_PREFIX + sc.getName());
        }
    }


    private MessageProducer _producer;
    private MessageConsumer _replyConsumer;

    private Session _session;
    private Connection _connection;
    private Queue _replyQueue;

    public JMSSequenceCommandProducer() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ConfigDefaults.BROKER_URL);
        try {
            _connection = connectionFactory.createConnection();
            _connection.setClientID("Sequence Command Producer");
            _connection.start();
            _connection.setExceptionListener(this);
            _session = _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            _producer = _session.createProducer(null);

            _replyQueue = _session.createTemporaryQueue();
            _replyConsumer = _session.createConsumer(_replyQueue);

            //this improves performance by avoiding to store the messages
            //_producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            LOG.info("Message Producer started");
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
    }

    public void shutdown() {
        try {
            _producer.close();
            _session.close();
            _connection.close();
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
    }


    /**
     * This method takes the given action and converts it into a JMS
     * message to be dispatched over the network
     * @param action The action to be sent via JMS
     * @return HandlerResponse associated to the given action
     */
    public HandlerResponse dispatchAction(Action action) {

        //first, build a message
        try {
            MapMessage m = _session.createMapMessage();

            //activity is a property
            m.setStringProperty(GMPKeys.GMP_ACTIVITY_PROP, action.getActivity().getName());

            //action id is stored as a property as well
            m.setIntProperty(GMPKeys.GMP_ACTIONID_PROP, action.getId());
            if (action.getConfiguration() != null) {
                //set all the configuration elements in the map
                Enumeration<String> e = action.getConfiguration().getKeys();
                while (e.hasMoreElements())  {
                    String key = e.nextElement();
                    String value = action.getConfiguration().getValue(key);
                    m.setString(key, value);
                }
            }
            //set the address to reply to this message
            m.setJMSReplyTo(_replyQueue);
            //send the message
            Destination topic = _session.createTopic(TOPIC_MAP.get(action.getSequenceCommand()));
            LOG.info("Pushing sequence command " + action.getSequenceCommand().getName() + "; Activity = " + action.getActivity().getName());
            _producer.send(topic, m);

            //now,  We'll receive the answer synchronously
            Message reply = _replyConsumer.receive(1000); //one sec.
            if (reply instanceof MapMessage) {
                MapMessage replyMap  =(MapMessage)reply;
                return JMSUtil.buildHandlerResponse(replyMap);
            } else {
                LOG.warning("No answer received to sequence command " + action.getSequenceCommand().getName());
            }

        } catch (JMSException e) {
            LOG.warning("Exception while sending sequence command " + e);
        }
        return HandlerResponseImpl.createError("No answer received to sequence command " + action.getSequenceCommand().getName());
    }


    public void onException(JMSException e) {
        LOG.warning("onException: " + e);
        e.printStackTrace();

    }


}
