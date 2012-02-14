package edu.gemini.aspen.giapi.data.fileevents.osgi;

import edu.gemini.aspen.giapi.data.AncillaryFileEventHandler;
import edu.gemini.aspen.giapi.data.IntermediateFileEventHandler;
import edu.gemini.aspen.giapi.data.fileevents.FileEventActionRunner;
import edu.gemini.aspen.giapi.data.fileevents.jms.JmsFileEventsListener;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsArtifact;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.logging.Logger;

/**
 * File Events Activator class
 */
public class Activator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());

    private BundleContext context = null;

    private FileEventActionRunner _action;

    private ServiceTracker _intermediateFileHandlerTracker;
    private ServiceTracker _ancillaryFileHandlerTracker;

    public void start(BundleContext bundleContext) throws Exception {
        context = bundleContext;
        _action = new FileEventActionRunner();
        BaseMessageConsumer consumer = new BaseMessageConsumer(
                "JMS File Event Monitor",
                new DestinationData(JmsFileEventsListener.TOPIC_NAME,
                        DestinationType.TOPIC),
                new JmsFileEventsListener(_action)
        );

        bundleContext.registerService(JmsArtifact.class.getName(), consumer, null);

        //and start tracking for intermediate file event handlers as well...
        _intermediateFileHandlerTracker = new ServiceTracker(bundleContext,
                IntermediateFileEventHandler.class.getName(),
                new IntermediateFileEventServiceTracker());
        _intermediateFileHandlerTracker.open();

        //and ancillary file event handlers as well...
        _ancillaryFileHandlerTracker = new ServiceTracker(bundleContext,
                AncillaryFileEventHandler.class.getName(),
                new AncillaryFileEventServiceTracker());
        _ancillaryFileHandlerTracker.open();


    }

    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stopping File Event Monitor Bundle ");
        _intermediateFileHandlerTracker.close();
        _intermediateFileHandlerTracker = null;

        _ancillaryFileHandlerTracker.close();
        _ancillaryFileHandlerTracker = null;


        _action.shutdown();
        _action = null;
    }


    /**
     * Service tracker to detect new Intermediate file handlers available
     */
    private class IntermediateFileEventServiceTracker implements ServiceTrackerCustomizer {
        public Object addingService(ServiceReference serviceReference) {
            LOG.info("Intermediate File Event Handler added.");
            IntermediateFileEventHandler handler = (IntermediateFileEventHandler) context.getService(serviceReference);
            _action.addIntermediateFileEventHandler(handler);
            return handler;
        }

        public void modifiedService(ServiceReference serviceReference, Object o) {
            //do nothing
        }

        public void removedService(ServiceReference serviceReference, Object o) {
            LOG.info("Intermediate File Event Handler removed.");
            IntermediateFileEventHandler handler = (IntermediateFileEventHandler) context.getService(serviceReference);
            _action.removeIntermediateFileEventHandler(handler);
        }
    }

    /**
     * Service tracker to detect new Ancillary file handlers available
     */
    private class AncillaryFileEventServiceTracker implements ServiceTrackerCustomizer {
        public Object addingService(ServiceReference serviceReference) {
            LOG.info("Ancillary File Event Handler added.");
            AncillaryFileEventHandler handler = (AncillaryFileEventHandler) context.getService(serviceReference);
            _action.addAncillaryFileEventHandler(handler);
            return handler;
        }

        public void modifiedService(ServiceReference serviceReference, Object o) {
            //do nothing
        }

        public void removedService(ServiceReference serviceReference, Object o) {
            LOG.info("Ancillary File Event Handler removed.");
            AncillaryFileEventHandler handler = (AncillaryFileEventHandler) context.getService(serviceReference);
            _action.removeAncillaryFileEventHandler(handler);
        }
    }

}
