package edu.gemini.aspen.gmp.logging.osgi;

import edu.gemini.aspen.gmp.logging.LoggingMessageConsumer;
import edu.gemini.jms.api.JmsArtifact;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private ServiceRegistration<JmsArtifact> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        registration = context.registerService(JmsArtifact.class, new LoggingMessageConsumer(), new Hashtable<String, Object>());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (registration != null) {
            registration.unregister();
            registration = null;
        }
    }
}
