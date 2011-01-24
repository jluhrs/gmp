package edu.gemini.aspen.gmp.epics.simulator.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;

import java.util.logging.Logger;
import java.util.Set;

import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.simulator.Simulator;
import edu.gemini.aspen.gmp.epics.simulator.SimulatedEpicsChannel;
import edu.gemini.aspen.gmp.epics.simulator.SimulatedEpicsConfiguration;

/**
 * OSGi Activator for the EPICS simulator bundle 
 */
public class Activator implements BundleActivator, ServiceTrackerCustomizer {

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());


    private ServiceTracker _tracker;

    private BundleContext context;

    private Simulator _simulator;


    private SimulatedEpicsConfiguration conf;

    public void start(BundleContext bundleContext) throws Exception {


        context = bundleContext;

        conf = new OsgiSimulatedEpicsConfiguration(bundleContext);

        _tracker = new ServiceTracker(context, EpicsRegistrar.class.getName(), this);
        _tracker.open();


    }

    public void stop(BundleContext bundleContext) throws Exception {
        _tracker.close();
        _tracker = null;
    }

    public Object addingService(ServiceReference serviceReference) {

        LOG.info("GMP Epics Registrar module found. Starting simulation");
        EpicsRegistrar registrar = (EpicsRegistrar)context.getService(serviceReference);

        _simulator = new Simulator(registrar);

        Set<SimulatedEpicsChannel> channels = conf.getSimulatedChannels();

        if (channels != null) {
            for (SimulatedEpicsChannel channel : channels) {
                LOG.info("Starting simulation of channel " + channel.getName());
                _simulator.startSimulation(channel);
            }
        } else {
            LOG.warning("No valid simulated channels were found in the configuration files");
        }
        return registrar;
    }

    public void modifiedService(ServiceReference serviceReference, Object o) {
        //nothing
    }

    public void removedService(ServiceReference serviceReference, Object o) {

        LOG.info("GMP Epics Registrar lost. Simulation stopped");
        _simulator.stopSimulation();
        _simulator = null;

    }
}
