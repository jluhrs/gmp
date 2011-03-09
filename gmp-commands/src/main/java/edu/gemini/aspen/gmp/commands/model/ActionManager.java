package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class keeps track of the actions that are being sent to the instruments
 * and notifies back the clients with completion information whenever that is
 * available.
 */
public class ActionManager {

    private static final Logger LOG = Logger.getLogger(
            ActionManager.class.getName());

    /**
     * The Action Queue stores the actions initiated when a sequence command
     * is received from the OCS and dispatched to the instrument
     */
    private final Queue<Action> _actionQueue =
            new ConcurrentLinkedQueue<Action>();

    /**
     * The Update Queue stores the completion information updates that come
     * from the instrument code, and is used to notify back to the clients that
     * are awaiting for completion in the action queue.
     */
    private final BlockingQueue<UpdateData> _updateQueue =
            new LinkedBlockingQueue<UpdateData>();

    /**
     * The update processor is responsible for receiving updates through
     * the update queue and notify the clients waiting in the action quueue
     */
    private final UpdateProcessor _processor = new UpdateProcessor();

    /**
     * The executor service provides a separate thread for the UpdateProcessor
     * to run
     */
    private final ExecutorService _executorService =
            Executors.newSingleThreadExecutor();


    /**
     * A Lock to synchronize the action manager with the command sender.
     */
    private Lock _lock = new ReentrantLock();

    /**
     * Tracks the responses received for the tracked actions
     */
    private HandlerResponseTracker _handlerResponseTracker = new HandlerResponseTracker();

    /**
     * A container for the update information received
     */
    private class UpdateData {
        int actionId;
        HandlerResponse response;

        private UpdateData(int actionId, HandlerResponse response) {
            this.actionId = actionId;
            this.response = response;
        }
    }

    /**
     * Register that there is a future {@link edu.gemini.aspen.giapi.commands.HandlerResponse}
     * to be received for the given APPLY action. If the action is not an APPLY, this method does nothing.
     *
     * @param action the APPLY action that will get a future response later.
     */
    public void increaseRequiredResponses(Action action) {
        if (action != null && action.getCommand().isApply()) {
            _handlerResponseTracker.increaseRequiredResponses(action);
        }
    }

    /**
     * Constructor.
     */
    public ActionManager() {
    }


    /**
     * The UpdateProcessor is in charge of collecting the update request from
     * the update Queue, and send the update information to the appropriate
     * clients using the completion listener callback. The callbacks are invoked
     * in the thread the UpdateProcessor is running.
     */
    private class UpdateProcessor implements Runnable {

        private boolean running = true;

        /**
         * Update the clients waiting for completion information.
         *
         * @param updateData contains the action Id used to identify the clients
         *                   waiting for completion feedback and the actual
         *                   response to be sent to the clients.
         */

