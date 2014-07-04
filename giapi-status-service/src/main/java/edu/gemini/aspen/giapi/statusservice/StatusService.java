package edu.gemini.aspen.giapi.statusservice;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.statusservice.jms.JmsStatusListener;
import edu.gemini.aspen.giapi.statusservice.jms.StatusConsumer;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class for the status service. This class is responsible to interface between the OSGi
 * container and the actual implementation residing in the StatusService
 */
public class StatusService implements JmsArtifact {
    private static final Logger LOG = Logger.getLogger(StatusService.class.getName());

    private StatusHandlerAggregate _aggregate;

    private final String statusName;
    private final String serviceName;

    private StatusConsumer _consumer;

    public StatusService(StatusHandlerAggregate aggregate,
                         String serviceName,
                         String statusName) {
        Preconditions.checkArgument(aggregate != null);
        Preconditions.checkArgument(serviceName != null);
        Preconditions.checkArgument(statusName != null);

        this.serviceName = serviceName;
        this.statusName = statusName;
        this._aggregate = aggregate;
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        _consumer = new StatusConsumer(serviceName, statusName);
        _consumer.setMessageListener(new JmsStatusListener(_aggregate));
        LOG.info("StatusService has built consumer for serviceName=" + serviceName + ", statusName=" + statusName);
        try {
            _consumer.startJms(provider);
        } catch (JMSException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }


    @Override
    public void stopJms() {
        if (_consumer != null) {
            _consumer.stopJms();
        } else {
            LOG.severe("Attempt to stop the the consumer before is ready");
        }
    }
}
