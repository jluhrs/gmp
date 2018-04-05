package edu.gemini.jms.activemq.provider;

import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.jms.api.JmsProviderStatusListener;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides methods to interact with the specific JMS Provider, in
 * this case ActiveMQ. Any usage of particular functionality provided by
 * the ActiveMQ package should be encapsulated here. Other classes just
 * rely on the plain JMS interfaces.
 */
public final class ActiveMQJmsProvider implements JmsProvider {
    private static final Logger LOG = Logger.getLogger(ActiveMQJmsProvider.class.getName());

    private ActiveMQConnectionFactory _factory;
    private final List<JmsProviderStatusListener> _statusListenerHandlers = new CopyOnWriteArrayList<JmsProviderStatusListener>();
    private final List<JmsArtifact> _jmsArtifacts = new CopyOnWriteArrayList<JmsArtifact>();

    private final String brokerUrl;

    private final AtomicReference<ActiveMQConnection> baseConnection = new AtomicReference<ActiveMQConnection>();
    private final AtomicBoolean connected = new AtomicBoolean(false);

    public ActiveMQJmsProvider(String url, int closeTimeout) {
        this.brokerUrl = substituteProperties(url);
        // Setup the connection factory
        LOG.info("ActiveMQ JMS Provider setup with url: " + brokerUrl + " and close timeout " + closeTimeout);
        _factory = new ActiveMQConnectionFactory(brokerUrl);
        _factory.setOptimizeAcknowledge(true);
        _factory.setAlwaysSessionAsync(false);
        _factory.setCloseTimeout(closeTimeout);
        _factory.setTransportListener(new JmsTransportListener());
    }

    public ActiveMQJmsProvider(String url) {
        this(url, 1000);
    }

    private String substituteProperties(String url) {
        String result = url;
        Properties properties = System.getProperties();

        // TODO: Use typesafe config for var replacement
        for (Map.Entry<Object, Object> e: properties.entrySet()) {
            String key = e.getKey().toString();
            String value = e.getKey().toString();
            String token = "${" + key + "}";
            if (url.contains(token)) {
                result = url.replace(token, value);
            }
        }
        return result;
    }

    public void startConnection() {
        // Start the connection in the background
        new Thread(() -> {
            try {
                ActiveMQConnection connection = (ActiveMQConnection) _factory.createConnection();
                connection.start();
                LOG.info("Base connection established to " + brokerUrl);
                Connection previousConnection = baseConnection.getAndSet(connection);
                if (previousConnection != null) {
                    previousConnection.close();
                }
            } catch (JMSException e) {
                LOG.log(Level.SEVERE, "Failure while creating the connection to " + brokerUrl, e);
            }
        }).start();
    }

    public void stopConnection() {
        Connection previousConnection = baseConnection.get();
        if (previousConnection != null) {
            try {
                previousConnection.close();
            } catch (JMSException e) {
                LOG.log(Level.SEVERE, "Failure while closing the connection to " + brokerUrl, e);
            }
        }
    }

    /**
     * Return a JMS Connection factory.
     *
     * @return the ConnectionFactory implemented by the JMS Provider
     */
    public ConnectionFactory getConnectionFactory() {
        return _factory;
    }

    public void bindJmsStatusListener(JmsProviderStatusListener providerStatusListenerListener) {
        _statusListenerHandlers.add(providerStatusListenerListener);
        LOG.info("JMS Status Listener Registered: " + providerStatusListenerListener);
    }

    public void unbindJmsStatusListener(JmsProviderStatusListener providerStatusListenerListener) {
        _statusListenerHandlers.remove(providerStatusListenerListener);
        LOG.info("Removed JMS Status Listener: " + providerStatusListenerListener);
    }

    public void bindJmsArtifact(JmsArtifact jmsArtifact) {
        synchronized (_jmsArtifacts) {
            _jmsArtifacts.add(jmsArtifact);
            if (connected.get()) {
                try {
                    jmsArtifact.startJms(this);
                } catch (Throwable e) {
                    LOG.severe("Exception starting JMSArtifact " + e);
                }
            }
            LOG.info("JMS Artifact Registered: " + jmsArtifact);
        }
    }

    public void unbindJmsArtifact(JmsArtifact jmsArtifact) {
        synchronized (_jmsArtifacts) {
            _jmsArtifacts.remove(jmsArtifact);
        }
        if (connected.get()) {
            jmsArtifact.stopJms();
        }
        LOG.info("JMS Artifact Removed: " + jmsArtifact);
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
            if (connected.compareAndSet(true, false)) {
                for (JmsProviderStatusListener l : _statusListenerHandlers) {
                    l.transportInterrupted();
                }
                synchronized (_jmsArtifacts) {
                    for (JmsArtifact a : _jmsArtifacts) {
                        try {
                            a.stopJms();
                        } catch (Exception e) {
                            LOG.log(Level.SEVERE, "Exception while shutting down jms artifact " + a, e);
                        }
                    }
                }
            }
        }

        @Override
        public void transportResumed() {
            LOG.fine("Connection resumed");
            // First time connection
            if (!connected.getAndSet(true)) {
                for (JmsProviderStatusListener l : _statusListenerHandlers) {
                    l.transportResumed();
                }
                synchronized (_jmsArtifacts) {
                    for (JmsArtifact a : _jmsArtifacts) {
                        LOG.fine("Starting JMS Artifact " + a);
                        try {
                            a.startJms(ActiveMQJmsProvider.this);
                        } catch (Throwable e) {
                            LOG.severe("Exception starting JMSArtifact " + e);
                        }
                    }
                }
            }
        }
    }
}