        private void updateClients(UpdateData updateData) {
            int actionId = updateData.actionId;
            HandlerResponse response = updateData.response;
            synchronized (_actionQueue) {
                Action action = _actionQueue.peek();

                if (action == null) {
                    LOG.log(Level.WARNING,
                            "I don't know about action ID " + actionId + ". Usually this means a problem in the instrument code.");
                    return;
                }
                //If the first action in the queue is bigger
                //than the one received, we don't have anything to do. Log this
                //since it's an indication something weird is happening
                if (action.getId() > actionId) {
                    LOG.log(Level.WARNING,
                            "Action ID received " + actionId +
                                    " but our first action to notify is "
                                    + action.getId() +
                                    ". Usually this means a duplicate action " +
                                    "id was received or we got a higher action " +
                                    "id confirmation first.");
                    return;
                }

                //if the action received hasn't been issued yet
                //by the system, that's an indication of a problem
                if (actionId > Action.getCurrentId()) {
                    LOG.log(Level.WARNING,
                            "Action ID received " + actionId +
                                    " but the last ID generated is " + Action.getCurrentId() +
                                    ". This usually is a problem in the instrument code");
                    return;
                }

                _lock.lock(); //acquire the lock before start updating

                try {
                    while (action != null && action.getId() <= actionId) {
                        //store this response to combine it with the other answers we might receive for the same action
                        _handlerResponseTracker.storeResponse(action, response);
                        if (_handlerResponseTracker.isComplete(action)) {
                            LOG.info("Updating clients with action " + action + " response " + response);
                            CompletionListener listener = action.getCompletionListener();
                            if (listener != null) {
                                listener.onHandlerResponse(_handlerResponseTracker.getResponse(action),
                                        action.getCommand());
                            } else {
                                LOG.info("No interested listener on action " + action);
                            }
                            //remove the action from the list of tracked actions
                            _handlerResponseTracker.removeTrackedAction(action);
                            
                            //now, remove the element from the queue
                            _actionQueue.poll();
                            //iterate to the next element
                            action = _actionQueue.peek();
                        } else {
                            LOG.info("Received update for action " + action + " response " +
                                     response + ". Waiting for the other parts of the action to complete...");
                            //in this case, the loop is aborted, since this action is not completed yet,
                            //so we have to keep waiting.
                            action = null;
                        }
                    }
                } finally {
                    _lock.unlock();
                }
            }
        }

        /**
         * Execution loop of this processing thread. Will get update information
         * from the update queue, and whenever that update information is
         * available, will update the clients waiting for completion
         * information
         */
        @Override
        public void run() {
            while (isRunning()) {
                try {
                    updateClients(_updateQueue.take());
                } catch (InterruptedException e) {
                    LOG.info("Update Processor Thread interrupted. Exiting");
                    return;
                }
            }
        }

        /**
         * Stop the current processing thread.
         */
        public synchronized void stop() {
            running = false;
        }

        /**
         * Returns <code>true</code> if the processing thread is running.
         *
         * @return <code>true</code> if the processing thread is running, false
         *         otherwise
         */
        public synchronized boolean isRunning() {
            return running;
        }
    }

    /**
     * Register this Action to keep track its progress internally. When
     * the completion information associated to this action is available, the
     * listener contained in it will be invoked.
     *
     * @param action the action to register
     */
    public void registerAction(Action action) {
        LOG.info("Start monitoring progress for Action " + action);
        _actionQueue.add(action);
    }

    /**
     * Deregister the given action from the monitored actions. This
     * is done so the system don't attempt to update with completion
     * information those actions that complete immediately.
     * @param action the action to deregister. 
     */
    public void unregisterAction(Action action) {
        LOG.info("Stopped monitoring progress for Action " + action + ". Action Completed Immediately");
        _actionQueue.remove(action);
    }

    /**
     * Acquire the updater processor lock. This way, the update processor
     * can't notify handler until the lock is released
     */
    public void lock() {
        _lock.lock();
    }

    /**
     * Release the lock of the update processor thread. The update
     * processor thread will resume execution as soon as reacquires the
     * lock. 
     */
    public void unlock() {
        _lock.unlock();
    }

    /**
     * Register the completion information to be sent to the clients identified
     * by the given action Id. This information is queued and used by the
     * processing thread to notify the clients.
     *
     * @param actionId the action Id used to identify the clients waiting for
     *                 completion feedback
     * @param response the completion information to be sent to the clients.
     */

    public void registerCompletionInformation(int actionId,
                                              HandlerResponse response) {
        UpdateData data = new UpdateData(actionId, response);
        try {
            _updateQueue.put(data);
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING,
                    "Interrupted exception while waiting to register update data",
                    e);
        }
    }

    /**
     * Start up a background thread used to send the completion
     * information invoking the <code>CompletionListener</code> handlers
     * registered.
     */
    public void start() {

        //Submit the processor task for execution in a separate thread
        _executorService.submit(_processor);

    }

    /**
     * Stop the processing thread of this action manager.
     */
    public void stop() {
        _processor.stop();
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
