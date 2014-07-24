package edu.gemini.aspen.gmp.epics.osgi;

import com.google.common.collect.Maps;
import edu.gemini.aspen.gmp.epics.EpicsConfiguration;
import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.impl.ChannelListConfiguration;
import edu.gemini.aspen.gmp.epics.impl.EpicsMonitor;
import edu.gemini.epics.api.EpicsClient;
import edu.gemini.jms.api.JmsArtifact;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import java.util.*;
import java.util.logging.Logger;

public class ChannelConfigurationFactory implements ManagedServiceFactory {
    private static final Logger LOG = Logger.getLogger(ChannelConfigurationFactory.class.getName());
    private static final String CONFIG_FILE_PROPERTY = "configurationFile";

    private final BundleContext context;
    private final EpicsRegistrar epicsRegistrar;
    private final Map<String, ServiceRegistration<EpicsConfiguration>> existingServices = Maps.newHashMap();
    private final Map<String, ServiceRef> existingMonitors = Maps.newHashMap();

    private final class ServiceRef {
        final EpicsMonitor epicsMonitor;
        final ServiceRegistration<?> registration;

        private ServiceRef(EpicsMonitor epicsMonitor, ServiceRegistration<?> registration) {
            this.epicsMonitor = epicsMonitor;
            this.registration = registration;
        }
    }

    public ChannelConfigurationFactory(BundleContext context, EpicsRegistrar epicsRegistrar) {
        this.context = context;
        this.epicsRegistrar = epicsRegistrar;
    }

    public String getName() {
        return "GMP Command Records factory";
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> properties) {
        if (existingServices.containsKey(pid)) {
            existingServices.remove(pid);
            existingMonitors.remove(pid);
            updated(pid, properties);
        } else {
            if (checkProperties(properties)) {
                ChannelListConfiguration configuration = createService(properties);
                ServiceRegistration<EpicsConfiguration> serviceRegistration = context.registerService(EpicsConfiguration.class, configuration, new Hashtable<String, Object>());
                existingServices.put(pid, serviceRegistration);

                Set<String> channelsNames = configuration.getValidChannelsNames();
                String[] props = channelsNames.toArray(new String[]{});
                LOG.info("Services properties set as: " + Arrays.asList(props));

                EpicsMonitor epicsMonitor = new EpicsMonitor(epicsRegistrar, configuration, props);
                Hashtable<String, Object> monitorProperties = new Hashtable<String, Object>();
                monitorProperties.put("edu.gemini.epics.api.EpicsClient.EPICS_CHANNELS", props);
                ServiceRegistration<?> registration = context.registerService(new String[] {EpicsClient.class.getName(), JmsArtifact.class.getName()}, epicsMonitor, monitorProperties);
                existingMonitors.put(pid, new ServiceRef(epicsMonitor, registration));
            } else {
                LOG.warning("Cannot build " + ChannelListConfiguration.class.getName() + " without the required properties");
            }
        }
    }

    private ChannelListConfiguration createService(Dictionary<String, ?> properties) {
        String configFile = properties.get(CONFIG_FILE_PROPERTY).toString();
        return new ChannelListConfiguration(configFile);
    }

    private boolean checkProperties(Dictionary<String, ?> properties) {
        return properties.get(CONFIG_FILE_PROPERTY) != null;
    }

    @Override
    public void deleted(String pid) {
        if (existingServices.containsKey(pid)) {
            existingServices.get(pid).unregister();
            existingServices.remove(pid);
        }
        if (existingMonitors.containsKey(pid)) {
            existingMonitors.get(pid).registration.unregister();
            existingMonitors.get(pid).epicsMonitor.stopChannels();
            existingMonitors.remove(pid);
        }
    }

    public void stopServices() {
        for (String pid: existingServices.keySet()) {
            deleted(pid);
        }
    }

}
