package edu.gemini.jms.activemq.provider;

import edu.gemini.jms.api.JmsProvider;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.ConnectionFactory;
import java.util.logging.Logger;

/**
 * This class provides methods to interact with the specific JMS Provider, in
 * this case ActiveMQ. Any usage of particular functionality provided by
 * the ActiveMQ package should be encapsulated here. Other classes just
 * rely on the plain JMS interfaces. 
 */
@Component(managedservice = "edu.gemini.jms.activemq.provider.ActiveMQJmsProvider")
@Instantiate
@Provides
public final class ActiveMQJmsProvider implements JmsProvider {
    private static final Logger LOG = Logger.getLogger(ActiveMQJmsProvider.class.getName());

    private ConnectionFactory _factory;
    private static final String DEFAULT_BROKER_URL = "failover:(tcp://localhost:61616)";

    @Property(name = "brokerUrl", value=DEFAULT_BROKER_URL, mandatory = true)
    private String brokerUrl;

    ActiveMQJmsProvider() {
    }

    public ActiveMQJmsProvider(String url) {
       this.brokerUrl = url;
       validated();
    }

    @Validate
    public void validated() {
        // Setup the connection factory
        LOG.info("ActiveMQ JMS Provider setup with url: " + brokerUrl);
        _factory = new ActiveMQConnectionFactory(brokerUrl);
    }

    /**
     * Return a JMS Connection factory.
     * @return the ConnectionFactory implemented by the JMS Provider
     */
    public ConnectionFactory getConnectionFactory() {
        return _factory;
    }

}
