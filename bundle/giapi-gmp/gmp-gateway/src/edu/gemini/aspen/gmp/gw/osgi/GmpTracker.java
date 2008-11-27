package edu.gemini.aspen.gmp.gw.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleContext;
import edu.gemini.aspen.gmp.broker.api.GMPService;

import java.util.logging.Logger;

/**
 *
 */
public class GmpTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(GmpTracker.class.getName());

    private Supervisor _supervisor;

    public GmpTracker(BundleContext ctx, Supervisor supervisor) {
        super(ctx, GMPService.class.getName(), null);
        _supervisor = supervisor;
    }
    
    @Override
    public Object addingService(ServiceReference serviceReference) {

        LOG.info("GMP Gateway has found GMP Service");
        GMPService service = (GMPService)context.getService(serviceReference);

        _supervisor.registerGmpService(service);
        _supervisor.start();

        return service;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {
        LOG.info("GMP Gateway has lost GMP Service. Stopping");
        _supervisor.stop();
        _supervisor.unregisterGmpService();
        context.ungetService(serviceReference);
    }
}
