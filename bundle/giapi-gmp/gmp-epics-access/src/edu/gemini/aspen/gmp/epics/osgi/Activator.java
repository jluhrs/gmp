package edu.gemini.aspen.gmp.epics.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;
import edu.gemini.aspen.gmp.epics.EpicsMonitor;
import edu.gemini.aspen.gmp.epics.EpicsConfiguration;
import edu.gemini.aspen.gmp.epics.jms.EpicsConfigRequestConsumer;
import edu.gemini.aspen.gmp.epics.jms.EpicsStatusUpdater;
import edu.gemini.epics.IEpicsClient;
import edu.gemini.jms.api.JmsProvider;

import java.util.Hashtable;
import java.util.Dictionary;
import java.util.logging.Logger;


/**
 * The activator class for the Epics Access bundle
 */
public class Activator implements BundleActivator, ServiceTrackerCustomizer {

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());


    private ServiceTracker _tracker = null;


    private EpicsConfigRequestConsumer _epicsRequestConsumer;
    private EpicsStatusUpdater _epicsStatusUpdater;


    private BundleContext context = null;

    private EpicsMonitor _monitor;
    private EpicsConfiguration _epicsConfig;

    public void start(BundleContext bundleContext) throws Exception {
        context = bundleContext;

        _epicsConfig = new OsgiEpicsConfiguration(bundleContext);

        _monitor = new EpicsMonitor(_epicsConfig);


        Dictionary<String, Object> props = new Hashtable<String, Object>();

        props.put(IEpicsClient.EPICS_CHANNELS, _monitor.getChannels());

        if (bundleContext != null) {
            bundleContext.registerService(IEpicsClient.class.getName(), _monitor, props);
        }


        _tracker = new ServiceTracker(context, JmsProvider.class.getName(), this);
        _tracker.open();


    }

    public void stop(BundleContext bundleContext) throws Exception {
        //unregistation is automatic!
        _tracker.close();
        _tracker = null;
        context = null;
        _monitor = null;

    }

    public Object addingService(ServiceReference serviceReference) {
        LOG.info("JMS Provider found. Starting Epics Access bundle");
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);
        _epicsRequestConsumer =
                new EpicsConfigRequestConsumer(provider, _epicsConfig);

        _epicsStatusUpdater = new EpicsStatusUpdater(provider, _epicsConfig);

        for (String channel : _epicsConfig.getValidChannelsNames()) {
            _monitor.registerInterest(channel, _epicsStatusUpdater);
        }


        _epicsStatusUpdater.start();
        return provider;
    }

    public void modifiedService(ServiceReference serviceReference, Object o) {
        //do nothing
    }

    public void removedService(ServiceReference serviceReference, Object o) {
        LOG.info("Stopping Epics Access bundle");
        _epicsRequestConsumer.close();
        _epicsRequestConsumer = null;

        _epicsStatusUpdater.close();
        _epicsStatusUpdater.stop();
        _epicsStatusUpdater = null;
    }
}
