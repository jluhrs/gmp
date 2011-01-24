package edu.gemini.aspen.gmp.tcs.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.osgi.JmsProviderTracker;
import edu.gemini.aspen.gmp.tcs.jms.TcsContextRequestListener;
import edu.gemini.aspen.gmp.tcs.jms.JmsTcsContextDispatcher;
import edu.gemini.aspen.gmp.tcs.model.TcsContextFetcher;
import edu.gemini.aspen.gmp.tcs.model.EpicsTcsContextFetcher;
import edu.gemini.aspen.gmp.tcs.model.TcsContextException;
import edu.gemini.aspen.gmp.tcs.model.SimTcsContextFetcher;
import edu.gemini.epics.IEpicsReader;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Activator class for the TCS Context Service
 */
public class Activator implements BundleActivator, ServiceTrackerCustomizer {

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());

    private static final String TCS_CTX_CHANNEL_PROP = "edu.gemini.aspen.gmp.tcs.epicsChannel";
    private static final String TCS_CTX_SIMULATION_PROP = "edu.gemini.aspen.gmp.tcs.simulation";
    private static final String TCS_CTX_SIMULATION_DATA_PROP = "edu.gemini.aspen.gmp.tcs.simulationData";

    /**
     * Message consumer used to receive TCS Context requests
     */
    private BaseMessageConsumer _messageConsumer;

    /**
     * Tracker of JMS Service. Used to initialize the different
     * JMS objects in this class
     */
    private JmsProviderTracker _jmsTracker;

    /**
     * The JMS Context Dispatcher is a JMS Producer message
     * that will send the TCS Context to the requester
     */
    private JmsTcsContextDispatcher _dispatcher;

    /**
     * Tracker of the EPICS service, to provide access to
     * the EPICS layer to obtain the TCS Context
     */
    private ServiceTracker _epicsTracker = null;

    private BundleContext _context = null;

    /**
     * JMS Listener to process the TCS Context requests.
     */
    private TcsContextRequestListener _listener = null;

    public Activator() {

        _dispatcher = new JmsTcsContextDispatcher("TCS Context Replier");

        _listener = new TcsContextRequestListener(_dispatcher);
        //Creates the TCS Context Request Consumer
        _messageConsumer = new BaseMessageConsumer(
                "JMS TCS Context Request Consumer",
                new DestinationData(TcsContextRequestListener.DESTINATION_NAME,
                        DestinationType.TOPIC),
                _listener
        );
    }

    public void start(BundleContext bundleContext) throws Exception {
        _context = bundleContext;

        _jmsTracker = new JmsProviderTracker(bundleContext,
                _dispatcher, _messageConsumer
        );
        _jmsTracker.open();


        String sim = _context.getProperty(TCS_CTX_SIMULATION_PROP);

        if (sim == null || "yes".equalsIgnoreCase(sim)) {
            String file = _context.getProperty(TCS_CTX_SIMULATION_DATA_PROP);
            LOG.info("Simulating data for TCS Context from " + file);
            _listener.registerTcsContextFetcher(new SimTcsContextFetcher(file));

        } else {
            //Start the EPICS tracker, so we can get the TCS context from
            //EPICS
            _epicsTracker = new ServiceTracker(bundleContext, IEpicsReader.class.getName(), this);
            _epicsTracker.open();
        }


        LOG.info("TCS Context Service started");

    }

    public void stop(BundleContext bundleContext) throws Exception {

        if (_epicsTracker != null) {
            _epicsTracker.close();
            _epicsTracker = null;
        }
        _jmsTracker.close();
        _jmsTracker = null;
    }

    public Object addingService(ServiceReference serviceReference) {

        IEpicsReader reader = (IEpicsReader) _context.getService(serviceReference);

        TcsContextFetcher fetcher = null;
        try {
            String tcsCtxChannel = _context.getProperty(TCS_CTX_CHANNEL_PROP);
            fetcher = new EpicsTcsContextFetcher(reader, tcsCtxChannel);
            _listener.registerTcsContextFetcher(fetcher);
            LOG.info("EPICS Connection established");
        } catch (TcsContextException e) {
            LOG.log(Level.WARNING, "Can't initialize EPICS channels", e);
        }

        return fetcher;

    }

    public void modifiedService(ServiceReference serviceReference, Object o) {

        TcsContextFetcher fetcher = (TcsContextFetcher) o;
        if (fetcher != null) {
            _listener.registerTcsContextFetcher(null);
            LOG.info("Removed old instance of EPICS writter");
        }

        IEpicsReader reader = (IEpicsReader) _context.getService(serviceReference);

        try {
            String tcsCtxChannel = _context.getProperty(TCS_CTX_CHANNEL_PROP);
            fetcher = new EpicsTcsContextFetcher(reader, tcsCtxChannel);

            _listener.registerTcsContextFetcher(fetcher);
            LOG.info("New instance of EPICS reader registered");
        } catch (TcsContextException ex) {
            LOG.log(Level.WARNING, "Can't initialize EPICS channels", ex);
        }
    }

    public void removedService(ServiceReference serviceReference, Object o) {

        _listener.registerTcsContextFetcher(null);

    }
}
