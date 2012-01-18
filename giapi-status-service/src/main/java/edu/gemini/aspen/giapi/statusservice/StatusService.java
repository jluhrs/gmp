package edu.gemini.aspen.giapi.statusservice;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.statusservice.jms.JmsStatusListener;
import edu.gemini.aspen.giapi.statusservice.jms.StatusConsumer;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class for the status service. This class is responsible to interface between the OSGi
 * container and the actual implementation residing in the StatusService
 */
@Component
public class StatusService {
    private static final Logger LOG = Logger.getLogger(StatusService.class.getName());
    private static final String DEFAULT_STATUS = ">"; //defaults to listen for all the status items.
    private static final String DEFAULT_NAME = "Status Service";

    private StatusHandlerAggregate _aggregate;

    private final JmsProvider _provider;

    private final String statusName;
    private final String serviceName;

    private StatusConsumer _consumer;

    public StatusService(@Requires StatusHandlerAggregate aggregate,
                         @Requires JmsProvider provider,
                         @Property(name = "serviceName", value = DEFAULT_NAME, mandatory = true) String serviceName,
                         @Property(name = "statusName", value = DEFAULT_STATUS, mandatory = true) String statusName) {
        Preconditions.checkArgument(aggregate != null);
        Preconditions.checkArgument(provider != null);
        Preconditions.checkArgument(serviceName != null);
        Preconditions.checkArgument(statusName != null);

        this.serviceName = serviceName;
        this.statusName = statusName;
        this._aggregate = aggregate;
        this._provider = provider;
    }

    @Validate
    public void initialize() {
        LOG.info("StatusService initialized");
        buildConsumer();
    }

    private void buildConsumer() {
        // Start consumer in a separate thread to avoid blocking if the connection is not working
        new Thread(new Runnable() {
            @Override
            public void run() {
                _consumer = new StatusConsumer(serviceName, statusName);
                _consumer.setMessageListener(new JmsStatusListener(_aggregate));
                LOG.info("StatusService has built consumer for serviceName=" + serviceName + ", statusName=" + statusName);
                try {
                    _consumer.startJms(_provider);
                } catch (JMSException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }).start();

    }

    @Invalidate
    public void stopComponent() {
        // TODO Cancel the thread in build consumer if still runnig
        if (_consumer != null) {
            _consumer.stopJms();
        } else {
            LOG.severe("Attempt to stop the the consumer before is ready");
        }
    }
}
