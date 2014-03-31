package edu.gemini.jms.activemq.provider;

import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.jms.api.JmsProviderStatusListener;
import net.jmatrix.eproperties.EProperties;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.TransportListener;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.io.IOException;
import java.util.List;
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
@Component
@Provides
public final class ActiveMQJmsProvider implements JmsProvider {
    private static final Logger LOG = Logger.getLogger(ActiveMQJmsProvider.class.getName());

    private ActiveMQConnectionFactory _factory;
    private static final String DEFAULT_BROKER_URL = "failover:(tcp://localhost:61616)";
    private static final String BROKER_URL_PROPERTY = "brokerUrl";
    private static final String CLOSE_TIMEOUT_PROPERTY = "closeTimeout";
    private final List<JmsProviderStatusListener> _statusListenerHandlers = new CopyOnWriteArrayList<JmsProviderStatusListener>();
    private final List<JmsArtifact> _jmsArtifacts = new CopyOnWriteArrayList<JmsArtifact>();

    private final String brokerUrl;
    private final TransportListener transportListener = new JmsTransportListener();

    private final AtomicReference<ActiveMQConnection> baseConnection = new AtomicReference<ActiveMQConnection>();
    private final AtomicBoolean connected = new AtomicBoolean(false);

    public ActiveMQJmsProvider(@Property(name = BROKER_URL_PROPERTY, value = DEFAULT_BROKER_URL, mandatory = true) String url,
            @Property(name = CLOSE_TIMEOUT_PROPERTY, value = "1000", mandatory = false) String closeTimeout) {
        this.brokerUrl = substituteProperties(url);
        // Setup the connection factory
        LOG.info("ActiveMQ JMS Provider setup with url: " + brokerUrl + " and close timeout " + closeTimeout);
        _factory = new ActiveMQConnectionFactory(brokerUrl);
        _factory.setCloseTimeout(Integer.parseInt(closeTimeout));
        _factory.setTransportListener(transportListener);
    }

    public ActiveMQJmsProvider(String url) {
        this(url, "1000");
    }

    private String substituteProperties(String url) {
        EProperties props = new EProperties();
        props.addAll(System.getProperties());
        props.put(BROKER_URL_PROPERTY, url);
        return props.get(BROKER_URL_PROPERTY, DEFAULT_BROKER_URL).toString();
    }

    @Validate
    public void startConnection() {
        // Start the connection in the background
        new Thread(new Runnable() {

            @Override
            public void run() {
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
            }
        }).start();
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

    @Bind(aggregate = true, optional = true)
    public void bindJmsArtifact(JmsArtifact jmsArtifact) {
        synchronized (_jmsArtifacts) {
            _jmsArtifacts.add(jmsArtifact);
        }
        if (connected.get()) {
            try {
                jmsArtifact.startJms(this);
            } catch (Throwable e) {
                LOG.severe("Exception starting JMSArtifact " + e);
            }
        }
        LOG.info("JMS Artifact Registered: " + jmsArtifact);
    }

    @Unbind(aggregate = true)
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
                for (JmsArtifact a : _jmsArtifacts) {
                    try {
                        a.stopJms();
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Exception while shutting down jms artifact " + a, e);
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
                for (JmsArtifact a : _jmsArtifacts) {
                    LOG.fine("Starting JMS Artifact" + a);
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
