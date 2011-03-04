package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
import org.apache.felix.ipojo.annotations.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * The Status Handler Aggregate acts as a register for status handlers
 * and also as a receiver of updates when status items are updated. It will
 * notify the Status Handlers registered whenever a new status item is
 * received. It assumes all the status handlers registered are interested
 * in all the status items for which the Status Service is registered.
 * Filtering/processing of status item is delegated to the status handlers
 * themselves.
 *
 * The iPOJO framework notifies the instance whenever new StatusHandler appear
 * and disappear.
 */
@Component
@Instantiate(name = "statusHandlerManager")
@Provides(specifications = StatusHandlerAggregate.class)
public class StatusHandlerAggregateImpl implements StatusHandlerAggregate {
    private static final Logger LOG = Logger.getLogger(StatusHandlerAggregate.class.getName());
    private static final String STATUS_HANDLER_NAME = "Status Handler Manager";
    
    private final List<StatusHandler> _statusHandlers = new CopyOnWriteArrayList<StatusHandler>();

    @Override
    public String getName() {
        return STATUS_HANDLER_NAME;
    }

    @Override
    public <T> void update(StatusItem<T> item) {
        for (StatusHandler handler: _statusHandlers) {
            handler.update(item);
        }
    }

    @Override
    @Bind(aggregate = true)
    public void bindStatusHandler(StatusHandler handler) {
        _statusHandlers.add(handler);
        LOG.info("Status Handler Registered: " + handler);
    }

    @Override
    @Unbind
    public void unbindStatusHandler(StatusHandler handler) {
        _statusHandlers.remove(handler);
        LOG.info("Removed Status Handler: " + handler);
    }
}
