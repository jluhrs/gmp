package edu.gemini.aspen.epicsheartbeat.osgi;

import edu.gemini.aspen.epicsheartbeat.EpicsHeartbeat;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.gmp.top.Top;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Hashtable;

public class Activator implements BundleActivator {
    private ServiceTracker<ChannelAccessServer, ChannelAccessServer> caServiceTracker;
    private ServiceTracker<Top, Top> topServiceTracker;
    private ServiceRegistration<ManagedServiceFactory> factoryService;

    @Override
    public void start(final BundleContext context) throws Exception {
        caServiceTracker = new ServiceTracker<ChannelAccessServer, ChannelAccessServer>(context, ChannelAccessServer.class, new ServiceTrackerCustomizer<ChannelAccessServer, ChannelAccessServer>() {

            @Override
            public ChannelAccessServer addingService(ServiceReference<ChannelAccessServer> reference) {
                final ChannelAccessServer cas = context.getService(reference);
                topServiceTracker = new ServiceTracker<Top, Top>(context, Top.class, new ServiceTrackerCustomizer<Top, Top>() {

                    private EpicsHeartbeatFactory factory;

                    @Override
                    public Top addingService(ServiceReference<Top> topReference) {
                        Top top = context.getService(topReference);

                        factory = new EpicsHeartbeatFactory(context, cas, top);

                        Hashtable<String, String> props = new Hashtable<String, String>();
                        props.put("service.pid", EpicsHeartbeat.class.getName());

                        factoryService = context.registerService(ManagedServiceFactory.class, factory, props);

                        return top;
                    }

                    @Override
                    public void modifiedService(ServiceReference<Top> reference, Top top) {

                    }

                    @Override
                    public void removedService(ServiceReference<Top> reference, Top top) {
                        if (factory != null) {
                            factory.stopServices();
                            factory = null;
                        }
                        if (factoryService != null) {
                            factoryService.unregister();
                            factoryService = null;
                        }
                    }
                });
                topServiceTracker.open();
                return context.getService(reference);
            }

            @Override
            public void modifiedService(ServiceReference<ChannelAccessServer> channelAccessServerServiceReference, ChannelAccessServer channelAccessServer) {

            }

            @Override
            public void removedService(ServiceReference<ChannelAccessServer> reference, ChannelAccessServer channelAccessServer) {
                if (topServiceTracker != null) {
                    topServiceTracker.close();
                    topServiceTracker = null;
                }
            }
        });
        caServiceTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (caServiceTracker != null) {
            caServiceTracker.close();
            caServiceTracker = null;
        }
        if (topServiceTracker != null) {
            topServiceTracker.close();
            topServiceTracker = null;
        }
    }
}
