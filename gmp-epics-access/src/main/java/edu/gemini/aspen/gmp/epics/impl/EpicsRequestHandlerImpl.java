package edu.gemini.aspen.gmp.epics.impl;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.gmp.epics.EpicsRequestHandler;
import edu.gemini.aspen.gmp.epics.jms.EpicsGetRequestConsumer;
import edu.gemini.epics.EpicsReader;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.logging.Logger;

/**
 * This class monitors the EPICS channels acting as an EpicsClient.
 * The updates are delegated for further processing to an EpicsRegistrar
 * <br>
 * It allows to register <code>EpicsUpdateListener</code> objects, which
 * will be invoked whenever an update to the monitored EPICS channel is
 * received.
 */
public class EpicsRequestHandlerImpl implements EpicsRequestHandler, JmsArtifact {
    private static final Logger LOG = Logger.getLogger(EpicsRequestHandlerImpl.class.getName());
    private final EpicsReader epicsReader;

    private EpicsGetRequestConsumer _epicsRequestConsumer;

    public EpicsRequestHandlerImpl(EpicsReader epicsReader) {
        Preconditions.checkArgument(epicsReader != null, "Cannot create an EpicsRequestHandlerImpl with a null context");
        this.epicsReader = epicsReader;
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        LOG.info("JMS Provider found. Starting EpicsRequestHandlerbundle");
        _epicsRequestConsumer = new EpicsGetRequestConsumer(provider, epicsReader);
    }

    @Override
    public void stopJms() {
        _epicsRequestConsumer.close();
    }
}
