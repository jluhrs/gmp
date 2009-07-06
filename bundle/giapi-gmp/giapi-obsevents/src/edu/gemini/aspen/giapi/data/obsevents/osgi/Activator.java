package edu.gemini.aspen.giapi.data.obsevents.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;

import java.util.logging.Logger;

import edu.gemini.aspen.gmp.data.ObservationEventHandler;
import edu.gemini.aspen.giapi.data.obsevents.jms.JmsObservationEventListener;
import edu.gemini.aspen.giapi.data.obsevents.ObservationEventHandlerComposite;
import edu.gemini.aspen.giapi.data.obsevents.ObservationEventAction;
import edu.gemini.jms.api.BaseMessageConsumer;

/**
 *
 */
public class Activator implements BundleActivator, ServiceTrackerCustomizer {

    private static final Logger LOG = Logger.getLogger(
            Activator.class.getName());

    private JmsProviderTracker _jmsTracker;

    private ServiceTracker _tracker = null;

    private ObservationEventHandlerComposite handlerComposite = null;

    private BundleContext context = null;

    public void start(BundleContext bundleContext) throws Exception {
        context = bundleContext;

        handlerComposite = new ObservationEventAction();

        BaseMessageConsumer consumer = new BaseMessageConsumer(
               "JMS Observation Event Monitor",
                JmsObservationEventListener.TOPIC_NAME,
                new JmsObservationEventListener(handlerComposite)
        );

        LOG.info("Start tracking for JMS Provider");

        _jmsTracker = new JmsProviderTracker(bundleContext, consumer);
        _jmsTracker.open();

        //and start tracking for observation event handlers as well...
        _tracker = new ServiceTracker(bundleContext, ObservationEventHandler.class.getName(), this);
        _tracker.open();


    }

    public void stop(BundleContext bundleContext) throws Exception {
        LOG.info("Stop tracking for JMS Provider");
        _jmsTracker.close();
        _jmsTracker = null;

        _tracker.close();
        _tracker = null;

        handlerComposite = null;
    }

    public void removedService(ServiceReference serviceReference, Object o) {
        LOG.info("Observation Handler removed.");
        ObservationEventHandler handler = (ObservationEventHandler) context.getService(serviceReference);
        handlerComposite.unregisterHandler(handler);
    }

    public Object addingService(ServiceReference serviceReference) {

        LOG.info("Observation Event Handler added.");
        ObservationEventHandler handler = (ObservationEventHandler) context.getService(serviceReference);
        handlerComposite.registerHandler(handler);

        return handler;

    }

    public void modifiedService(ServiceReference serviceReference, Object o) {
        //do nothing
    }
}
