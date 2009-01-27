package edu.gemini.aspen.gmp.statusdb;

import edu.gemini.aspen.gmp.status.api.StatusHandler;
import edu.gemini.aspen.gmp.status.api.StatusItem;
import edu.gemini.aspen.gmp.status.api.StatusProcessor;

import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The Status Database contains the most up to date information related to
 * the status items. All the status items received by the GMP are recorded here
 * and then dispatched for further processing in a separate thread.
 */
public class StatusDatabase implements StatusHandler {

    private static final Logger LOG = Logger.getLogger(StatusDatabase.class.getName());

    //Store the most recent information associatied to all the status items
    final private ConcurrentHashMap<String, StatusItem> _db
            = new ConcurrentHashMap<String, StatusItem>();

    final private StatusConsumer _statusConsumer;

    /**
       * The executor service provides a separate thread for the StatusConsumer
       * to run
       */
    private final ExecutorService _executorService;
    
    public StatusDatabase() {
        _statusConsumer = new StatusConsumer();
        _executorService = Executors.newSingleThreadExecutor();
    }

    public void registerStatusProcessor(StatusProcessor processor) {
        _statusConsumer.registerStatusProcessor(processor);
    }

    public void unregisterStatusProcessor(StatusProcessor processor) {
        _statusConsumer.unregisterStatusProcessor(processor);
    }

    public String getName() {
        return "Status Database";  
    }


    public StatusItem getValue(String name) {
        return _db.get(name);
    }

    public void update(StatusItem item) {
        //store this new value in the database
        _db.put(item.getName(), item);
        //now tell the consumer thread to process
        //this item
        _statusConsumer.putStatusItem(item);
    }

    public void start() {
        LOG.info("Starting up Status Database");
         //Start a new thread to execute the status consumer task
        _executorService.submit(_statusConsumer);
    }

    public void shutdown() {
        LOG.info("Shutting down Status Database");

        _statusConsumer.stop();
        _executorService.shutdown();
        try {
            if (!_executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                _executorService.shutdownNow();   
            }
        } catch (InterruptedException e) {
            _executorService.shutdownNow();
        }
    }


    @Override
    public String toString() {
        return getName();
    }
}
