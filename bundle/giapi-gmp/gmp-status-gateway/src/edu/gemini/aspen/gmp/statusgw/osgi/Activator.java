package edu.gemini.aspen.gmp.statusgw.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator class for the Status Gateway bundle
 */
public class Activator implements BundleActivator {

    private Supervisor _supervisor;

    private StatusDatabaseTracker _tracker;
    private JmsProviderTracker _providerTracker;


    public void start(BundleContext bundleContext) throws Exception {

        _supervisor = new Supervisor();
        //Look for the status database
        _tracker = new StatusDatabaseTracker(bundleContext, _supervisor);
        _tracker.open();

        _providerTracker = new JmsProviderTracker(bundleContext, _supervisor);
        _providerTracker.open();

    }

    public void stop(BundleContext bundleContext) throws Exception {


        _providerTracker.close();
        _providerTracker = null;

        _tracker.close();
        _tracker = null;

        _supervisor = null;
    }
}