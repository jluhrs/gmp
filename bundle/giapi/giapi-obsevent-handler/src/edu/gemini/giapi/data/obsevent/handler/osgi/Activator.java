package edu.gemini.giapi.data.obsevent.handler.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import edu.gemini.giapi.data.obsevent.handler.TestHandler;
import edu.gemini.aspen.gmp.data.ObservationEventHandler;

/**
 * 
 */
public class Activator implements BundleActivator {

    private ServiceRegistration _registration;

    private TestHandler _handler;

    public void start(BundleContext bundleContext) throws Exception {
        
        _handler = new TestHandler();
        _registration = bundleContext.registerService(
                ObservationEventHandler.class.getName(),
                _handler, null);
    }

    public void stop(BundleContext bundleContext) throws Exception {


        //notify the OSGi framework this service is not longer available
        _registration.unregister();
        _handler = null;

    }
}
