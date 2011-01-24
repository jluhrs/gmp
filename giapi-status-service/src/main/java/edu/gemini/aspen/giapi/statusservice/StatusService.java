package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.statusservice.jms.JmsStatusListener;
import edu.gemini.aspen.giapi.statusservice.jms.StatusConsumer;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class for the status service. This class is responsible to interface between the OSGi
 * container and the actual implementation residing in the StatusService
 */
@Component(name = "statusService", managedservice = "edu.gemini.aspen.giapi.statusservice.StatusService")
@Instantiate(name = "statusService")
public class StatusService implements JmsArtifact{
    private static final Logger LOG = Logger.getLogger(StatusService.class.getName());
    private static final String DEFAULT_STATUS = ">"; //defaults to listen for all the status items.
    private static final String DEFAULT_NAME = "Status Service";
//    private JmsProvider _provider;

    @Requires(id = "statusHandlerManager")
    private StatusHandlerAggregate _aggregate;

    @Requires
    private JmsProvider _provider;

    private StatusConsumer _consumer;

    @Property(name = "statusName", value = DEFAULT_STATUS, mandatory = true)
    private String statusName = DEFAULT_STATUS;

    @Property(name = "serviceName", value = DEFAULT_NAME)
    private String serviceName = DEFAULT_NAME;

    // Mark as private so is not exposed for API but iPOJO can see it anyway
    private StatusService() {
    }

    public StatusService(StatusHandlerAggregate aggregate, String serviceName, String statusName, JmsProvider provider) {
        this.serviceName = serviceName;
        this.statusName = statusName;
        this._aggregate = aggregate;
        this._provider = provider;
    }

    @Validate
    public void initialize() {
        LOG.info("StatusService validated, build consumer and begin listening...");
        buildConsumer(serviceName, statusName);
    }

    private void buildConsumer(String serviceName, String statusName) {
        _consumer = new StatusConsumer(serviceName, statusName);
        _consumer.setMessageListener(new JmsStatusListener(_aggregate));
        LOG.info("StatusService has built consumer for serviceName=" + serviceName + ", statusName=" + statusName);
        try{
            //if(!_consumer.isConnected()){
                _consumer.startJms(_provider);
           // }
        }catch(JMSException ex){
            LOG.log(Level.SEVERE,ex.getMessage(),ex);
        }
    }

    @Updated
    public void updated() {
        LOG.info("Updated service properties to serviceName=" + serviceName + ", statusName=" + statusName);
        // TODO: Decide how to restart the service if the configuration changes
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        _provider=provider;
        initialize();
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
