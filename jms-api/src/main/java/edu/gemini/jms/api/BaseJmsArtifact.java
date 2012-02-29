package edu.gemini.jms.api;

import javax.jms.*;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class to create Message Producers and Consumers in JMS.
 * Every message consumer/producer has its own connection and session
 * to the server, so if you want/need to share connections and sessions among
 * several message consumers, you need manually to create your artifacts
 */

public abstract class BaseJmsArtifact implements JmsArtifact, ExceptionListener {

    protected static final Logger LOG = Logger.getLogger(BaseJmsArtifact.class.getName());
    private Connection _connection;

    protected Session _session;
    protected String _clientName;
    protected DestinationData _destinationData;
    protected DestinationBuilder _destinationBuilder;

    private boolean _isConnected;

    public BaseJmsArtifact(DestinationData data, String clientName) {
        _destinationData = data;
        _clientName = clientName;
        _destinationBuilder = new DestinationBuilder();
        _isConnected = false;
    }

    /**
     * Start the connection to the given JMS Provider.
     * @param provider the JMS Provider to connect to
     * @throws JMSException in case there is a problem initializing
     * the JMS artifact
     */
    public void startJms(JmsProvider provider) throws JMSException {

        ConnectionFactory factory = provider.getConnectionFactory();

        _connection = factory.createConnection();
        _connection.setClientID(_clientName+ UUID.randomUUID().toString());
        _connection.start();
        _connection.setExceptionListener(this);
        _session = _connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Destination destination = _destinationBuilder.newDestination(
                _destinationData, _session
        );

        constructJmsObject(destination);
        _isConnected = true;
        LOG.info("Started JMS Artifact [" + _clientName + "]");
    }

    /**
     * Used to create the actual consumer or producer.
     * @param d Destination to be used by the consumer or producer.
     * @throws JMSException in case there is a problem constructing the consumer or producer
     */
    protected abstract void constructJmsObject(Destination d) throws JMSException;

    /**
     * Destroy the consumer or producer
     * @throws JMSException in case there is a problem destroying the consumer or producer
     */
    protected abstract void destroyJmsObject() throws JMSException;


    /**
     * Stop this JMS artifact. 
     */
    public void stopJms() {
        _isConnected = false;
        try {
            destroyJmsObject();

            if (_session != null)
                _session.close();
            if (_connection != null)
                _connection.close();
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception while stopping JMS Artifact", e);
        }
        LOG.info("Stopped JMS Artifact [" + _clientName + "]");
    }

    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on JMS Artifact", e);
    }

    public boolean isConnected() {
        return _isConnected;
    }

}
