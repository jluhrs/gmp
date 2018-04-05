package edu.gemini.aspen.giapi.status.setter.osgi;

import edu.gemini.aspen.giapi.status.setter.StatusSetter;
import edu.gemini.aspen.giapi.status.setter.StatusSetterService;
import edu.gemini.jms.api.JmsArtifact;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private ServiceRegistration<?> serviceRegistration = null;

    @Override
    public void start(BundleContext bundleContext) {
        String[] interfaces = new String[] {JmsArtifact.class.getName(), StatusSetter.class.getName()};
        serviceRegistration = bundleContext.registerService(interfaces, new StatusSetterService(), new Hashtable<>());
    }

    @Override
    public void stop(BundleContext bundleContext) {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            serviceRegistration = null;
        }
    }
}
