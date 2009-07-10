package edu.gemini.aspen.gmp.servlet.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.logging.Logger;

import edu.gemini.aspen.gmp.commands.api.CommandSender;
import edu.gemini.aspen.gmp.servlet.www.GmpServlet;

/**
 * Service tracker that will get notifications when the GMP Service is available
 */

public class GmpTracker extends ServiceTracker {


    private static final Logger LOG = Logger.getLogger(GmpTracker.class.getName());

    private GmpServlet _servlet;

    private ServletSupervisor _supervisor;

    public GmpTracker(BundleContext ctx) {
        super(ctx, CommandSender.class.getName(), null);
    }

    public void registerSupervisor(ServletSupervisor supervisor) {
        _supervisor = supervisor;
    }

    public Object addingService(ServiceReference serviceReference) {

        LOG.info("Adding GMP Service");

        CommandSender service = (CommandSender)context.getService(serviceReference);

        _servlet = new GmpServlet(service);

        _supervisor.attemptRegisterServlet();

        return service;
    }

    public void removedService(ServiceReference serviceReference, Object service) {
        LOG.info("Removing GMP Service");
        _servlet = null;
        context.ungetService(serviceReference);
        _supervisor.unregisterServlet();

    }


    /**
     * Returns a reference to the servlet. Don't hold references to this servlet for
     * too long, since this object can disappear.
     * @return Servlet reference
     */

    public GmpServlet getGmpServlet() {
        return _servlet;
    }

}