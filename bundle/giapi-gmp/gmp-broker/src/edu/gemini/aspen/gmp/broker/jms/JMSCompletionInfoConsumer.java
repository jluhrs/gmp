package edu.gemini.aspen.gmp.broker.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import edu.gemini.aspen.gmp.broker.impl.ConfigDefaults;
import edu.gemini.aspen.gmp.broker.impl.GMPKeys;
import edu.gemini.aspen.gmp.broker.api.GMPService;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;

import java.util.logging.Logger;

/**
 * This is a consumer of Completion Info messages.
 * <p/>
 * Whenever an action in the client code is finished, it has to report back to
 * the GMP with their completion information. Completion information can be
 * either COMPLETED or ERROR.
 */
public class JMSCompletionInfoConsumer implements MessageListener, ExceptionListener {

    private static final Logger LOG = Logger.getLogger(
            JMSCompletionInfoConsumer.class.getName());

    private GMPService _service;

    public JMSCompletionInfoConsumer(GMPService service) {
        _service = service;
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                ConfigDefaults.BROKER_URL);
        try {
            Connection connection = connectionFactory.createConnection();
            connection.setClientID("JMS Completion Info Consumer");
            connection.start();
            connection.setExceptionListener(this);
            Session session = connection.createSession(false,
                                                       Session.AUTO_ACKNOWLEDGE);
            //Completion info comes from a queue
            Destination destination = session.createQueue(
                    GMPKeys.GMP_COMPLETION_INFO);
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(this);
            LOG.info(
                    "Message Listener started to receive completion information");
        } catch (JMSException e) {
            LOG.warning("exception : " + e);
        }
    }

    public void onMessage(Message message) {
        LOG.info("Received Message");
        try {
            if (message instanceof MapMessage) {
                MapMessage m = (MapMessage) message;

                int actionId = m.getIntProperty(GMPKeys.GMP_ACTIONID_PROP);
                HandlerResponse response = JMSUtil.buildHandlerResponse(m);
                LOG.info(
                        "Received Completion info for action ID " + actionId + " : " + response
                                .toString());
                //Notify the OCS. Based on the action ID, we know who to notify
                _service.updateOcs(actionId, response);
            }
        } catch (JMSException e) {
            LOG.warning("onMessage exception : " + e);
        }
    }

    public void onException(JMSException e) {
        LOG.warning("onException: " + e);
        e.printStackTrace();
    }
}
