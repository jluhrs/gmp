package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.statusservice.jms.JmsStatusListener;
import edu.gemini.aspen.giapi.statusservice.jms.StatusConsumer;
import edu.gemini.jms.api.BaseJmsArtifact;

/**
 * The main class for the status service.
 */
public class StatusService {

    private final StatusHandlerManager _manager;

    private final StatusConsumer _consumer;

    private static final String DEFAULT_STATUS = ">"; //defaults to listen for all the status items.


    public StatusService(String serviceName, String statusName) {

        _manager = new StatusHandlerManager();

        if (statusName == null) {
            statusName = DEFAULT_STATUS;
        }

        if (serviceName == null) {
            serviceName = _manager.getName();
        }

        _consumer = new StatusConsumer(serviceName, statusName);
        _consumer.setMessageListener(new JmsStatusListener(_manager));

    }

    public StatusHandlerRegister getStatusHandlerRegister() {
        return _manager;
    }

    public BaseJmsArtifact getJmsArtifact() {
        return _consumer;
    }

    public void removeRegisteredHandlers() {
        _manager.removeAllHandlers();
    }

}
