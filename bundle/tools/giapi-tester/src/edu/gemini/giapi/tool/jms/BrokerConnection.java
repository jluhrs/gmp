package edu.gemini.giapi.tool.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.JMSException;

import edu.gemini.giapi.tool.TesterException;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Encapsulation of the JMS connection to the GMP
 */
public class BrokerConnection {

    private static final Logger LOG = Logger.getLogger(BrokerConnection.class.getName());
    private ConnectionFactory _factory;

    private Connection _connection;

    private Session _session;

    public BrokerConnection(String url) {
        _factory = new ActiveMQConnectionFactory(url);
    }

    public void start() throws TesterException {
        LOG.log(Level.FINE, "Started Broker Connection");
        try {
            _connection = _factory.createConnection();
            _connection.start();
            _session = _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException ex) {
            throw new TesterException(ex.getMessage());
        }

    }

    public void stop() throws TesterException {
        LOG.log(Level.FINE, "Stopping Broker Connection");
        try {
            if (_session != null) _session.close();
            if (_connection != null) _connection.close();
        } catch (JMSException ex) {
            throw new TesterException(ex.getMessage());
        }

    }

    public Session getSession() {
        return _session;
    }


}
