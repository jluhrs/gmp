package edu.gemini.giapi.tool.commands;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import edu.gemini.aspen.gmp.gw.jms.GatewayKeys;
import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.commands.api.Activity;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.broker.jms.JMSUtil;

/**
 *
 */
public class CommandSender {


       public CommandSender(String url) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(GatewayKeys.COMMAND_TOPIC);
            MessageProducer producer = session.createProducer(destination);

            MapMessage m = session.createMapMessage();

            m.setStringProperty(GatewayKeys.SEQUENCE_COMMAND_KEY, SequenceCommand.INIT.name());
            m.setStringProperty(GatewayKeys.ACTIVITY_KEY, Activity.PRESET_START.name());

            Queue queue = session.createTemporaryQueue();
            m.setJMSReplyTo(queue);

            MessageConsumer consumer = session.createConsumer(queue);

            System.out.println("Sending message "+ m);

            producer.send(destination, m);

            MapMessage reply = (MapMessage)consumer.receive();

            HandlerResponse response = JMSUtil.buildHandlerResponse(reply);
            System.out.println("Reply: " + response);

            producer.close();
            consumer.close();
            session.close();
            connection.stop();
            connection.close();




        } catch (JMSException ex) {
            ex.printStackTrace();
        }

       }

}
