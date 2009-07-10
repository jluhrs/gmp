package edu.gemini.aspen.gmp.commands.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.logging.Logger;

import edu.gemini.aspen.gmp.commands.ActionManager;

/**
 * The OSGi Activator for the GMP Service
 */
public class Activator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(
            Activator.class.getName());
    private JmsProviderTracker _jmsTracker;

    private ActionManager _actionManager = new ActionManager();

    public void start(BundleContext bundleContext) throws Exception {
        LOG.info("Start tracking for JMS Provider");

        _actionManager.start();

        _jmsTracker = new JmsProviderTracker(bundleContext, _actionManager);
        _jmsTracker.open();


    }

    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stop tracking for JMS Provider");
        _jmsTracker.close();
        _jmsTracker = null;

        _actionManager.stop();
    }
}
