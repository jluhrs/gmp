package edu.gemini.giapi.data.fileevent.handler.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import edu.gemini.giapi.data.fileevent.handler.TestHandler;
import edu.gemini.aspen.giapi.data.AncillaryFileEventHandler;
import edu.gemini.aspen.giapi.data.IntermediateFileEventHandler;

/**
 * Simple activator class to advertise the handler in the OSGi framework
 */
public class Activator implements BundleActivator {

    private ServiceRegistration _ancillaryFileEventHandlerRegistration;
    private ServiceRegistration _intermediateFileEventHandlerRegistration;

    private TestHandler _handler;

    public void start(BundleContext bundleContext) throws Exception {

        _handler = new TestHandler();
        /**
         * Advertise the handler as an ancillary handler
         */
        _ancillaryFileEventHandlerRegistration = bundleContext.registerService(
                AncillaryFileEventHandler.class.getName(),
                _handler, null);

        /**
         * Advertise the handler as an intermediate handler as well
         */
        _intermediateFileEventHandlerRegistration = bundleContext.registerService(
                IntermediateFileEventHandler.class.getName(),
                _handler, null);
    }

    public void stop(BundleContext bundleContext) throws Exception {


        //notify the OSGi framework this service is not longer available
        _ancillaryFileEventHandlerRegistration.unregister();
        _intermediateFileEventHandlerRegistration.unregister();
        _handler = null;

    }
}