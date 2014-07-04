package edu.gemini.gmp.top;

/**
 * Interface Top
 *
 * @author Nicolas A. Barriga
 *         Date: 4/6/11
 */
public interface Top {
    /**
     * Adds the top to the given channel name
     *
     * @param name base name of the channel
     * @return full name of the channel, including the top
     */
    String buildEpicsChannelName(String name);

    /**
     * Adds the top to the given StatusItem name
     *
     * @param name base name of the StatusItem
     * @return full name of the StatusItem, including the top
     */
    String buildStatusItemName(String name);

}
