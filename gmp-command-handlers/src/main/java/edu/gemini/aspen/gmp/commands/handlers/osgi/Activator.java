package edu.gemini.aspen.gmp.commands.handlers.osgi;

import edu.gemini.aspen.gmp.commands.handlers.CommandHandlers;
import edu.gemini.aspen.gmp.commands.handlers.impl.CommandHandlersImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private ServiceRegistration<CommandHandlers> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        registration = context.registerService(CommandHandlers.class, new CommandHandlersImpl(), new Hashtable<String, Object>());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (registration != null) {
            registration.unregister();
            registration = null;
        }
    }
}
