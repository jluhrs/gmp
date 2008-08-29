package edu.gemini.aspen.gmp.broker.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;

import edu.gemini.aspen.gmp.broker.impl.ConfigDefaults;

/**
 * This class provides methods to interact with the specific JMS Provider, in
 * this case ActiveMQ. Any usage of particular functionality provided by
 * the ActiveMQ package should be encapsulated here. Other classes just
 * rely on the plain JMS interfaces. 
 */
public class JMSProvider {

    private static ConnectionFactory _factory =
            new ActiveMQConnectionFactory(ConfigDefaults.BROKER_URL);

    /**
     * Return a JMS Connection factory.
     * @return the ConnectionFactory implemented by the JMS Provider
     */
    public static ConnectionFactory getConnectionFactory() {
        return _factory;
    }

}
