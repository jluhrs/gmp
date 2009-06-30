package edu.gemini.jms.activemq.broker;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;

import edu.gemini.jms.api.JmsProvider;

/**
 * This class provides methods to interact with the specific JMS Provider, in
 * this case ActiveMQ. Any usage of particular functionality provided by
 * the ActiveMQ package should be encapsulated here. Other classes just
 * rely on the plain JMS interfaces. 
 */
public final class ActiveMQJmsProvider implements JmsProvider {

    private ConnectionFactory _factory;

    public ActiveMQJmsProvider() {
        _factory = new ActiveMQConnectionFactory(ConfigDefaults.BROKER_URL);
    }


    public ActiveMQJmsProvider(String url) {
        _factory = new ActiveMQConnectionFactory(url);
    }

    /**
     * Return a JMS Connection factory.
     * @return the ConnectionFactory implemented by the JMS Provider
     */
    public ConnectionFactory getConnectionFactory() {
        return _factory;
    }

}
