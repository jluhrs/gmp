package edu.gemini.aspen.gmp.tcs.model;

import edu.gemini.aspen.gmp.tcs.jms.JmsTcsContextDispatcher;
import edu.gemini.aspen.gmp.tcs.jms.TcsContextRequestListener;
import edu.gemini.epics.EpicsReader;
import edu.gemini.jms.api.*;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface to define a composite of several TCS Context objects
 */
@Component
@Provides
public class TcsContextComponent implements JmsArtifact {
    private static final Logger LOG = Logger.getLogger(TcsContextComponent.class.getName());

    private final Boolean simulation;

    private final String tcsChannel;

    private final String simulationData;

    /**
     * The JMS Context Dispatcher is a JMS Producer message
     * that will send the TCS Context to the requester
     */
    private JmsTcsContextDispatcher _dispatcher;

    /**
     * JMS Listener to process the TCS Context requests.
     */
    private TcsContextRequestListener _listener;

    /**
     * Message consumer used to receive TCS Context requests
     */
    private BaseMessageConsumer _messageConsumer;

    private final EpicsReader _epicsReader;

    private TcsContextFetcher fetcher;

    protected TcsContextComponent(@Requires EpicsReader reader,
            @Property(name = "tcsChannel", value = "NOVALID", mandatory = true) String tcsChannel,
            @Property(name = "simulation", value = "true", mandatory = true) String simulation,
            @Property(name = "simulationData", value = "NOVALID", mandatory = true) String simulationData) {
        this._epicsReader = reader;
        this.tcsChannel = tcsChannel;
        this.simulation = Boolean.parseBoolean(simulation);
        this.simulationData = simulationData;

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

    @Validate
    public void validated() throws JMSException {
        if (!simulation) {
            addNewTcsContextFetcher();
        } else {
            LOG.info("TCS in simulation mode");
        }
    }

    private void addSimulatedTcsContextFetcher() {
        try {
            LOG.info("Simulating data for TCS Context from " + simulationData);
            _listener.registerTcsContextFetcher(new SimTcsContextFetcher(new FileInputStream(simulationData)));
        } catch (FileNotFoundException e) {
            LOG.log(Level.SEVERE, "Simulation file not found " + simulationData, e);
        }
    }


    private void addNewTcsContextFetcher() {
        try {
            LOG.info("New instance of EPICS reader registered, get tcsContext from " + tcsChannel);
            fetcher = new EpicsTcsContextFetcher(_epicsReader, tcsChannel);
            _listener.registerTcsContextFetcher(fetcher);
        } catch (TcsContextException e) {
            LOG.log(Level.WARNING, "Can't initialize EPICS channels", e);
        }
    }

    @Invalidate
    public void unRegisterEpicsReader() {
        if (!simulation) {
            removeOldTcsContextFetcher();
        }
    }

    private void removeOldTcsContextFetcher() {
        if (fetcher != null) {
            LOG.info("Removed old instance of EPICS writer");
            _listener.registerTcsContextFetcher(null);
        }
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        LOG.fine("TCS Context validated, starting... ");
        _dispatcher.startJms(provider);
        _messageConsumer.startJms(provider);
        if (simulation) {
            addSimulatedTcsContextFetcher();
        }
        LOG.fine("TCS Context Service started");
    }

    @Override
    public void stopJms() {
        LOG.fine("TCS Context stopped, disconnecting jms... ");
        _dispatcher.stopJms();
        _messageConsumer.stopJms();
        LOG.fine("TCS Context Service Stopped");
    }
}
