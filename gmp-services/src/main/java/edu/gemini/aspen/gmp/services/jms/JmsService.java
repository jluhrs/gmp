package edu.gemini.aspen.gmp.services.jms;

import edu.gemini.aspen.gmp.services.core.Service;
import edu.gemini.aspen.gmp.services.core.ServiceRequest;
import edu.gemini.aspen.gmp.services.core.ServiceException;

import javax.jms.Session;

/**
 * A Service that stores the JMS session internally, so
 * implementers can use it for advanced operations
 * as sending information back to the requester
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

    /**
     * The process method of a JmsService will only extract the JMS Message
     * from the request, and will pass it to the process method with the
     * JMS message argument.
     * @param request A service request. If it's not a JmsServiceRequest,
     * this method doesn't do anything.
     * @throws edu.gemini.aspen.gmp.services.core.ServiceException
     */
    public void process(ServiceRequest request) throws ServiceException {
        if (request instanceof JmsServiceRequest) {
            JmsServiceRequest jmsRequest = (JmsServiceRequest) request;
            process(jmsRequest);
        }
    }

    /**
     * Process the given JmsServiceRequest
     * @param request the JMS Service Request to be processed. 
     * @throws ServiceException in case the service can not process the
     * request
     */
    public abstract void process(JmsServiceRequest request) throws ServiceException;
}
