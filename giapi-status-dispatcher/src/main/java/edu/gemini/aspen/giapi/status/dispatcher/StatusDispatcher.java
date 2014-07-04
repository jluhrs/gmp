package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Status Dispatcher provides mechanisms for registering handlers with
 * specific status items. This way, client code can register one specific
 * Status Handler to be invoked whenever a change to a particular Status
 * Item occurs.
 * <br>
 * The StatusDispatcher will listen for all the Status Items that arrive over
 * the network (it does this as a client of the GIAPI Status Service).
 * <br>
 * The Status Dispatcher will map status items names to specific handlers to
 * be invoked. It will provide mechanisms for client code to register these
 * handlers and associate them with particular status items.
 */
public class StatusDispatcher implements StatusHandler {

    private final static Logger LOG = Logger.getLogger(StatusDispatcher.class.getName());

    private final List<FilteredStatusHandler> _handlers = new ArrayList<FilteredStatusHandler>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    @Override
    public String getName() {
        return StatusDispatcher.class.getName();
    }

    @Override
    public <T> void update(StatusItem<T> item) {
        for (FilteredStatusHandler handler : _handlers) {
            if (handler.getFilter().match(item)) {
                try {
                    handler.update(item);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Exception updating a FilteredStatusHandler", ex);
                }
            }
        }
    }

    public void bindStatusHandler(FilteredStatusHandler handler) {
        lock.writeLock().lock();
        _handlers.add(handler);
        lock.writeLock().unlock();
        LOG.info("Status Handler Registered at Dispatcher: " + handler);
    }

    public void unbindStatusHandler(FilteredStatusHandler handler) {
        lock.writeLock().lock();
        _handlers.remove(handler);
        lock.writeLock().unlock();
        LOG.info("Removed Status Handler from Dispatcher: " + handler);
    }

    @Override
    public String toString() {
        return "StatusDispatcher";
    }
}
