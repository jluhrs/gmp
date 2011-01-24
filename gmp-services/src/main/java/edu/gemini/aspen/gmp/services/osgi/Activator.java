package edu.gemini.aspen.gmp.services.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * OSGi Activator for the Services bundle
 */
public class Activator implements BundleActivator {

    private JmsProviderTracker _providerTracker;

    public void start(BundleContext bundleContext) throws Exception {
        _providerTracker = new JmsProviderTracker(bundleContext);
        _providerTracker.open();
    }

    public void stop(BundleContext bundleContext) throws Exception {
        _providerTracker.close();
        _providerTracker = null;
    }
}
