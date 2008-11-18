package edu.gemini.aspen.gmp.broker.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.logging.Logger;

import edu.gemini.aspen.gmp.broker.impl.GMPServiceImpl;
import edu.gemini.aspen.gmp.broker.api.GMPService;
import edu.gemini.aspen.gmp.broker.jms.JMSCompletionInfoConsumer;

/**
 * The OSGi Activator for the GMP Service
 */
public class Activator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(
            Activator.class.getName());
    private JmsProviderTracker _jmsTracker;

    public void start(BundleContext bundleContext) throws Exception {
        LOG.info("Start tracking for JMS Provider");
        _jmsTracker = new JmsProviderTracker(bundleContext);
        _jmsTracker.open();


    }

    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stop tracking for JMS Provider");
        _jmsTracker.close();
        _jmsTracker = null;
    }
}
