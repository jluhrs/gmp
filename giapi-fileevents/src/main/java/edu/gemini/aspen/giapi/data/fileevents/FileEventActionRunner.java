package edu.gemini.aspen.giapi.data.fileevents;

import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.AncillaryFileEventHandler;
import edu.gemini.aspen.giapi.data.IntermediateFileEventHandler;

import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.*;

/**
 * This file event action allows processing file events in separate threads.
 * This class holds a list of ancillary file event handlers and
 * a list of intermediate file event handlers.
 */
public class FileEventActionRunner implements FileEventAction {

    private final static Logger LOG = Logger.getLogger(FileEventActionRunner.class.getName());

    private List<IntermediateFileEventHandler> _intermediateFileHandlers;

    private List<AncillaryFileEventHandler> _ancillaryFileHandlers;

    //we use a cached thread pool, since the expectation is to
    //have several short-lived processes whenever the file events arrive.
    private final ExecutorService _threadPool = Executors.newCachedThreadPool();

    public FileEventActionRunner() {
        _intermediateFileHandlers = new CopyOnWriteArrayList<IntermediateFileEventHandler>();
        _ancillaryFileHandlers = new CopyOnWriteArrayList<AncillaryFileEventHandler>();
    }


    public void onAncillaryFileEvent(final String filename, final DataLabel dataLabel) {

        /**
         * For each handler available, invoke it in a separate thread
         */
        for (final AncillaryFileEventHandler handler: _ancillaryFileHandlers) {
            _threadPool.submit(new Runnable() {
                public void run() {
                    handler.onAncillaryFileEvent(filename, dataLabel);
                }
            });
        }
    }

    public void onIntermediateFileEvent(final String filename, final DataLabel dataLabel, final String hint) {

        /**
         * For each handler available, invoke it in a separate thread
         */
        for (final IntermediateFileEventHandler handler: _intermediateFileHandlers) {
            _threadPool.submit(new Runnable() {
                public void run() {
                    handler.onIntermediateFileEvent(filename, dataLabel, hint);
                }
            });
        }
    }

    /**
     * Remove the intermediate file handler from this action
     * @param handler the handler to remove
     */
    public void removeIntermediateFileEventHandler(IntermediateFileEventHandler handler) {
        _intermediateFileHandlers.remove(handler);
    }

    /**
     * Add the intermediate file handler to this action
     * @param handler the handler to add
     */
    public void addIntermediateFileEventHandler(IntermediateFileEventHandler handler) {
        _intermediateFileHandlers.add(handler);
    }


    /**
     * Remove the intermediate ancillary file handler from this action
     * @param handler the handler to remove
     */
    public void removeAncillaryFileEventHandler(AncillaryFileEventHandler handler) {
        _ancillaryFileHandlers.remove(handler);
    }

    /**
     * Add the ancillary file handler to this action. 
     * @param handler the handler to add
     */
    public void addAncillaryFileEventHandler(AncillaryFileEventHandler handler) {
        _ancillaryFileHandlers.add(handler);
    }

    /**
     * Shutdown (as clean as possible) the thread pool.
     */
    public void shutdown() {
        _threadPool.shutdown();
        try {
            //wait for the existing tasks to terminate
            if (!_threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                _threadPool.shutdownNow();
                //wait a while for tasks to respond to being cancelled
                if (!_threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    LOG.warning("Thread pool couldn't terminate");
                }
            }
        } catch (InterruptedException e) {
            //(re-)cancel if current thread is also interrupted
            _threadPool.shutdownNow();
            //preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
