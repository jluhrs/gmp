package edu.gemini.aspen.gmp.statusservice.core;

import edu.gemini.aspen.gmp.status.api.StatusHandler;
import edu.gemini.aspen.gmp.status.api.StatusItem;

import java.util.logging.Logger;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Status Service main bundle class. Acts as a register for status handlers
 * and also as a receiver of updates when status items are updated. It will
 * notify the Status Hanlders registered in such a case.
 */
public class StatusService implements StatusUpdater, StatusHandlerRegister {

    private static final Logger LOG = Logger.getLogger(StatusService.class.getName());

    private List<StatusHandler> _statusHandlers = new CopyOnWriteArrayList<StatusHandler>();

    public StatusService() {

    }

    public void update(StatusItem item) {
        for (StatusHandler handler: _statusHandlers) {
            handler.update(item);
        }
    }

    public void start() {
        LOG.info("Starting Status Service");
    }

    public void shutdown() {
        LOG.info("Shutting down Status Service");
        _removeAllHandlers();
    }

    public void addStatusHandler(StatusHandler handler) {
        LOG.info("Adding status handler to status service");
        _statusHandlers.add(handler);
    }

    public void removeStatusHandler(StatusHandler handler) {
        LOG.info("Removing status handler from status service");
        _statusHandlers.remove(handler);
    }


    private void _removeAllHandlers() {
        _statusHandlers.clear();
    }
}
