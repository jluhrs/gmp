package edu.gemini.aspen.gmp.pcs.model;

import edu.gemini.aspen.gmp.pcs.jms.PcsUpdateListener;
import edu.gemini.aspen.gmp.pcs.model.updaters.LogPcsUpdater;
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

@Component
@Instantiate
public class PcsUpdaterAggregate implements PcsUpdater, PcsUpdaterComposite {
    private static final Logger LOG = Logger.getLogger(PcsUpdaterComposite.class.getName());
    private final List<PcsUpdater> _pcsUpdaters = new CopyOnWriteArrayList<PcsUpdater>();

    private BaseMessageConsumer _messageConsumer;

    @Requires
    private JmsProvider _provider;

    public PcsUpdaterAggregate() {
        registerUpdater(new LogPcsUpdater());
    }

    @Override
    @Bind(aggregate = true)
    public void registerUpdater(PcsUpdater updater) {
        LOG.fine("Adding a registered PcsUpdater: " + updater);
        _pcsUpdaters.add(updater);
    }

    @Override
    @Unbind
    public void unregisterUpdater(PcsUpdater updater) {
        LOG.fine("Adding an unregistered PcsUpdater: " + updater);
        _pcsUpdaters.remove(updater);
    }

    public void update(PcsUpdate update) throws PcsUpdaterException {
        for (PcsUpdater updater : _pcsUpdaters) {
            updater.update(update);
        }
    }

    @Validate
    public void initialize() throws JMSException {
        //Creates the PCS Updates Consumer
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
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @Invalidate
    public void invalidate() {
        _messageConsumer.stopJms();
    }
}