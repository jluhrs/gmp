package edu.gemini.jms.api;

import javax.jms.JMSException;

/**
 * Public interface for any JMS Artifact, as message consumers or producers
 */
public interface JmsArtifact {
    /**
     * Start the connection to the given JMS Provider.
     * @param provider the JMS Provider to connect to
     * @throws javax.jms.JMSException in case there is a problem initializing
     * the JMS artifact
     */
    void startJms(JmsProvider provider) throws JMSException;

    /**
     * Stop this JMS artifact.
     */
    void stopJms();
}
