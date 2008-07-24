package edu.gemini.aspen.gmp.servlet.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Hashtable;

import edu.gemini.aspen.gmp.servlet.www.GmpServlet;

import javax.servlet.ServletException;

/**
 *
 */
public class HttpTracker extends ServiceTracker {


    private static final Logger LOG = Logger.getLogger(HttpTracker.class.getName());

    private static final String APP_CONTEXT = "/gmp";

    private HttpService _service;

    private ServletSupervisor _supervisor;

    public HttpTracker(BundleContext ctx) {
        super(ctx, HttpService.class.getName(), null);
    }

    public void registerSupervisor(ServletSupervisor supervisor) {
        _supervisor = supervisor;
    }


    public Object addingService(ServiceReference serviceReference) {

        LOG.info("Adding HttpService");

        _service = (HttpService) context.getService(serviceReference);

        _supervisor.attemptRegisterServlet();

        return _service;
    }

    public void removedService(ServiceReference serviceReference, Object service) {
        LOG.info("Removing HttpService");
        HttpService http = (HttpService) service;
        http.unregister(APP_CONTEXT);
        _service = null;
        context.ungetService(serviceReference);
    }

    public HttpService getHttpService() {
        return _service;
    }


    public void registerServlet(GmpServlet servlet) {
        LOG.info("Registering servlet");
        try {
            _service.registerServlet(APP_CONTEXT, servlet, new Hashtable(), null);
        } catch (ServletException ex) {
            LOG.log(Level.SEVERE, "Trouble setting up web interface", ex);
        } catch (NamespaceException ex) {
            LOG.log(Level.SEVERE, "Trouble setting up web interface", ex);
        }
    }


    public void unregisterServlet() {
        if (_service != null) {
            LOG.info("Unregistering servlet");
            _service.unregister(APP_CONTEXT);
        }
    }
}
