package edu.gemini.aspen.gmp.servlet.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.logging.Logger;

/**
 * The OSGi activator for the GMP Servlet service
 */
public class Activator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());

    private ServletSupervisor _supervisor;

    public void start(BundleContext bundleContext) throws Exception {
        LOG.info("Start GMP Servlet Interface service bundle");
        _supervisor = new ServletSupervisor(bundleContext);
    }

    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stop GMP Servlet Interface service bundle");
        _supervisor.close();
        _supervisor = null;
    }
}
