package edu.gemini.aspen.heartbeatdistributor.osgi;

import edu.gemini.aspen.heartbeatdistributor.HeartbeatConsumer;
import edu.gemini.aspen.heartbeatdistributor.HeartbeatDistributor;
import edu.gemini.jms.api.JmsArtifact;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private HeartbeatDistributor distributor;
    private ServiceRegistration<JmsArtifact> registration;
    private ServiceTracker<HeartbeatConsumer, HeartbeatConsumer> heartbeatConsumerServiceTracker;

    @Override
    public void start(final BundleContext context) {
        distributor = new HeartbeatDistributor();
        registration = context.registerService(JmsArtifact.class, distributor, new Hashtable<>());

        heartbeatConsumerServiceTracker = new ServiceTracker<>(context, HeartbeatConsumer.class, new ServiceTrackerCustomizer<HeartbeatConsumer, HeartbeatConsumer>() {
            @Override
            public HeartbeatConsumer addingService(ServiceReference<HeartbeatConsumer> reference) {
                HeartbeatConsumer consumer = context.getService(reference);
                distributor.bindHeartbeatConsumer(consumer);
                return consumer;
            }

            @Override
            public void modifiedService(ServiceReference<HeartbeatConsumer> reference, HeartbeatConsumer heartbeatConsumer) {

            }

            @Override
            public void removedService(ServiceReference<HeartbeatConsumer> reference, HeartbeatConsumer heartbeatConsumer) {
                distributor.unbindHeartbeatConsumer(heartbeatConsumer);
            }
        });
        heartbeatConsumerServiceTracker.open(true);
    }

    @Override
    public void stop(BundleContext context) {
        if (registration != null) {
            registration.unregister();
            registration = null;
        }
        if (heartbeatConsumerServiceTracker != null) {
            heartbeatConsumerServiceTracker.close();
            heartbeatConsumerServiceTracker = null;
        }
    }
}
