package edu.gemini.aspen.giapi.data.obsevents.osgi;

import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import edu.gemini.aspen.giapi.data.obsevents.ObservationEventAction;
import edu.gemini.aspen.giapi.data.obsevents.ObservationEventHandlerComposite;
import edu.gemini.aspen.giapi.data.obsevents.jms.JmsObservationEventListener;
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
 * Activator for the Observation Event Monitor
 */
public class Activator implements BundleActivator, ServiceTrackerCustomizer<ObservationEventHandler, ObservationEventHandler> {

    private static final Logger LOG = Logger.getLogger(
            Activator.class.getName());

    private ServiceTracker<ObservationEventHandler, ObservationEventHandler> _tracker = null;

    private ObservationEventHandlerComposite handlerComposite = null;

    private BundleContext context = null;

    public void start(BundleContext bundleContext) {
        context = bundleContext;

        handlerComposite = new ObservationEventAction();

        BaseMessageConsumer consumer = new BaseMessageConsumer(
               "JMS Observation Event Monitor",
                new DestinationData(JmsObservationEventListener.TOPIC_NAME,
                        DestinationType.TOPIC),
                new JmsObservationEventListener(handlerComposite)
        );

        bundleContext.registerService(JmsArtifact.class.getName(), consumer, null);

        //and start tracking for observation event handlers as well...
        _tracker = new ServiceTracker<>(bundleContext, ObservationEventHandler.class.getName(), this);
        _tracker.open();
    }

    public void stop(BundleContext bundleContext) {
        LOG.info("Stop tracking for JMS Provider");

        _tracker.close();
        _tracker = null;

        handlerComposite = null;
    }

    @Override
    public ObservationEventHandler addingService(ServiceReference<ObservationEventHandler> serviceReference) {

        LOG.info("Observation Event Handler added.");
        ObservationEventHandler handler = context.getService(serviceReference);
        handlerComposite.registerHandler(handler);

        return handler;

    }

    @Override
    public void removedService(ServiceReference<ObservationEventHandler> serviceReference, ObservationEventHandler o) {
        LOG.info("Observation Handler removed.");
        ObservationEventHandler handler = context.getService(serviceReference);
        handlerComposite.unregisterHandler(handler);
    }

    @Override
    public void modifiedService(ServiceReference<ObservationEventHandler> serviceReference, ObservationEventHandler o) {
        //do nothing
    }
}
