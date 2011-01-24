package edu.gemini.aspen.gmp.pcs.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import edu.gemini.jms.api.osgi.JmsProviderTracker;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.aspen.gmp.pcs.jms.PcsUpdateListener;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.updaters.LogPcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.updaters.EpicsPcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterComposite;
import edu.gemini.epics.IEpicsWriter;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Activator class for the PCS Updater bundle
 */
public class Activator implements BundleActivator, ServiceTrackerCustomizer {

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());

    private static final String PCS_SIMULATION_PROP = "edu.gemini.aspen.gmp.pcs.simulation";
    private static final String PCS_CHANNEL_PROP = "edu.gemini.aspen.gmp.pcs.epicsChannel";

    private JmsProviderTracker _jmsTracker;

    private BaseMessageConsumer _messageConsumer;

    private ServiceTracker _epicsTracker = null;

    private BundleContext _context = null;

    /**
     * The composite of PcsUpdaters to be
     * invoked whenever an update for the PCS is received.
     */
    private PcsUpdaterComposite _pcsUpdaterComposite;

    public Activator() {

        _pcsUpdaterComposite = new PcsUpdaterComposite();
        _pcsUpdaterComposite.registerUpdater(new LogPcsUpdater());

        //Creates the PCS Updates Consumer
        _messageConsumer = new BaseMessageConsumer(
                "JMS PCS Updates Consumer",
                new DestinationData(PcsUpdateListener.DESTINATION_NAME,
                                    DestinationType.TOPIC),
                new PcsUpdateListener(_pcsUpdaterComposite)
        );
    }


    public void start(BundleContext bundleContext) throws Exception {

        _context = bundleContext;

        _jmsTracker = new JmsProviderTracker(bundleContext,
                _messageConsumer);
        _jmsTracker.open();

        String sim = _context.getProperty(PCS_SIMULATION_PROP);
        if (sim == null || "yes".equalsIgnoreCase(sim)) {
            LOG.info("PCS Updates in simulation mode. No updates to the PCS will be attempted");
        } else {
            //Start the EPICS tracker, so we can update the PCS via EPICS
            _epicsTracker = new ServiceTracker(bundleContext, IEpicsWriter.class.getName(), this);
            _epicsTracker.open();
        }

        LOG.info("PCS Updater bundle started");
    }

    public void stop(BundleContext bundleContext) throws Exception {

        if (_epicsTracker != null) {
            _epicsTracker.close();
            _epicsTracker = null;
        }

        _jmsTracker.close();
        _jmsTracker = null;
        
        LOG.info("PCS Updater bundle stopped");
    }

    public Object addingService(ServiceReference serviceReference) {
        
        IEpicsWriter writter = (IEpicsWriter)_context.getService(serviceReference);

        PcsUpdater updater = null;
        try {
            String pcschannel = _context.getProperty(PCS_CHANNEL_PROP);
            updater = new EpicsPcsUpdater(writter, pcschannel);
            _pcsUpdaterComposite.registerUpdater(updater);
            LOG.info("EPICS Connection established");
        } catch (PcsUpdaterException ex) {
            LOG.log(Level.WARNING, "Can't initialize EPICS channels", ex);
        }
        return updater;
    }

    public void modifiedService(ServiceReference serviceReference, Object o) {
        PcsUpdater updater = (PcsUpdater)o;
        if (updater != null) {
            _pcsUpdaterComposite.unregisterUpdater(updater);
            LOG.info("Removed old instance of EPICS writter");
        }

        IEpicsWriter writter = (IEpicsWriter)_context.getService(serviceReference);

        try {
            String pcschannel = _context.getProperty(PCS_CHANNEL_PROP);
            updater = new EpicsPcsUpdater(writter, pcschannel);
            _pcsUpdaterComposite.registerUpdater(updater);
            LOG.info("New instance of EPICS writter registered");
        } catch (PcsUpdaterException ex) {
            LOG.log(Level.WARNING, "Can't initialize EPICS channels", ex);
        }

    }

    public void removedService(ServiceReference serviceReference, Object o) {

        PcsUpdater updater = (PcsUpdater)o;
        if (updater != null) {
            _pcsUpdaterComposite.unregisterUpdater(updater);
            LOG.info("Disconnected from EPICS");
        }
        

    }
}
