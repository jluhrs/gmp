package edu.gemini.aspen.gmp.pcs.model;

import edu.gemini.aspen.gmp.pcs.jms.PcsUpdateListener;
import edu.gemini.aspen.gmp.pcs.model.updaters.EpicsPcsUpdater;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.ChannelFactory;
import edu.gemini.epics.EpicsWriter;
import edu.gemini.jms.api.*;
import org.apache.felix.ipojo.Nullable;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface to define a composite of several PCS updater objects
 */
@Component
@Provides
public class PcsUpdaterComponent implements PcsUpdater, JmsArtifact {
    private static final Logger LOG = Logger.getLogger(PcsUpdaterComponent.class.getName());

    private Boolean simulation;

    private String pcsChannel;

    private final ChannelAccessServer _channelFactory;

    private PcsUpdater updater;

    private BaseMessageConsumer _messageConsumer;

    public PcsUpdaterComponent(@Requires ChannelAccessServer channelFactory,
                                  @Property(name = "simulation", value = "yes", mandatory = true) Boolean simulation,
                                  @Property(name = "epicsChannel", value = "NOVALID", mandatory = true) String pcsChannel) {
        this.simulation = simulation;
        this.pcsChannel = pcsChannel;
        _channelFactory = channelFactory;
    }

    @Validate
    public void startComponent() {
        LOG.info("Start PCS Updater Component");
        if (!simulation) {
            try {
                updater = new EpicsPcsUpdater(_channelFactory, pcsChannel);
                LOG.info("EPICS Connection established");
            } catch (PcsUpdaterException ex) {
                LOG.log(Level.WARNING, "Can't initialize EPICS channels", ex);
            }
        }
    }

    @Invalidate
    public void stopComponent() {
        LOG.info("Stop PCS Updater Component");
        if (!simulation && updater != null) {
            // TODO kill updater
            updater = null;
            LOG.info("Disconnected from EPICS");
        }
    }

    @Updated
    public void modifiedEpicsWriter() {
        LOG.info("Modify PCS Updater Component");
        /*if (!simulation) {
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
        }*/
    }

    public void update(PcsUpdate update) throws PcsUpdaterException {
        LOG.info("PCS update " + Arrays.toString(update.getZernikes()));
        try {
            updater.update(update);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Exception updating a PcsUpdater", ex);
        }
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        LOG.info("Start listening for JMS Messages on " + PcsUpdateListener.DESTINATION_NAME);
        //Creates the PCS Updates Consumer
        _messageConsumer = new BaseMessageConsumer(
                "JMS PCS Updates Consumer",
                new DestinationData(PcsUpdateListener.DESTINATION_NAME,
                        DestinationType.TOPIC),
                new PcsUpdateListener(PcsUpdaterComponent.this)
        );

        _messageConsumer.startJms(provider);
    }

    @Override
    public void stopJms() {
        _messageConsumer.stopJms();
    }

    @Override
    public String toString() {
        return "PCS Updater";
    }


}
