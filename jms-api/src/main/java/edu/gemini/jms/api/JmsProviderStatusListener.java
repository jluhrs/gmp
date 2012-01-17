package edu.gemini.jms.api;

/**
 * Services implementing this interface get notifications when the JmsProvider
 * is connected or disconnected
 */
public interface JmsProviderStatusListener {
    /**
     * Callback called when the connection to the ActiveMQ broker starts or is restored
     */
    void transportResumed();

    /**
     * Callback called when the connection to the ActiveMQ broker gets interrupted
     */
    void transportInterrupted();
}
