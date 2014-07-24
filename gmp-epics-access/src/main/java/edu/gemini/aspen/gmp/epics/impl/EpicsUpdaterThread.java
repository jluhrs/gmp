package edu.gemini.aspen.gmp.epics.impl;

import edu.gemini.aspen.gmp.epics.EpicsRegistrar;
import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * This implements the EpicsRegistrar interface. Its job is
 * to receive Epics Updates, and then process them via the
 * registered listeners using a separate thread
 */
public class EpicsUpdaterThread implements EpicsRegistrar {
    private final static Logger LOG = Logger.getLogger(EpicsUpdaterThread.class.getName());

    private final BlockingQueue<EpicsUpdate<?>> _updateQueue =
            new LinkedBlockingQueue<EpicsUpdate<?>>();

    private final Map<String, EpicsUpdateListener> _updatersMap = new HashMap<String, EpicsUpdateListener>();

    /**
     * The executor service provides a separate thread for the Updater thread
     * to run
     */
    private final ExecutorService _executorService =
            Executors.newSingleThreadExecutor();

    /**
     * The update task is responsible for receiving updates through
     * the update queue and notify the clients waiting in the action queue
     */
    private final UpdaterTask _updaterTask = new UpdaterTask();

    public void unregisterInterest(String channel) {
        _updatersMap.remove(channel);
    }

    public void registerInterest(String channel, EpicsUpdateListener updater) {
        _updatersMap.put(channel, updater);
    }

    public synchronized void processEpicsUpdate(EpicsUpdate<?> update) {
        //put the update on the queue, and keep waiting for more updates
        try {
            _updateQueue.put(update);
        } catch (InterruptedException e) {
            //nothing to do. The operation was interrupted
        }
    }

    /**
     * This is the runnable task that will get the epics updates from
     * the queue and will invoke any registered listeners.
     */
    private class UpdaterTask implements Runnable {
        private boolean isRunning;

        public void run() {
            isRunning = true;
            while (isRunning()) {
                try {
                    EpicsUpdate<?> update = _updateQueue.take();

                    if (update != null) {
                        //notify the updaters
                        EpicsUpdateListener listener = _updatersMap.get(update.getChannelName());
                        //If there is any interested listeners, will notify them.
                        //will filter null updates. They can occur if the EPICS communication
                        //is lost for some reason.
                        List<?> value = update.getChannelData();
                        if (listener != null && value != null && !value.isEmpty()) {
                            listener.onEpicsUpdate(update);
                        }
                    }
                } catch (InterruptedException e) {
                    LOG.info("Updater Task Thread interrupted. Exiting");
                    isRunning = false;
                    return;
                }
            }
        }

        private synchronized boolean isRunning() {

            return isRunning;

        }

        /**
         * Stop the current processing thread.
         */
        public synchronized void stop() {
            isRunning = false;
        }
    }

    /**
     * Start up a background thread
     */
    public void start() {
        //Submit the processor task for execution in a separate thread
        _executorService.submit(_updaterTask);
    }

    /*
     ** Stop the processing thread.
    */
    public void stop() {
        _updaterTask.stop();
        _executorService.shutdown();
        try {
            if (!_executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                _executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            _executorService.shutdownNow();
        }
    }

}
