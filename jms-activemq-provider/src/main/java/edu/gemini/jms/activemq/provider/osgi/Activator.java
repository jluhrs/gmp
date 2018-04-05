package edu.gemini.jms.activemq.provider.osgi;

import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProviderStatusListener;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private ServiceRegistration<ManagedServiceFactory> factoryService;
    private ServiceTracker<JmsArtifact, JmsArtifact> jmsArtifactTracker;

    public void start(BundleContext context) {
        Hashtable<String, String> props = new Hashtable<String, String>();
        props.put("service.pid", ActiveMQJmsProvider.class.getName());

        ActiveMQJmsProviderFactory providerFactory = new ActiveMQJmsProviderFactory(context);

        jmsArtifactTracker = new ServiceTracker<>(context, JmsArtifact.class, new JmsArtifactTracker(context, providerFactory));
        jmsArtifactTracker.open(true);

        factoryService = context.registerService(ManagedServiceFactory.class, providerFactory, props);
    }

    public void stop(BundleContext context) {
        if (factoryService != null) {
            factoryService.unregister();
            factoryService = null;
        }
        if (jmsArtifactTracker != null) {
            jmsArtifactTracker.close();
            jmsArtifactTracker = null;
        }
    }

    private class JmsArtifactTracker implements ServiceTrackerCustomizer<JmsArtifact, JmsArtifact> {
        private final BundleContext context;
        private final ActiveMQJmsProviderFactory providerFactory;

        public JmsArtifactTracker(BundleContext context, ActiveMQJmsProviderFactory providerFactory) {
            this.context = context;
            this.providerFactory = providerFactory;
        }

        @Override
        public JmsArtifact addingService(ServiceReference<JmsArtifact> reference) {
            JmsArtifact jmsArtifact = context.getService(reference);
            if (providerFactory != null) {
                providerFactory.bindJmsArtifact(jmsArtifact);
            }
            return jmsArtifact;
        }

        @Override
        public void modifiedService(ServiceReference<JmsArtifact> jmsArtifactServiceReference, JmsArtifact jmsArtifact) {

        }

        @Override
        public void removedService(ServiceReference<JmsArtifact> jmsArtifactServiceReference, JmsArtifact jmsArtifact) {
            if (providerFactory != null) {
                providerFactory.unbindJmsArtifact(jmsArtifact);
            }
        }
    }
}