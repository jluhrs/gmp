package edu.gemini.aspen.gmp.pcs.model;

import edu.gemini.aspen.gmp.pcs.model.updaters.EpicsPcsUpdater;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelFactory;
import edu.gemini.epics.EpicsWriter;
import org.apache.felix.ipojo.annotations.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface to define a composite of several PCS updater objects
 */
@Component
public class PcsUpdaterComponent {
    private static final Logger LOG = Logger.getLogger(PcsUpdaterComposite.class.getName());

    private Boolean simulation;

    private String pcsChannel;

    private final PcsUpdaterComposite pcsUpdaterAggregate;
    private final ChannelAccessServer _channelFactory;

    private PcsUpdater updater;

    public PcsUpdaterComponent(@Requires ChannelAccessServer channelFactory,
                                  @Requires PcsUpdaterComposite updater,
                                  @Property(name = "simulation", value = "yes", mandatory = true) Boolean simulation,
                                  @Property(name = "epicsChannel", value = "NOVALID", mandatory = true) String pcsChannel) {
        this.pcsUpdaterAggregate = updater;
        this.simulation = simulation;
        this.pcsChannel = pcsChannel;
        _channelFactory = channelFactory;
    }

    @Validate
    public void startComponent() {
        if (!simulation) {
            try {
                updater = new EpicsPcsUpdater(_channelFactory, pcsChannel);
                pcsUpdaterAggregate.registerUpdater(updater);
                LOG.info("EPICS Connection established");
            } catch (PcsUpdaterException ex) {
                LOG.log(Level.WARNING, "Can't initialize EPICS channels", ex);
            }
        }
    }

    @Invalidate
    public void stopComponent() {
        if (!simulation && updater != null) {
            pcsUpdaterAggregate.unregisterUpdater(updater);
            updater = null;
            LOG.info("Disconnected from EPICS");
        }
    }

    @Updated
    public void modifiedEpicsWriter() {
        if (!simulation) {
            if (updater != null) {
                pcsUpdaterAggregate.unregisterUpdater(updater);
                LOG.info("Removed old instance of EPICS writer");
            }

            try {
                updater = new EpicsPcsUpdater(_channelFactory, pcsChannel);
                pcsUpdaterAggregate.registerUpdater(updater);
                LOG.info("New instance of EPICS writer registered");
            } catch (PcsUpdaterException ex) {
                LOG.log(Level.WARNING, "Can't initialize EPICS channels", ex);
            }
        }
    }

}
