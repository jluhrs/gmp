package edu.gemini.aspen.gmp.pcs.model;

import edu.gemini.aspen.gmp.pcs.jms.PcsUpdateListener;
import edu.gemini.aspen.gmp.pcs.model.updaters.LogPcsUpdater;
import edu.gemini.jms.api.*;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Instantiate
@Provides
public class PcsUpdaterCompositeImpl implements PcsUpdaterComposite, JmsArtifact {
    private static final Logger LOG = Logger.getLogger(PcsUpdaterComposite.class.getName());

    private final List<PcsUpdater> _pcsUpdaters = new CopyOnWriteArrayList<PcsUpdater>();
    private BaseMessageConsumer _messageConsumer;

    public PcsUpdaterCompositeImpl() {
        registerUpdater(new LogPcsUpdater());
    }

    /**
     * Register a new PcsUpdater in this aggregation
     *
     * @param updater the new updater in the agregation
     */
    @Override
    @Bind(aggregate = true)
    public void registerUpdater(PcsUpdater updater) {
        LOG.fine("Adding a registered PcsUpdater: " + updater);
        _pcsUpdaters.add(updater);
    }

    /**
     * Removes the given PcsUpdater from the aggregation
     *
     * @param updater updater to remove
     */
    @Override
    @Unbind
    public void unregisterUpdater(PcsUpdater updater) {
        LOG.fine("Adding an unregistered PcsUpdater: " + updater);
        _pcsUpdaters.remove(updater);
    }

    public void update(PcsUpdate update) throws PcsUpdaterException {
        for (PcsUpdater updater : _pcsUpdaters) {
            try {
                updater.update(update);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Exception updating a PcsUpdater", ex);
            }
        }
    }

    @Validate
    public void initialize() throws JMSException {
        // Required for iPojo
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        LOG.info("Start listening for JMS Messages on " + PcsUpdateListener.DESTINATION_NAME);
        //Creates the PCS Updates Consumer
        _messageConsumer = new BaseMessageConsumer(
                "JMS PCS Updates Consumer",
                new DestinationData(PcsUpdateListener.DESTINATION_NAME,
                        DestinationType.TOPIC),
                new PcsUpdateListener(PcsUpdaterCompositeImpl.this)
        );

        _messageConsumer.startJms(provider);
    }

    @Override
    public void stopJms() {
        _messageConsumer.stopJms();
    }
}