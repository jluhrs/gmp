package edu.gemini.aspen.gmp.broker.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.Enumeration;

import edu.gemini.aspen.gmp.broker.impl.ConfigDefaults;
import edu.gemini.aspen.gmp.broker.impl.GMPKeys;
import edu.gemini.aspen.gmp.commands.api.RequestHandler;


/**
 * A JMS Message listener. For testing at this point
 */
public class JMSMessageHandler implements MessageListener, ExceptionListener {

    private final static Logger LOG = Logger.getLogger(JMSMessageHandler.class.getName());

    private RequestHandler _handler;

    public JMSMessageHandler(String topic, RequestHandler handler) {

        _handler = handler;
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ConfigDefaults.BROKER_URL);
        try {
            Connection connection = connectionFactory.createConnection();
            connection.setClientID("JMSMessageHandler:" + topic);
            connection.start();
            connection.setExceptionListener(this);
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topic);
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(this);
            LOG.info("Message Listener started for topic : " + topic);
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
    }


    public void onMessage(Message message) {
        LOG.info("A message was received: " + message);
        
        try {
            LOG.info("Property: " +  message.getStringProperty(GMPKeys.GMP_ACTIVITY_PROP));

            if (message instanceof MapMessage) {
                MapMessage m = (MapMessage)message;

                Enumeration e = m.getMapNames();
                while (e.hasMoreElements()) {
                    String key = (String)e.nextElement();
                    LOG.info("Key = " +  key + " Value = " + m.getString(key));
                }

            }
            LOG.info("Unrecognized message received");
        } catch (JMSException e) {
            LOG.warning("onMessage exception : " + e);
        }

    }

    public void onException(JMSException e) {
        LOG.warning("onException: " + e);

    }
}
