package edu.gemini.aspen.gmp.epics.simulator.osgi;

import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.simulator.EpicsSimulatorComponent;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Hashtable;

public class Activator implements BundleActivator {
    private ServiceTracker<EpicsRegistrar, EpicsRegistrar> erServiceTracker;
    private EpicsSimulatorFactory factory;
    private ServiceRegistration<ManagedServiceFactory> factoryService;

    @Override
    public void start(final BundleContext context) throws Exception {
        erServiceTracker = new ServiceTracker<EpicsRegistrar, EpicsRegistrar>(context, EpicsRegistrar.class, new ServiceTrackerCustomizer<EpicsRegistrar, EpicsRegistrar>() {

            @Override
            public EpicsRegistrar addingService(ServiceReference<EpicsRegistrar> reference) {
                EpicsRegistrar epicsRegistrar = context.getService(reference);
                factory = new EpicsSimulatorFactory(epicsRegistrar);

                Hashtable<String, String> props = new Hashtable<String, String>();
                props.put("service.pid", EpicsSimulatorComponent.class.getName());

                factoryService = context.registerService(ManagedServiceFactory.class, factory, props);

                return epicsRegistrar;
            }

            @Override
            public void modifiedService(ServiceReference<EpicsRegistrar> epicsRegistrarServiceReference, EpicsRegistrar epicsRegistrar) {

            }

            @Override
            public void removedService(ServiceReference<EpicsRegistrar> epicsRegistrarServiceReference, EpicsRegistrar epicsRegistrar) {
                if (factoryService != null) {
                    factoryService.unregister();
                    factoryService = null;
                }
                if (factory != null) {
                    factory.stopServices();
                    factory = null;
                }
            }
        });
        erServiceTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (erServiceTracker != null) {
            erServiceTracker.close();
            erServiceTracker = null;
        }
        if (factoryService != null) {
            factoryService.unregister();
            factoryService = null;
        }
        if (factory != null) {
            factory.stopServices();
            factory = null;
        }
    }
}
