package edu.gemini.aspen.gmp.epics;

/**
 * Interface for classes that keep track of registering
 * <code>EpicsUpdateListener</code> to get epics channel updates.
 * <br>
 * Implementations of this class will process the epics channel
 * updates received by invoking the listeners registered.
 * The listeners will be invoked in a separate execution thread.
 */
public interface EpicsRegistrar {
    /**
     * Register the specific listener to be invoked whenever an update to
     * the epics channel specified by name is received
     *
     * @param channel name of the epics channel whose update will trigger
     *                a call to the listener.
     * @param updater listener to be invoked when an update is received
     */
    void registerInterest(String channel, EpicsUpdateListener updater);

    /**
     * Stop updating the listener associated to the channel
     *
     * @param channel name of the epics channel that will stop being monitored.
     */
    void unregisterInterest(String channel);


    /**
     * Put the epics update in the queue for processing. Any
     * registered listeners will be invoked in a separate thread
     * with this update.
     *
     * @param update an Epics Status Update
     */
    void processEpicsUpdate(EpicsUpdate<?> update);


    /**
     * Start processing the updates received
     */
    void start();

    /**
     * Stop processing the updates received.
     */
    void stop();
}
