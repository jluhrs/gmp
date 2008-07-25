package edu.gemini.aspen.gmp.jms.test;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import edu.gemini.aspen.gmp.broker.impl.ConfigDefaults;
import edu.gemini.aspen.gmp.broker.impl.GMPKeys;
import edu.gemini.aspen.gmp.commands.api.SequenceCommand;

/**
 * Just a test producer
 */
public class TestProducer {


    public static void main(String[] args) {


        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ConfigDefaults.BROKER_URL);
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(GMPKeys.GMP_SEQUENCE_COMMAND_PREFIX + SequenceCommand.INIT.getName());
            MessageProducer producer = session.createProducer(destination);

            MapMessage m = session.createMapMessage();

            m.setIntProperty(GMPKeys.GMP_ACTIONID_PROP, 12345);

            System.out.println("Sending message "+ m);

            producer.send(destination, m);


            producer.close();
            session.close();
            connection.stop();
            connection.close();




        } catch (JMSException ex) {

        }


    }

}
