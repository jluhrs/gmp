package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.statusservice.jms.JmsStatusListener;
import edu.gemini.aspen.giapi.statusservice.jms.StatusConsumer;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;

/**
 * The main class for the status service.
 */
public class StatusService implements JmsArtifact, StatusHandlerRegister {

    private final StatusHandlerManager _manager;

    private final StatusConsumer _consumer;

    private static final String DEFAULT_STATUS = ">"; //defaults to listen for all the status items.

    private static final String DEFAULT_NAME = "Status Service";


    public StatusService(String serviceName, String statusName) {

        if (statusName == null) {
            statusName = DEFAULT_STATUS;
        }

        if (serviceName == null) {
            serviceName = DEFAULT_NAME;
        }

        _manager = new StatusHandlerManager();

        _consumer = new StatusConsumer(serviceName, statusName);
        _consumer.setMessageListener(new JmsStatusListener(_manager));
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        _consumer.startJms(provider);
    }

    @Override
    public void stopJms() {
        _consumer.stopJms();
    }

    @Override
    public void addStatusHandler(StatusHandler handler) {
        _manager.addStatusHandler(handler);
    }

    @Override
    public void removeStatusHandler(StatusHandler handler) {
        _manager.removeStatusHandler(handler);
    }
}
