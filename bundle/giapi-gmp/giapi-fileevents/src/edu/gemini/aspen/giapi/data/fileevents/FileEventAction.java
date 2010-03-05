package edu.gemini.aspen.giapi.data.fileevents;

import edu.gemini.aspen.gmp.data.Dataset;
import edu.gemini.aspen.gmp.data.AncillaryFileEventHandler;
import edu.gemini.aspen.gmp.data.IntermediateFileEventHandler;

import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.*;

/**
 * The file event action allows processing file events in separate threads.
 * The file event action implements a composite pattern for both the
 * intermediate file event handlers and the ancillary file event handlers.
 */
public class FileEventAction implements FileEventHandlerComposite {

    private final static Logger LOG = Logger.getLogger(FileEventAction.class.getName());

    private List<IntermediateFileEventHandler> _intermediateFileHandlers;

    private List<AncillaryFileEventHandler> _ancillaryFileHandlers;

    //we use a cached thread pool, since the expectation is to
    //have several short-lived processes whenever the file events arrive.
    private final ExecutorService _threadPool = Executors.newCachedThreadPool();

    public FileEventAction() {
        _intermediateFileHandlers = new CopyOnWriteArrayList<IntermediateFileEventHandler>();
        _ancillaryFileHandlers = new CopyOnWriteArrayList<AncillaryFileEventHandler>();
    }


    public void onAncillaryFileEvent(final String filename, final Dataset dataset) {

        for (final AncillaryFileEventHandler handler: _ancillaryFileHandlers) {
            _threadPool.submit(new Runnable() {
                public void run() {
                    handler.onAncillaryFileEvent(filename, dataset);
                }
            });
        }
    }

    public void onIntermediateFileEvent(final String filename, final Dataset dataset, final String hint) {
        for (final IntermediateFileEventHandler handler: _intermediateFileHandlers) {
            _threadPool.submit(new Runnable() {
                public void run() {
                    handler.onIntermediateFileEvent(filename, dataset, hint);
                }
            });
        }
    }

    public void removeIntermediateFileEventHandler(IntermediateFileEventHandler handler) {
        _intermediateFileHandlers.remove(handler);
    }

    public void addIntermediateFileEventHandler(IntermediateFileEventHandler handler) {
        _intermediateFileHandlers.add(handler);
    }

    public void removeAncillaryFileEventHandler(AncillaryFileEventHandler handler) {
        _ancillaryFileHandlers.remove(handler);
    }

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
