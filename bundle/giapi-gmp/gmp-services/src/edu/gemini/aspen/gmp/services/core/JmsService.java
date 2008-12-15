package edu.gemini.aspen.gmp.services.core;

import javax.jms.Session;

/**
 * A Service that stores JMS session internally, so
 * implementators can use it for advanced operations
 * as sending information back to the requestor
 */
public abstract class JmsService implements Service {

    protected Session session;

    /**
     * Set the JMS Session this service can use
     * to interact with the JMS system
     * @param session JMS Session that can
     * be used by the service
     */
    public void setJmsSession(Session session) {
        this.session = session;
    }
}
