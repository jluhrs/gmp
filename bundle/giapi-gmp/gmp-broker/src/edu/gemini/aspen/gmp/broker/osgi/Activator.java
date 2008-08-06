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

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());
    private final GMPService _service = new GMPServiceImpl();


    ServiceRegistration _registration;

    public void start(BundleContext bundleContext) throws Exception {
        LOG.info("Start GMP service bundle");
        _service.start();
        //advertise the GMP service in the OSGi framework
        _registration = bundleContext.registerService(GMPService.class.getName(),
                _service, null);

        //start the Completion Info Consumer
        new JMSCompletionInfoConsumer();
    }

    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stop GMP service bundle");

        _service.shutdown();

        //notify the OSGi framework this service is not longer available
        _registration.unregister();
    }
}
