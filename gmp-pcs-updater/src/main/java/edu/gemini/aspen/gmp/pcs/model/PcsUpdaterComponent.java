package edu.gemini.aspen.gmp.pcs.model;

import edu.gemini.aspen.gmp.pcs.model.updaters.EpicsPcsUpdater;
import edu.gemini.epics.IEpicsWriter;
import org.apache.felix.ipojo.annotations.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface to define a composite of several PCS updater objects
 */
@Component(name = "pcsUpdater", managedservice = "edu.gemini.aspen.gmp.pcs.PcsUpdater")
@Instantiate(name = "pcsUpdater")
public class PcsUpdaterComponent {
    private static final Logger LOG = Logger.getLogger(PcsUpdaterComposite.class.getName());

    @Property(name = "simulation", value = "yes", mandatory = true)
    private Boolean simulation;

    @Property(name = "epicsChannel", value = "NOVALID", mandatory = true)
    private String pcsChannel;

    @Requires(id = "epicsWriter")
    private IEpicsWriter _epicsWriter;
    @Requires
    private PcsUpdaterComposite pcsUpdaterAggregate;

    private PcsUpdater updater;

    private PcsUpdaterComponent() {
    }

    protected PcsUpdaterComponent(IEpicsWriter epicsWriter, PcsUpdaterComposite updater, Boolean simulation, String pcsChannel) {
        this(epicsWriter, updater);
        this.simulation = simulation;
        this.pcsChannel = pcsChannel;
    }

    protected PcsUpdaterComponent(IEpicsWriter epicsWriter, PcsUpdaterComposite updater) {
        this._epicsWriter = epicsWriter;
        this.pcsUpdaterAggregate = updater;
    }

    @Bind(id = "epicsWriter")
    public void registerEpicsWriter() {
        if (!simulation) {
            try {
                updater = new EpicsPcsUpdater(_epicsWriter, pcsChannel);
                pcsUpdaterAggregate.registerUpdater(updater);
                LOG.info("EPICS Connection established");
            } catch (PcsUpdaterException ex) {
                LOG.log(Level.WARNING, "Can't initialize EPICS channels", ex);
            }
        }
    }

    @Unbind(id = "epicsWriter")
    public void unRegisterEpicsWriter() {
        if (!simulation && updater != null) {
            pcsUpdaterAggregate.unregisterUpdater(updater);
            updater = null;
            LOG.info("Disconnected from EPICS");
        }
    }

    @Modified(id = "epicsWriter")
    public void modifiedEpicsWriter() {
        if (!simulation) {
            if (updater != null) {
                pcsUpdaterAggregate.unregisterUpdater(updater);
                LOG.info("Removed old instance of EPICS writer");
            }

            try {
                updater = new EpicsPcsUpdater(_epicsWriter, pcsChannel);
                pcsUpdaterAggregate.registerUpdater(updater);
                LOG.info("New instance of EPICS writer registered");
            } catch (PcsUpdaterException ex) {
                LOG.log(Level.WARNING, "Can't initialize EPICS channels", ex);
            }
        }
    }

}
