package edu.gemini.aspen.gmp.epics.osgi;

import edu.gemini.aspen.gmp.epics.EpicsRequestHandler;
import edu.gemini.aspen.gmp.epics.impl.EpicsRequestHandlerImpl;
import edu.gemini.epics.EpicsReader;
import edu.gemini.jms.api.JmsArtifact;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Hashtable;

public class Activator implements BundleActivator {
    private ServiceTracker<EpicsReader, EpicsReader> erServiceTracker;
    private ServiceRegistration<?> erhRegistration;

    @Override
    public void start(final BundleContext context) throws Exception {
        erServiceTracker = new ServiceTracker<EpicsReader, EpicsReader>(context, EpicsReader.class, new ServiceTrackerCustomizer<EpicsReader, EpicsReader>() {

            @Override
            public EpicsReader addingService(ServiceReference<EpicsReader> reference) {
                EpicsReader epicsReader = context.getService(reference);
                EpicsRequestHandlerImpl epicsRequestHandler = new EpicsRequestHandlerImpl(epicsReader);
                erhRegistration = context.registerService(new String[]{EpicsRequestHandler.class.getName(), JmsArtifact.class.getName()}, epicsRequestHandler, new Hashtable<String, Object>());
                return epicsReader;
            }

            @Override
            public void modifiedService(ServiceReference<EpicsReader> EpicsReaderServiceReference, EpicsReader EpicsReader) {

            }

            @Override
            public void removedService(ServiceReference<EpicsReader> EpicsReaderServiceReference, EpicsReader EpicsReader) {
                if (erhRegistration != null) {
                    erhRegistration.unregister();
                    erhRegistration = null;
                }
            }
        });
        erServiceTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (erServiceTracker != null) {
            erServiceTracker.close();
            erServiceTracker = null;
        }
        if (erhRegistration != null) {
            erhRegistration.unregister();
            erhRegistration = null;
        }
    }
}
