package edu.gemini.aspen.gmp.pcs.model;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.gemini.aspen.gmp.pcs.jms.PcsUpdateListener;
import edu.gemini.aspen.gmp.pcs.model.updaters.EpicsPcsUpdater;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.jms.api.*;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface to define a composite of several PCS updater objects
 */
@Component
@Provides
public class PcsUpdaterComponent implements PcsUpdater, JmsArtifact {
    private static final Logger LOG = Logger.getLogger(PcsUpdaterComponent.class.getName());

    private List<Double> gains;
    private Boolean simulation;
    private String pcsChannel;

    private final ChannelAccessServer _channelFactory;

    private EpicsPcsUpdater updater;
    private BaseMessageConsumer _messageConsumer;

    private PcsUpdaterComponent(@Requires ChannelAccessServer channelFactory,
                                @Property(name = "simulation", value = "yes", mandatory = true) String simulation, // Simulation must be a string to be compatible with iPojo
                                @Property(name = "epicsChannel", value = "NOVALID", mandatory = true) String pcsChannel,
                                @Property(name = "gains", value = "NOVALID", mandatory = true) String gains) {
        this(channelFactory, Boolean.parseBoolean(simulation), pcsChannel, gains);
    }

    public PcsUpdaterComponent(ChannelAccessServer channelFactory,
                               boolean simulation,
                               String pcsChannel,
                               String gains) {
        this.simulation = simulation;
        this.pcsChannel = pcsChannel;
        this.gains = Lists.transform(ImmutableList.copyOf(gains.split(" ")), new Function<String, Double>() {
            @Override
            public Double apply(String input) {
                return Double.parseDouble(input);
            }
        });
        _channelFactory = channelFactory;
    }

    @Validate
    public void startComponent() {
        LOG.info("Start PCS Updater Component");
        if (!simulation) {
            try {
                updater = new EpicsPcsUpdater(_channelFactory, pcsChannel, gains);
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
        this.pcsChannel = conf.get("epicsChannel");
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
                updater = new EpicsPcsUpdater(_channelFactory, pcsChannel, gains);
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
