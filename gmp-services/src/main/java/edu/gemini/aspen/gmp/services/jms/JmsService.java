package edu.gemini.aspen.gmp.services.jms;

import edu.gemini.aspen.gmp.services.core.Service;
import edu.gemini.aspen.gmp.services.core.ServiceException;
import edu.gemini.aspen.gmp.services.core.ServiceRequest;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Service that stores the JMS session internally, so
 * implementers can use it for advanced operations
 * as sending information back to the requester
 */
public abstract class JmsService implements Service, JmsArtifact {
    private static final Logger LOG = Logger.getLogger(JmsService.class.getName());

    protected Session _session;
    private Connection _connection;

    /**
     * The process method of a JmsService will only extract the JMS Message
     * from the request, and will pass it to the process method with the
     * JMS message argument.
     *
     * @param request A service request. If it's not a JmsServiceRequest,
     *                this method doesn't do anything.
     * @throws edu.gemini.aspen.gmp.services.core.ServiceException
     *
     */
    public void process(ServiceRequest request) throws ServiceException {
        if (request instanceof JmsServiceRequest) {
            JmsServiceRequest jmsRequest = (JmsServiceRequest) request;
            process(jmsRequest);
        }
    }

    /**
     * Process the given JmsServiceRequest
     *
     * @param request the JMS Service Request to be processed.
     * @throws ServiceException in case the service can not process the
     *                          request
     */
    public abstract void process(JmsServiceRequest request) throws ServiceException;

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        ConnectionFactory factory = provider.getConnectionFactory();
        _connection = factory.createConnection();
        _connection.setClientID("JMS Service Request Consumer");
        _connection.start();
        _session = _connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    public void stopJms() {
        try {
            if (_session != null)
                _session.close();
            if (_connection != null)
                _connection.close();
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception closing Service Request Consumer: ", e);
        }
    }
}
