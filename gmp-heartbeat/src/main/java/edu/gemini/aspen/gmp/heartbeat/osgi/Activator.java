package edu.gemini.aspen.gmp.heartbeat.osgi;

import edu.gemini.aspen.giapi.status.setter.StatusSetter;
import edu.gemini.aspen.gmp.heartbeat.Heartbeat;
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
    private ServiceTracker<Top, Top> topServiceTracker;
    private ServiceRegistration<ManagedServiceFactory> factoryService;
    private ServiceTracker<StatusSetter, StatusSetter> ssServiceTracker;

    @Override
    public void start(final BundleContext context) {
        topServiceTracker = new ServiceTracker<>(context, Top.class, new ServiceTrackerCustomizer<Top, Top>() {

            @Override
            public Top addingService(ServiceReference<Top> topReference) {
                final Top top = context.getService(topReference);
                ssServiceTracker = new ServiceTracker<>(context, StatusSetter.class, new ServiceTrackerCustomizer<StatusSetter, StatusSetter>() {
                    private HeartbeatFactory factory;

                    @Override
                    public StatusSetter addingService(ServiceReference<StatusSetter> ssReference) {
                        StatusSetter ss = context.getService(ssReference);
                        factory = new HeartbeatFactory(context, top, ss);

                        Hashtable<String, String> props = new Hashtable<>();
                        props.put("service.pid", Heartbeat.class.getName());

                        factoryService = context.registerService(ManagedServiceFactory.class, factory, props);

                        return null;
                    }

                    @Override
                    public void modifiedService(ServiceReference<StatusSetter> reference, StatusSetter ss) {

                    }

                    @Override
                    public void removedService(ServiceReference<StatusSetter> reference, StatusSetter ss) {
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
                ssServiceTracker.open();
                return top;
            }

            @Override
            public void modifiedService(ServiceReference<Top> reference, Top top) {

            }

            @Override
            public void removedService(ServiceReference<Top> reference, Top top) {
                if (ssServiceTracker != null) {
                    ssServiceTracker.close();
                    ssServiceTracker = null;
                }
            }
        });
        topServiceTracker.open();
    }

    @Override
    public void stop(BundleContext context) {
        if (factoryService != null) {
            factoryService.unregister();
            factoryService = null;
        }
        if (ssServiceTracker != null) {
            ssServiceTracker.close();
            ssServiceTracker = null;
        }
        if (topServiceTracker != null) {
            topServiceTracker.close();
            topServiceTracker = null;
        }
    }
}
