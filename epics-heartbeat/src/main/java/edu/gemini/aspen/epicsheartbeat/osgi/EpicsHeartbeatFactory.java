package edu.gemini.aspen.epicsheartbeat.osgi;

import com.google.common.collect.Maps;
import edu.gemini.aspen.epicsheartbeat.EpicsHeartbeat;
import edu.gemini.aspen.heartbeatdistributor.HeartbeatConsumer;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.gmp.top.Top;
import gov.aps.jca.CAException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

public class EpicsHeartbeatFactory implements ManagedServiceFactory {
    private static final Logger LOG = Logger.getLogger(EpicsHeartbeatFactory.class.getName());
    private static final String EPICS_CHANNEL_PROPERTY = "channelName";

    private final Map<String, ServiceRef> existingServices = Maps.newHashMap();
    private final BundleContext context;
    private final ChannelAccessServer channelFactory;
    private final Top top;

    private class ServiceRef {
        private final ServiceRegistration<?> serviceRegistration;
        private final EpicsHeartbeat heartbeat;

        private ServiceRef(ServiceRegistration<?> serviceRegistration, EpicsHeartbeat heartbeat) {
            this.serviceRegistration = serviceRegistration;
            this.heartbeat = heartbeat;
        }
    }

    public EpicsHeartbeatFactory(BundleContext context, ChannelAccessServer channelFactory, Top top) {
        this.context = context;
        this.channelFactory = channelFactory;
        this.top = top;
    }

    public String getName() {
        return "EpicsHeartbeat factory";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) {
        if (existingServices.containsKey(pid)) {
            try {
                existingServices.get(pid).heartbeat.update();
            } catch (CAException e) {
                LOG.severe("Error updating the Epics Heartbeat");
            }
        } else {
            if (checkProperties(properties)) {
                EpicsHeartbeat heartbeat = createService(properties);
                ServiceRegistration<?> registration = context.registerService(HeartbeatConsumer.class, heartbeat, new Hashtable<String, Object>());
                existingServices.put(pid, new ServiceRef(registration, heartbeat));
                try {
                    heartbeat.initialize();
                } catch (CAException e) {
                    LOG.severe("Error starting up the Epics Heartbeat");
                }
            } else {
                LOG.warning("Cannot build " + EpicsHeartbeat.class.getName() + " without the required properties");
            }
        }
    }

    private EpicsHeartbeat createService(Dictionary<String, ?> properties) {
        try {
            String epicsChannel = properties.get(EPICS_CHANNEL_PROPERTY).toString();
            return new EpicsHeartbeat(channelFactory, top, epicsChannel);
        } catch (NumberFormatException e) {
            LOG.severe("Cannot start ActiveMQBroker");
            throw e;
        }
    }

    private boolean checkProperties(Dictionary<String, ?> properties) {
        return properties.get(EPICS_CHANNEL_PROPERTY) != null;
    }

    @Override
    public void deleted(String pid) {
        if (existingServices.containsKey(pid)) {
            ServiceRef reference = existingServices.get(pid);
            reference.serviceRegistration.unregister();
            reference.heartbeat.shutdown();
            existingServices.remove(pid);
        }
    }

    public void stopServices() {
        for (String pid: existingServices.keySet()) {
            deleted(pid);
        }
    }



}
