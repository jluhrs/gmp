package edu.gemini.aspen.gmp.services.jms;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.aspen.gmp.services.core.*;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A JMS consumer of service requests.
 */
public class RequestConsumer implements MessageListener, ExceptionListener, JmsArtifact {

    private static final Logger LOG = Logger.getLogger(RequestConsumer.class.getName());

    private Connection _connection;
    private Session _session;
    private MessageConsumer _consumer;

    private final ServiceProcessor _serviceProcessor;

    public RequestConsumer(Service service) {
        _serviceProcessor = new ServiceProcessorImpl();
        _serviceProcessor.registerService(service);
    }

    public void close() {
        try {
            if (_consumer != null)
                _consumer.close();
            if (_session != null)
                _session.close();
            if (_connection != null)
                _connection.close();
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception closing Service Request Consumer: ", e);
        }
    }

    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on Services Request Consumer", e);
    }

    public void onMessage(Message message) {
        Preconditions.checkState(_session != null, "Session should have been initialized");
        try {
            if (message instanceof MapMessage) {
                MapMessage mm = (MapMessage) message;

                int requestType = mm.getIntProperty(JmsKeys.GMP_UTIL_REQUEST_TYPE);

                switch (requestType) {
                    case JmsKeys.GMP_UTIL_REQUEST_PROPERTY:
                        _serviceProcessor.process(ServiceType.PROPERTY_SERVICE, new JmsServiceRequest(mm));
                        break;

                    default:
                        LOG.warning("Invalid request received: " + requestType);
                }
            } else {
                LOG.warning("Unexpected message received by Services Request Consumer");
            }
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception parsing Service Request", e);
        } catch (ServiceException e) {
            LOG.log(Level.WARNING, "Exception processing Service Request", e);
        }
    }

    @Override
    public void startJms(JmsProvider provider) {
        ConnectionFactory factory = provider.getConnectionFactory();
        try {
            _connection = factory.createConnection();
            _connection.setClientID("Service Request Consumer");
            _connection.start();
            _connection.setExceptionListener(this);
            _session = _connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            //Completion info comes from a queue
            Destination destination = _session.createQueue(
                    JmsKeys.GMP_UTIL_REQUEST_DESTINATION);
            _consumer = _session.createConsumer(destination);
            _consumer.setMessageListener(this);

            LOG.info("Message Consumer started to receive service requests");
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Exception starting up Service Request Consumer", e);
        }
    }

    @Override
    public void stopJms() {
        close();
    }

    @Override
    public String toString() {
        return "RequestConsumer";
    }
}
