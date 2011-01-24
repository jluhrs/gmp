package edu.gemini.jms.api;

import javax.jms.Destination;
import javax.jms.Session;
import javax.jms.JMSException;

/**
 * Utility class to construct JMS Destination objects from the
 * {@link edu.gemini.jms.api.DestinationData} container.
 */
public class DestinationBuilder {


    /**
     * Get a new {@link javax.jms.Destination} based on the given
     * {@link edu.gemini.jms.api.DestinationData} container for the given
     * {@link javax.jms.Session}.
     *
     * @param dd      Destination data container used to construct the JMS
     *                Destination
     * @param session JMS Session that will be used to construct the destination
     * @return a new {@link javax.jms.Destination} object
     * @throws JMSException in case there is a problem creating the new
     *                      session
     */
    public Destination newDestination(DestinationData dd, Session session) throws JMSException {
        if (dd != null && dd.getName() != null) {
            switch (dd.getType()) {
                case QUEUE:
                    return session.createQueue(dd.getName());
                case TOPIC:
                    return session.createTopic(dd.getName());
            }
        }
        return null;
    }
}
