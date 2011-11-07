package edu.gemini.aspen.gmp.epics;

import java.util.List;

/**
 * Interface to describe an Epics Update.
 */
public interface EpicsUpdate<T> {

    /**
     * Name of the channel with an update
     *
     * @return string with the channel name
     */
    String getChannelName();

    /**
     * The data in the channel update
     *
     * @return the data in the channel update
     */
    List<T> getChannelData();
}
