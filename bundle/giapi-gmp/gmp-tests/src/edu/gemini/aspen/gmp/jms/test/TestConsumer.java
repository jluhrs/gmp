package edu.gemini.aspen.gmp.jms.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import edu.gemini.aspen.gmp.broker.impl.ConfigDefaults;
import edu.gemini.aspen.gmp.broker.impl.GMPKeys;
import edu.gemini.aspen.gmp.commands.api.SequenceCommand;

import javax.jms.*;
import java.util.logging.Logger;

/**
 * Just a test consumer
 */
public class TestConsumer implements MessageListener, ExceptionListener {

    public static final Logger LOG = Logger.getLogger(TestConsumer.class.getName());

    public static void main(String[] args) {


        TestConsumer tc = new TestConsumer();
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ConfigDefaults.BROKER_URL);
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(GMPKeys.GMP_SEQUENCE_COMMAND_PREFIX + SequenceCommand.INIT.getName());

            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(tc);
//            session.close();
//            connection.stop();
//            connection.close();
        } catch (JMSException ex) {

        }


    }

    public void onException(JMSException e) {
        LOG.warning("Exception " + e);
    }

    public void onMessage(Message message) {
        LOG.info("Received message " + message);
    }
}
