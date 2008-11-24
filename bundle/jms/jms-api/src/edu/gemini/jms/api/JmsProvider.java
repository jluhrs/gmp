package edu.gemini.jms.api;

import javax.jms.ConnectionFactory;

/**
 * Provides access to the factory to construct
 * JMS objects, hidding the details of the specific JMS
 * provider in use
 */
public interface JmsProvider {

    /**
     * Gets the connection factory associated
     * to the current JMS provider
     * @return a JMS ConnectionFactory
     */
    ConnectionFactory getConnectionFactory();
    
}
