package edu.gemini.epics.api;

import java.util.List;

/**
 * Interface for a client that wishes to be told when an Epics channel value is
 * updated. Clients should register instances as services, specifying the channels
 * they are interested in via the property EPICS_CHANNELS, which should be an
 * array of Strings.
 */
public interface EpicsClient {

    /**
     * Service property for defining the channels that the client cares about.
     * Pass this as part of your registration as an array of Strings.
     */
    String EPICS_CHANNELS = EpicsClient.class.getName() + ".EPICS_CHANNELS";

    /**
     * Called when the specified channel value changes.
     *
     * @param channel name of the epics channel that changed.
     * @param values
     */
    <T> void valueChanged(String channel, List<T> values);

    /**
     * Called when the client is connected.
     */
    void connected();

    /**
     * Called when the client is disconnected.
     */
    void disconnected();

}
