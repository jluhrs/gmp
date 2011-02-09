package edu.gemini.aspen.gmp.pcs.model;

import edu.gemini.aspen.gmp.pcs.jms.PcsUpdateListener;
import edu.gemini.aspen.gmp.pcs.model.updaters.EpicsPcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.updaters.LogPcsUpdater;
import edu.gemini.epics.IEpicsWriter;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface to define a composite of several PCS updater objects
 */
@Component
@Instantiate
@Provides(specifications = PcsUpdaterComposite.class)
public class PcsUpdaterCompositeImpl implements PcsUpdater, PcsUpdaterComposite {

    private static final Logger LOG = Logger.getLogger(PcsUpdaterComposite.class.getName());

    private static final String PCS_SIMULATION_PROP = "edu.gemini.aspen.gmp.pcs.simulation";
    private static final String PCS_CHANNEL_PROP = "edu.gemini.aspen.gmp.pcs.epicsChannel";

    private final List<PcsUpdater> _pcsUpdaters = new CopyOnWriteArrayList<PcsUpdater>();
    private BaseMessageConsumer _messageConsumer;

    @Property(name = "simulation", value = "yes", mandatory = true)
    private Boolean simulation;

    @Property(name = "epicsChannel", value = "NOVALID", mandatory = true)
    private String pcsChannel;

    @Requires
    private JmsProvider _provider;

    @Requires(id = "epicsWriter")
    private IEpicsWriter _epicsWriter;
    private PcsUpdater updater;

    /**
     * Initialize the composite.
     */
    public PcsUpdaterCompositeImpl() {

        //Creates the PCS Updates Consumer
        _messageConsumer = new BaseMessageConsumer(
                "JMS PCS Updates Consumer",
                new DestinationData(PcsUpdateListener.DESTINATION_NAME,
                        DestinationType.TOPIC),
                new PcsUpdateListener(this)
        );
        registerUpdater(new LogPcsUpdater());
    }

    @Override
    @Bind(aggregate = true)
    public void registerUpdater(PcsUpdater updater) {
        _pcsUpdaters.add(updater);
    }

    @Override
    @Unbind
    public void unregisterUpdater(PcsUpdater updater) {
        _pcsUpdaters.remove(updater);
    }

    @Bind(id = "epicsWriter")
    public void registerEpicsWriter() {
        if (!simulation) {
            try {
                updater = new EpicsPcsUpdater(_epicsWriter, pcsChannel);
                registerUpdater(updater);
                LOG.info("EPICS Connection established");
            } catch (PcsUpdaterException ex) {
                LOG.log(Level.WARNING, "Can't initialize EPICS channels", ex);
            }
        }
    }

    @Unbind(id = "epicsWriter")
    public void unRegisterEpicsWriter() {
        if (!simulation) {
            if (updater != null) {
                unregisterUpdater(updater);
                updater = null;
                LOG.info("Disconnected from EPICS");
            }
        }

    }

    @Modified(id = "epicsWriter")
    public void modifiedEpicsWriter() {
        if (!simulation) {
            if (updater != null) {
                unregisterUpdater(updater);
                LOG.info("Removed old instance of EPICS writter");
            }

            try {
                updater = new EpicsPcsUpdater(_epicsWriter, pcsChannel);
                registerUpdater(updater);
                LOG.info("New instance of EPICS writter registered");
            } catch (PcsUpdaterException ex) {
                LOG.log(Level.WARNING, "Can't initialize EPICS channels", ex);
            }
        }
    }

    @Validate
    public void initialize() throws JMSException {
        _messageConsumer = new BaseMessageConsumer(
                "JMS PCS Updates Consumer",
                new DestinationData(PcsUpdateListener.DESTINATION_NAME,
                        DestinationType.TOPIC),
                new PcsUpdateListener(this)
        );
        _messageConsumer.startJms(_provider);

        try {
            _messageConsumer.startJms(_provider);
        } catch (JMSException ex) {
            // LOG.log(Level.SEVERE,ex.getMessage(),ex);
        }
    }

    @Invalidate
    public void invalidate() {
        _messageConsumer.stopJms();
    }

    public void update(PcsUpdate update) throws PcsUpdaterException {
        for (PcsUpdater updater : _pcsUpdaters) {
            updater.update(update);
        }
    }
}
