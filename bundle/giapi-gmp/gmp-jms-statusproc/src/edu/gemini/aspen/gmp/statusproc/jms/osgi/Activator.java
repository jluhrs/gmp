package edu.gemini.aspen.gmp.statusproc.jms.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 *  Activator for the JMS Status processor bundle
 */
public class Activator implements BundleActivator {

    private JmsProviderTracker _tracker;


    public void start(BundleContext bundleContext) throws Exception {

        //Look for a JMS Provider, so we can start up the processor
        _tracker = new JmsProviderTracker(bundleContext);
        _tracker.open();

    }

    public void stop(BundleContext bundleContext) throws Exception {

        _tracker.close();
        _tracker = null;
    }
}