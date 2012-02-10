package edu.gemini.jms.api.osgi;

import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import javax.jms.JMSException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This provides a tracker of a JMS Provider.
 */
public class JmsProviderTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(JmsProviderTracker.class.getName());

    private final JmsArtifact[] _jmsArtifacts;

    /**
     * Constructor. Takes as arguments the bundle context, the name of the tracker
     * @param ctx  The bundle context
     * @param artifacts JMS Artifacts that will be initiated upon discovery of
     */
    public JmsProviderTracker(BundleContext ctx, JmsArtifact... artifacts) {
        super(ctx, JmsProvider.class.getName(), null);
        _jmsArtifacts = Arrays.copyOf(artifacts, artifacts.length);
        LOG.log(Level.WARNING, "ActiveMQBroker built JMSProviderTracjker tracker for " + Arrays.toString(_jmsArtifacts), new RuntimeException());
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);

        try {
            for (JmsArtifact jmsArtifact: _jmsArtifacts) {
                LOG.log(Level.WARNING, "ActiveMQBroker starting provider for " + jmsArtifact, new RuntimeException());
                jmsArtifact.startJms(provider);
            }
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem starting JMS Artifacts", e);
        }
        return provider;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {

        for (JmsArtifact jmsArtifact: _jmsArtifacts) {
            jmsArtifact.stopJms();
        }
        context.ungetService(serviceReference);
    }
}