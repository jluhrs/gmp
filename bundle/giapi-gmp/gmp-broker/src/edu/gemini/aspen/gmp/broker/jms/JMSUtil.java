package edu.gemini.aspen.gmp.broker.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.broker.impl.GMPKeys;
import edu.gemini.aspen.gmp.broker.commands.HandlerResponseImpl;

/**
 */
public class JMSUtil {

    private boolean _isTransacted = false;
    private int _ackMode = Session.AUTO_ACKNOWLEDGE;
    

    public Connection createConnection(String brokerUrl) throws JMSException {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        Connection connection = connectionFactory.createConnection();
        //TODO: Set client ID?
        connection.start();
        return connection;

    }

    public Session createSession(Connection connection) throws JMSException {
        return connection.createSession(_isTransacted, _ackMode);
    }

    public Destination createDestination(Session session, boolean isTopic, String subject) throws JMSException {
        return isTopic ? session.createTopic(subject) : session.createQueue(subject);
    }

    public static HandlerResponse buildHandlerResponse(MapMessage msg) throws JMSException {

        String responseType = msg.getString(GMPKeys.GMP_HANDLER_RESPONSE_KEY);
        HandlerResponse.Response response; 
        try {
            response = HandlerResponse.Response.valueOf(responseType);
        } catch(IllegalArgumentException ex) {
            throw new JMSException("Invalid response type contained in the reply");
        }

        if (response != null) {
            if (response == HandlerResponse.Response.ERROR) {
                String errorMsg = msg.getString(GMPKeys.GMP_HANDLER_RESPONSE_ERROR_KEY);
                return HandlerResponseImpl.create(response, errorMsg);
            }
            return HandlerResponseImpl.create(response);
        }
        return null;
    }







}
