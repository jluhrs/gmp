package edu.gemini.aspen.gmp.epics;

/**
 * A listener interface that will be invoked whenever a change to an
 * EPICS status item is received.
 */
public interface EpicsUpdateListener {


    /**
     * Invoked whenever an Epics Update is available for the
     * epics channel name this listener is registered to.
     *
     * @param update the update information for the monitored epics channel.
     */
    public void onEpicsUpdate(EpicsUpdate<?> update);

}
