package edu.gemini.aspen.gmp.servlet.osgi;

import edu.gemini.aspen.gmp.servlet.www.GmpServlet;
import org.osgi.service.http.HttpService;
import org.osgi.framework.BundleContext;

/**
 * The supervisor will register the GMP Servlet if and only if
 * both the HTTP service and the GMP Service are available. Will be in charge
 * alse of stopping the servlet service in case of any of those services 
 * dissapear.
 */
public class ServletSupervisor {


    private GmpTracker _gmpTracker;

    private HttpTracker _httpTracker;

    public ServletSupervisor(BundleContext ctx) {

        _gmpTracker = new GmpTracker(ctx);
        _httpTracker = new HttpTracker(ctx);

        _gmpTracker.registerSupervisor(this);
        _httpTracker.registerSupervisor(this);

        _httpTracker.open();
        _gmpTracker.open();
    }


    /**
     * Attempts to register the GMP Servlet. It verifies
     * both the HttpService and the GMP service are available and
     * that the the GMP servlet has been instantiated. 
     */
    public void attemptRegisterServlet() {

        if (_httpTracker == null || _gmpTracker == null) return;

        GmpServlet servlet = _gmpTracker.getGmpServlet();
        HttpService httpService = _httpTracker.getHttpService();
        if (servlet != null && httpService != null) {
            _httpTracker.registerServlet(servlet);
        }
    }

    public void unregisterServlet() {
        if (_httpTracker != null) {
            _httpTracker.unregisterServlet();
        }
    }


    public void close() {

        _gmpTracker.close();
        _gmpTracker = null;
        
        _httpTracker.close();
        _httpTracker = null;


    }
}
