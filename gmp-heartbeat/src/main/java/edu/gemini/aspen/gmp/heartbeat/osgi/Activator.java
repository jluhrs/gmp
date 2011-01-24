package edu.gemini.aspen.gmp.heartbeat.osgi;

import edu.gemini.aspen.gmp.heartbeat.Heartbeat;
import edu.gemini.jms.api.osgi.JmsProviderTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.logging.Logger;

/**
 * The OSGi Activator class for the heartbeat
 */
public class Activator implements BundleActivator {

    public static final Logger LOG = Logger.getLogger(Activator.class.getName());
    private Heartbeat hb;

    public void start(BundleContext bundleContext) throws Exception {
        LOG.info("Starting Heartbeat");

        hb=new Heartbeat();
        hb.start("tcp://localhost:61616");
    }

    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stopping Heartbeat");
        hb.stop();
        hb=null;
    }
}
