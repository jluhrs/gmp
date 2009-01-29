package edu.gemini.aspen.gmp.statusdb;

import edu.gemini.aspen.gmp.status.api.StatusItem;
import edu.gemini.aspen.gmp.status.api.StatusProcessor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.List;

/**
 * This runnable task receives new status items and is in
 * charge of dispatching them to the different status processors
 * registered with it.
 */
public class StatusConsumer implements Runnable {

    private static final Logger LOG = Logger.getLogger(StatusConsumer.class.getName());

    /**
     * Blocking Queue to hold the status items to be processed
     */
    private final BlockingQueue<StatusItem> _itemQueue =
            new LinkedBlockingQueue<StatusItem>();

    /**
     * List of status item processors registered. The processors
     * registered in this list will get invoked with each new
     * status item received.
     */
    private final List<StatusProcessor> _processors =
            new CopyOnWriteArrayList<StatusProcessor>();


    private volatile boolean _isRunning = true;

    public void run() {

        while (isRunning()) {

            try {
                StatusItem item = _itemQueue.take();
                //update all the handlers
                for (StatusProcessor processor : _processors) {
                    processor.process(item);
                }

            } catch (InterruptedException e) {
                LOG.info("Status Consumer Thread Interrupted. Exiting thread");
                return;
            }

        }
    }


    /**
     * Put a new status item in the queue to get processed by the working thread
     *
     * @param item the status item to be processed
     */
    void putStatusItem(StatusItem item) {
        if (!_itemQueue.offer(item)) {
            LOG.warning("Missed item. Queue is full. We are receiving more items than we can handle");
        }
    }

    /**
     * Registers a new status processor in the consumer thread
     *
     * @param processor the status processor to be registered
     */
    void registerStatusProcessor(StatusProcessor processor) {
        _processors.add(processor);
    }

    /**
     * Removes the specified status processor from the list of
     * internal status processors this consumer will update
     * each time a new status item is received.
     *
     * @param processor the status processor to remove
     */
    void unregisterStatusProcessor(StatusProcessor processor) {
        _processors.remove(processor);
    }


    private boolean isRunning() {
        return _isRunning;
    }

    /**
     * Stop the consumer. 
     */
    void stop() {
        _isRunning = false;
    }
}
