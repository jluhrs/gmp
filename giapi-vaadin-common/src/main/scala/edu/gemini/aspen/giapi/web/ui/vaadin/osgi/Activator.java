package edu.gemini.aspen.giapi.web.ui.vaadin.osgi;

import edu.gemini.aspen.giapi.web.ui.vaadin.services.StaticResources;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import javax.servlet.Servlet;
import java.util.Hashtable;

public class Activator implements BundleActivator {

    private ServiceRegistration<?> serviceRegistration = null;

    @Override
    public void start(BundleContext bundleContext) {
        String[] interfaces = new String[] {Servlet.class.getName()};
        Hashtable<String, Object> properties = new Hashtable<String, Object>();
        properties.put("alias", "/VAADIN");
        properties.put("servlet-key", "VaadinResourcesServlet");
        serviceRegistration = bundleContext.registerService(interfaces, new StaticResources(bundleContext), properties);
    }

    @Override
    public void stop(BundleContext bundleContext) {
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            serviceRegistration = null;
        }
    }
}
