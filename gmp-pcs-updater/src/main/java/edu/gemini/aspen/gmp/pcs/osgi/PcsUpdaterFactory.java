package edu.gemini.aspen.gmp.pcs.osgi;

import com.google.common.collect.Maps;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterComponent;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.jms.api.JmsArtifact;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

public class PcsUpdaterFactory implements ManagedServiceFactory {
    private static final Logger LOG = Logger.getLogger(PcsUpdaterFactory.class.getName());
    private static final String SIMULATION_PROPERTY = "simulation";
    private static final String EPICS_CHANNEL_PROPERTY = "epicsChannel";
    private static final String GAINS_PROPERTY = "gains";
    private static final String TAI_DIFF_PROPERTY = "taiDiff";

    private final Map<String, ServiceRef> existingServices = Maps.newHashMap();
    private final BundleContext context;
    private final ChannelAccessServer channelFactory;

    private class ServiceRef {
        private final ServiceRegistration<?> serviceRegistration;
        private final PcsUpdaterComponent updater;

        private ServiceRef(ServiceRegistration<?> serviceRegistration, PcsUpdaterComponent updater) {
            this.serviceRegistration = serviceRegistration;
            this.updater = updater;
        }
    }

    public PcsUpdaterFactory(BundleContext context, ChannelAccessServer channelFactory) {
        this.context = context;
        this.channelFactory = channelFactory;
    }

    public String getName() {
        return "PcsUpdater factory";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) {
        if (existingServices.containsKey(pid)) {
            existingServices.get(pid).updater.updatedComponent(properties);
        } else {
            if (checkProperties(properties)) {
                PcsUpdaterComponent updater = createService(properties);
                ServiceRegistration<?> registration = context.registerService(new String[]{PcsUpdater.class.getName(), JmsArtifact.class.getName()}, updater, new Hashtable<String, Object>());
                existingServices.put(pid, new ServiceRef(registration, updater));
                updater.startComponent();
            } else {
                LOG.warning("Cannot build " + PcsUpdater.class.getName() + " without the required properties");
            }
        }
    }

    private PcsUpdaterComponent createService(Dictionary<String, ?> properties) {
        try {
            boolean simulation = "true".equalsIgnoreCase(properties.get(SIMULATION_PROPERTY).toString());
            String epicsChannel = properties.get(EPICS_CHANNEL_PROPERTY).toString();
            String gains = properties.get(GAINS_PROPERTY).toString();
            int taiDiff = Integer.parseInt(properties.get(TAI_DIFF_PROPERTY).toString());
            return new PcsUpdaterComponent(channelFactory, simulation, epicsChannel, gains, taiDiff);
        } catch (NumberFormatException e) {
            LOG.severe("Cannot start ActiveMQBroker");
            throw e;
        }
    }

    private boolean checkProperties(Dictionary<String, ?> properties) {
        return properties.get(SIMULATION_PROPERTY) != null &&
            properties.get(EPICS_CHANNEL_PROPERTY) != null &&
            properties.get(GAINS_PROPERTY) != null &&
            properties.get(TAI_DIFF_PROPERTY) != null;
    }

    @Override
    public void deleted(String pid) {
        if (existingServices.containsKey(pid)) {
            ServiceRef reference = existingServices.get(pid);
            reference.serviceRegistration.unregister();
            reference.updater.stopComponent();
            existingServices.remove(pid);
        }
    }

    public void stopServices() {
        for (String pid: existingServices.keySet()) {
            deleted(pid);
        }
    }



}
