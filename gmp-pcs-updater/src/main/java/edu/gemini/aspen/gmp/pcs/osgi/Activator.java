package edu.gemini.aspen.gmp.pcs.osgi;

import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterComponent;
import edu.gemini.cas.ChannelAccessServer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Hashtable;

public class Activator implements BundleActivator {
    private ServiceRegistration<ManagedServiceFactory> factoryService;
    private ServiceTracker<ChannelAccessServer, ChannelAccessServer> serviceTracker;

    @Override
    public void start(final BundleContext context) throws Exception {
        serviceTracker = new ServiceTracker<ChannelAccessServer, ChannelAccessServer>(context, ChannelAccessServer.class, new ServiceTrackerCustomizer<ChannelAccessServer, ChannelAccessServer>() {

            private PcsUpdaterFactory providerFactory;

            @Override
            public ChannelAccessServer addingService(ServiceReference<ChannelAccessServer> reference) {
                ChannelAccessServer channelAccessServer = context.getService(reference);

                Hashtable<String, String> props = new Hashtable<String, String>();
                props.put("service.pid", PcsUpdaterComponent.class.getName());

                providerFactory = new PcsUpdaterFactory(context, channelAccessServer);

                factoryService = context.registerService(ManagedServiceFactory.class, providerFactory, props);

                return channelAccessServer;
            }

            @Override
            public void modifiedService(ServiceReference<ChannelAccessServer> channelAccessServerServiceReference, ChannelAccessServer channelAccessServer) {

            }

            @Override
            public void removedService(ServiceReference<ChannelAccessServer> channelAccessServerServiceReference, ChannelAccessServer channelAccessServer) {
                if (factoryService != null) {
                    factoryService.unregister();
                    factoryService = null;
                }
                if (providerFactory != null) {
                    providerFactory.stopServices();
                    providerFactory = null;
                }
            }
        });
        serviceTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (factoryService != null) {
            factoryService.unregister();
            factoryService = null;
        }
        if (serviceTracker != null) {
            serviceTracker.close();
            serviceTracker = null;
        }
    }
}
