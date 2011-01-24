package edu.gemini.jms.api.osgi;

import edu.gemini.jms.api.JmsArtifact;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;

import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;

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
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);

        try {
            for (JmsArtifact jmsArtifact: _jmsArtifacts) {
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