package edu.gemini.aspen.gmp.statusdb.osgi;

import edu.gemini.aspen.gmp.status.StatusProcessor;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleContext;
import edu.gemini.aspen.gmp.statusdb.StatusDatabase;

import java.util.logging.Logger;

/**
 * Tracks status processors in the OSGi environment and
 * registers them into the Status Database
 */
public class StatusProcessorTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(StatusProcessorTracker.class.getName());

    private StatusDatabase _database;

    public StatusProcessorTracker(BundleContext ctx, StatusDatabase database) {
        super(ctx, StatusProcessor.class.getName(), null);
        _database = database;
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {

        StatusProcessor processor = (StatusProcessor)context.getService(serviceReference);
        LOG.info("Adding Status Processor: " + processor.getName());
        //use this processor in the status database
        _database.registerStatusProcessor(processor);

        return processor;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {

        StatusProcessor processor = (StatusProcessor)context.getService(serviceReference);
        LOG.info("Removing Status Processor: " + processor.getName());

        _database.unregisterStatusProcessor(processor);
        

    }
}
