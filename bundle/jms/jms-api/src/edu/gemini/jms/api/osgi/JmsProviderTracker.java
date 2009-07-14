package edu.gemini.jms.api.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.gemini.jms.api.JmsProvider;
import edu.gemini.jms.api.BaseJmsArtifact;

import javax.jms.JMSException;

/**
 * This provides a tracker of a JMS Provider.
 */
public class JmsProviderTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(JmsProviderTracker.class.getName());

    private String _name;

    private List<BaseJmsArtifact> _jmsArtifacts;

    public JmsProviderTracker(BundleContext ctx, String name) {
        super(ctx, JmsProvider.class.getName(), null);
        _jmsArtifacts = new CopyOnWriteArrayList<BaseJmsArtifact>();
        _name = name;
    }


    public void registerJmsArtifact(BaseJmsArtifact artifact) {
        _jmsArtifacts.add(artifact);
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);

        try {
            for (BaseJmsArtifact jmsArtifact: _jmsArtifacts) {
                jmsArtifact.startJms(provider);
            }
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem starting JMS Artifacts", e);
        }
        LOG.info("Started JMS Artifacts [" + _name + ']');
        return provider;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {

        for (BaseJmsArtifact jmsArtifact: _jmsArtifacts) {
            jmsArtifact.stopJms();
        }
        LOG.info("Stopped JMS Artifacts (" + _name + ')');
        context.ungetService(serviceReference);

    }
}