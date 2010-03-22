package edu.gemini.aspen.gmp.logging.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import edu.gemini.jms.api.osgi.JmsProviderTracker;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.aspen.gmp.logging.jms.LoggingListener;
import edu.gemini.aspen.gmp.logging.DefaultLogProcessor;

/**
 * OSGi Activator for the Services bundle
 */
public class Activator implements BundleActivator {

    private JmsProviderTracker _providerTracker;

    private final BaseMessageConsumer _messageConsumer;

    public Activator() {

        //Creates the Logging Message Consumer
        _messageConsumer = new BaseMessageConsumer(
                "Logging Message Consumer",
                new DestinationData(LoggingListener.DESTINATION_NAME,
                                    DestinationType.TOPIC),
                new LoggingListener(new DefaultLogProcessor())
        );
    }
    
    public void start(BundleContext bundleContext) throws Exception {
        _providerTracker = new JmsProviderTracker(bundleContext, "Logging Service");
        _providerTracker.registerJmsArtifact(_messageConsumer);
        _providerTracker.open();
    }

    public void stop(BundleContext bundleContext) throws Exception {
        _providerTracker.close();
        _providerTracker = null;
    }
}