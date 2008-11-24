package edu.gemini.jms.api;

import javax.jms.ConnectionFactory;

/**
 *
 */
public interface JmsProvider {

    ConnectionFactory getConnectionFactory();
    
}
