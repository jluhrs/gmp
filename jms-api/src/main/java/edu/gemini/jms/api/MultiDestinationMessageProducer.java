package edu.gemini.jms.api;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class MultiDestinationMessageProducer
 *
 * @author Nicolas A. Barriga
 *         Date: 7/6/12
 */
public class MultiDestinationMessageProducer implements JmsArtifact, ExceptionListener {
    protected static final Logger LOG = Logger.getLogger(MultiDestinationMessageProducer.class.getName());
    private Connection _connection;

    protected Session _session;
    private String _clientName;
    private DestinationBuilder _destinationBuilder;
    private Map<DestinationData, MessageProducer> _producers = new HashMap<DestinationData, MessageProducer>();

    private volatile boolean _isConnected;

    public MultiDestinationMessageProducer(String clientName) {
        _clientName = clientName;
        _destinationBuilder = new DestinationBuilder();
        _isConnected = false;
    }

    /**
     * Start the connection to the given JMS Provider.
     *
     * @param provider the JMS Provider to connect to
     * @throws JMSException in case there is a problem initializing
     *                      the JMS artifact
     */
    @Override
    public void startJms(JmsProvider provider) throws JMSException {

        ConnectionFactory factory = provider.getConnectionFactory();

        _connection = factory.createConnection();
        _connection.setClientID(_clientName + UUID.randomUUID().toString());
        _connection.start();
        _connection.setExceptionListener(this);
        _session = _connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        _isConnected = true;
        LOG.info("Started JMS Artifact [" + _clientName + "]");
    }

    private void destroyJmsObjects() {
        for (MessageProducer p : _producers.values()) {
            try {
                p.close();
            } catch (JMSException e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    /**
     * Stop this JMS artifact.
     */
    @Override
    public void stopJms() {
        _isConnected = false;
        try {
            destroyJmsObjects();
            _producers.clear();
            if (_session != null)
                _session.close();
            if (_connection != null)
                _connection.close();
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception while stopping JMS Artifact", e);
        }
        LOG.info("Stopped JMS Artifact [" + _clientName + "]");
    }

    @Override
    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on JMS Artifact", e);
    }

    public boolean isConnected() {
        return _isConnected;
    }

    public void send(Message m, DestinationData d) throws JMSException {
        MessageProducer p = _producers.get(d);
        if (p == null) {
            synchronized (this) {
                Destination destination = _destinationBuilder.newDestination(
                        d, _session
                );

                p = _session.createProducer(destination);
                _producers.put(d, p);
            }
        }
        synchronized (p) {
            p.send(m);
        }
    }
}
