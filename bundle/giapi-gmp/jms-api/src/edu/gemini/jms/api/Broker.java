package edu.gemini.jms.api;

/**
 *  A Broker provides the core communication platform for the
 * Gemini Master Process.
 */

public interface Broker {

    /**
     * Initialize the service, allowing clients to connect and interact
     */
    void start();

    /**
     * Shutdown the service and associated resources.
     */
    void shutdown();
}
