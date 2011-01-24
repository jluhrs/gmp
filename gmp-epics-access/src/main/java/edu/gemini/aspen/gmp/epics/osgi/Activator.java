package edu.gemini.aspen.gmp.epics.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;
import edu.gemini.aspen.gmp.epics.impl.EpicsMonitor;
import edu.gemini.aspen.gmp.epics.impl.EpicsUpdaterThread;
import edu.gemini.aspen.gmp.epics.EpicsConfiguration;
import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.jms.EpicsConfigRequestConsumer;
import edu.gemini.aspen.gmp.epics.jms.EpicsStatusUpdater;
import edu.gemini.epics.IEpicsClient;
import edu.gemini.jms.api.JmsProvider;

import java.util.*;
import java.util.logging.Logger;


/**
 * The activator class for the Epics Access bundle
 */
public class Activator implements BundleActivator, ServiceTrackerCustomizer {

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());

    private ServiceTracker _tracker = null;

    List<ServiceRegistration> _serviceRegistrations = new ArrayList<ServiceRegistration>();

    private EpicsConfigRequestConsumer _epicsRequestConsumer;

    private EpicsStatusUpdater _epicsStatusUpdater;

    private BundleContext context = null;

    private EpicsMonitor _monitor;

    private EpicsRegistrar _registrar;

    private EpicsConfiguration _epicsConfig;

    public void start(BundleContext bundleContext) throws Exception {
        context = bundleContext;

        _epicsConfig = new OsgiEpicsConfiguration(bundleContext);

        _registrar = new EpicsUpdaterThread();

        _registrar.start();

        _monitor = new EpicsMonitor(_registrar);


        Dictionary<String, Object> props = new Hashtable<String, Object>();

        Set<String> channels = _epicsConfig.getValidChannelsNames();

        props.put(IEpicsClient.EPICS_CHANNELS,
                channels.toArray(new String[channels.size()]));

        if (bundleContext != null) {
            //register the EpicsClient service, so it gets notification
            //with the epics channels of interest.
            _serviceRegistrations.add(
                    bundleContext.registerService(IEpicsClient.class.getName(),
                            _monitor, props));
            //register the EpicsRegistrar interface in the osgi.
            //This way, other bundles can register epics listeners also
            //(or an epics simulator can submit updates for processing!!)
            _serviceRegistrations.add(
                    bundleContext.registerService(EpicsRegistrar.class.getName(),
                            _registrar, null));
        }
        _tracker = new ServiceTracker(context, JmsProvider.class.getName(), this);
        _tracker.open();


    }

    public void stop(BundleContext bundleContext) throws Exception {
        //unregistation is automatic!
        _tracker.close();
        _tracker = null;
        _monitor = null;
        _registrar.stop();
        _registrar = null;
        _epicsConfig = null;
        for (ServiceRegistration sr: _serviceRegistrations) {
            sr.unregister();
        }
        context = null;


    }

    public Object addingService(ServiceReference serviceReference) {
        LOG.info("JMS Provider found. Starting Epics Access bundle");
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);
        _epicsRequestConsumer =
                new EpicsConfigRequestConsumer(provider, _epicsConfig);

        _epicsStatusUpdater = new EpicsStatusUpdater(provider, _epicsConfig);

        for (String channel : _epicsConfig.getValidChannelsNames()) {
            _registrar.registerInterest(channel, _epicsStatusUpdater);
        }

        return provider;
    }

    public void modifiedService(ServiceReference serviceReference, Object o) {
        //do nothing
    }

    public void removedService(ServiceReference serviceReference, Object o) {
        LOG.info("Stopping Epics Access bundle");
        _epicsRequestConsumer.close();
        _epicsRequestConsumer = null;

        for (String channel : _epicsConfig.getValidChannelsNames()) {
            _registrar.unregisterInterest(channel);
        }

        _epicsStatusUpdater.close();
        _epicsStatusUpdater = null;
    }
}
