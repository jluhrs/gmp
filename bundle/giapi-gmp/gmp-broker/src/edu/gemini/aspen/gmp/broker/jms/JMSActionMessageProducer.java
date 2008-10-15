package edu.gemini.aspen.gmp.broker.jms;

import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.broker.impl.GMPKeys;
import edu.gemini.aspen.gmp.broker.commands.HandlerResponseImpl;
import edu.gemini.aspen.gmp.broker.commands.Action;

import javax.jms.*;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * The JMSSequenceCommandProducer is in charge of generating JMS messages that
 * will contain an Action and dispatch them to the clients via JMS
 */
public class JMSActionMessageProducer implements ExceptionListener {

    private final static Logger LOG = Logger.getLogger(JMSActionMessageProducer.class.getName());
    private static final Map<SequenceCommand, String> TOPIC_MAP = new HashMap<SequenceCommand, String>();

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


    public JMSActionMessageProducer() {
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
        LOG.info("Message Producer started");

    }


    public ActionMessage createActionMessage(Action action) throws JMSException {

        MapMessage m = _session.createMapMessage();
        m.setJMSReplyTo(_replyQueue);

        //activity is a property
        m.setStringProperty(GMPKeys.GMP_ACTIVITY_PROP,
                            action.getActivity().getName());

        //action id is stored as a property as well
        m.setIntProperty(GMPKeys.GMP_ACTIONID_PROP, action.getId());


        return new ActionMessage(m);
    }

    public Destination createTopic(SequenceCommand sequenceCommand) throws JMSException {
        return _session.createTopic(TOPIC_MAP.get(sequenceCommand));
    }

    public HandlerResponse send(Destination topic, ActionMessage m) throws JMSException {
        _producer.send(topic, m.getJmsMessage());
        //now,  We'll receive the answer synchronously
        Message reply = _replyConsumer.receive(1000); //one sec.
        if (reply instanceof MapMessage) {
            MapMessage replyMap = (MapMessage) reply;
            return JMSUtil.buildHandlerResponse(replyMap);
        } else {
            LOG.warning("No answer received for action ");
        }
        return HandlerResponseImpl.createError("No answer received to action ");
    }

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
