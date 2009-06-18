package edu.gemini.aspen.gmp.jms.test;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import edu.gemini.jms.activemq.broker.ConfigDefaults;

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
            Destination destination = session.createTopic("test");
            MessageProducer producer = session.createProducer(destination);

            MapMessage m = session.createMapMessage();

//            m.setStringProperty(GatewayKeys.SEQUENCE_COMMAND_KEY, SequenceCommand.INIT.name());
//            m.setStringProperty(GatewayKeys.ACTIVITY_KEY, Activity.PRESET_START.name());

            Queue queue = session.createTemporaryQueue();
            m.setJMSReplyTo(queue);

            MessageConsumer consumer = session.createConsumer(queue);

            System.out.println("Sending message "+ m);

            producer.send(destination, m);

            MapMessage reply = (MapMessage)consumer.receive();

//            HandlerResponse response = JMSUtil.buildHandlerResponse(reply);
//            System.out.println("Reply: " + response);

            producer.close();
            consumer.close();
            session.close();
            connection.stop();
            connection.close();




        } catch (JMSException ex) {

        }


    }

}
