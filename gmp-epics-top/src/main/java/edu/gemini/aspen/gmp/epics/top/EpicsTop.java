package edu.gemini.aspen.gmp.epics.top;

/**
 * Interface EpicsTop
 *
 * @author Nicolas A. Barriga
 *         Date: 4/6/11
 */
public interface EpicsTop {
    /**
     * Adds the top to the given channel name
     *
     * @param name base name of the channel
     * @return full name of the channel, including the top
     */
    String buildChannelName(String name);
}
