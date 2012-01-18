package edu.gemini.jms.activemq.provider;

import edu.gemini.jms.api.JmsProvider;
import edu.gemini.jms.api.JmsProviderStatusListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.ConnectionFactory;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * This class provides methods to interact with the specific JMS Provider, in
 * this case ActiveMQ. Any usage of particular functionality provided by
 * the ActiveMQ package should be encapsulated here. Other classes just
 * rely on the plain JMS interfaces.
 */
@Component
@Provides
public final class ActiveMQJmsProvider implements JmsProvider {
    private static final Logger LOG = Logger.getLogger(ActiveMQJmsProvider.class.getName());

    private ActiveMQConnectionFactory _factory;
    private static final String DEFAULT_BROKER_URL = "failover:(tcp://localhost:61616)";
    private final List<JmsProviderStatusListener> _statusListenerHandlers = new CopyOnWriteArrayList<JmsProviderStatusListener>();

    private final String brokerUrl;
    private final TransportListener transportListener = new JmsTransportListener();

    public ActiveMQJmsProvider(@Property(name = "brokerUrl", value = DEFAULT_BROKER_URL, mandatory = true) String url) {
        this.brokerUrl = url;
        // Setup the connection factory
        LOG.info("ActiveMQ JMS Provider setup with url: " + brokerUrl);
        _factory = new ActiveMQConnectionFactory(brokerUrl);
        _factory.setTransportListener(transportListener);
    }

    @Validate
    public void startConnection() {
        // Required for iPojo
    }

    /**
     * Return a JMS Connection factory.
     *
     * @return the ConnectionFactory implemented by the JMS Provider
     */
    public ConnectionFactory getConnectionFactory() {
        return _factory;
    }

    @Bind(aggregate = true, optional = true)
    public void bindJmsStatusListener(JmsProviderStatusListener providerStatusListenerListener) {
        _statusListenerHandlers.add(providerStatusListenerListener);
        LOG.info("JMS Status Listener Registered: " + providerStatusListenerListener);
    }

    @Unbind
    public void unbindJmsStatusListener(JmsProviderStatusListener providerStatusListenerListener) {
        _statusListenerHandlers.remove(providerStatusListenerListener);
        LOG.info("Removed JMS Status Listener: " + providerStatusListenerListener);
    }

    class JmsTransportListener implements TransportListener {
        @Override
        public void onCommand(Object o) {
            // Ignore
        }

        @Override
        public void onException(IOException e) {
            // Ignore
        }

        @Override
        public void transportInterupted() {
            for (JmsProviderStatusListener l: _statusListenerHandlers) {
                l.transportInterrupted();
            }
        }

        @Override
        public void transportResumed() {
            for (JmsProviderStatusListener l: _statusListenerHandlers) {
                l.transportResumed();
            }
        }
    }

}
