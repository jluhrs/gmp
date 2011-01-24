package edu.gemini.aspen.gmp.epics;

/**
 *
 * Interface to describe an Epics Update.
 *
 */
public interface EpicsUpdate {

    /**
     * Name of the channel with an update
     * @return string with the channel name
     */
    String getChannelName();

    /**
     * The data in the channel update
     * @return the data in the channel update
     */
    Object getChannelData();
}
