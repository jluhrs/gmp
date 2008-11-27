package edu.gemini.aspen.gmp.gw.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * This is the Bundle Activator for the GMP Gateway bundle
 */
public class Activator implements BundleActivator {

    private Supervisor _supervisor;
    private JmsProviderTracker _providerTracker;
    private GmpTracker _gmpTracker;

    public void start(BundleContext bundleContext) throws Exception {

        _supervisor = new Supervisor();
        _gmpTracker = new GmpTracker(bundleContext, _supervisor);
        _gmpTracker.open();

        _providerTracker = new JmsProviderTracker(bundleContext, _supervisor);
        _providerTracker.open();
    }

    public void stop(BundleContext bundleContext) throws Exception {
        _gmpTracker.close();
        _gmpTracker = null;

        _providerTracker.close();
        _providerTracker = null;

        _supervisor = null;

    }
}
