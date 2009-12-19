package edu.gemini.epics;

/**
 * Base interface for Epics Access Objects. Allows the
 * binding of channel names to actual channels. 
 */
public interface IEpicsBase {
    /**
     * Makes the connection of the specified channel name with an actual
     * EPICS channel.
     * @param channel the channel to connect to
     * @throws EpicsException in case there is a problem connecting to the
     * given channel.
     */
    void bindChannel(String channel) throws EpicsException;
    
}
