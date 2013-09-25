package edu.gemini.aspen.gmp.pcs.model;

import com.google.common.collect.Iterators;
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
import java.util.Dictionary;
import java.util.Enumeration;
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

    private EpicsPcsUpdater updater;
    private BaseMessageConsumer _messageConsumer;

    private PcsUpdaterComponent(@Requires ChannelAccessServer channelFactory,
                               @Property(name = "simulation", value = "yes", mandatory = true) String simulation, // Simulation must be a string to be compatible with iPojo
                               @Property(name = "epicsChannel", value = "NOVALID", mandatory = true) String pcsChannel) {
        this(channelFactory, Boolean.parseBoolean(simulation), pcsChannel);
    }

    public PcsUpdaterComponent(ChannelAccessServer channelFactory,
                               boolean simulation,
                               String pcsChannel) {
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
            updater.stopChannel();
            updater = null;
        }
    }

    @Updated
    public void updatedComponent(Dictionary<String, String> conf) {
        this.simulation = Boolean.parseBoolean(conf.get("simulation"));
        LOG.info("Modify PCS Updater Component simulation=" + simulation + " channelName=" + pcsChannel);
        if (simulation && updater != null) {
            updater.stopChannel();
            updater = null;
        }
        if (!simulation) {
            if (updater != null) {
                updater.stopChannel();
                updater = null;
            }

            try {
                updater = new EpicsPcsUpdater(_channelFactory, pcsChannel);
                LOG.info("New instance of EPICS writer registered");
            } catch (PcsUpdaterException ex) {
                LOG.log(Level.WARNING, "Can't initialize EPICS channels", ex);
            }
        }
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
