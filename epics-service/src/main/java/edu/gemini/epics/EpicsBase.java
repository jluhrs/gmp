package edu.gemini.epics;

import gov.aps.jca.event.ConnectionListener;

/**
 * Base interface for Epics Access Objects. Allows the
 * binding of channel names to actual channels.
 */
public interface EpicsBase {
    /**
     * Makes the connection of the specified channel name with an actual
     * EPICS channel.
     *
     * @param channel the channel to connect to
     * @throws EpicsException in case there is a problem connecting to the
     *                        given channel.
     */
    void bindChannel(String channel) throws EpicsException;

    /**
     * Makes the connection of the specified channel name with an actual
     * EPICS channel asynchronously.
     *
     * @param channel the channel to connect to
     * @throws EpicsException in case there is a problem connecting to the
     *                        given channel.
     */
    void bindChannelAsync(String channel) throws EpicsException;

    /**
     * Makes the connection of the specified channel name with an actual
     * EPICS channel asynchronously.
     *
     * @param channel  the channel to connect to
     * @param listener to notify when the connection state changes
     * @throws EpicsException in case there is a problem connecting to the
     *                        given channel.
     */
    void bindChannelAsync(String channel, ConnectionListener listener) throws EpicsException;

    /**
     * Checks if the specified channel is bound to an actual
     * EPICS channel.
     *
     * @param channel the channel to query
     */
    boolean isChannelConnected(String channel);

    /**
     * Disconnects of the specified channel name
     *
     * @param channel the channel to connect to
     * @throws EpicsException in case there is a problem connecting to the
     *                        given channel.
     */
    void unbindChannel(String channel) throws EpicsException;
}
