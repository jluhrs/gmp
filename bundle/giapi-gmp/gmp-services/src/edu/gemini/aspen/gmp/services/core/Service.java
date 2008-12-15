package edu.gemini.aspen.gmp.services.core;

import javax.jms.MapMessage;
import javax.jms.JMSException;

/**
 * A Service definition interface.
 *
 * All services will have a method to process a given request, and a
 * type. 
 *
 */
public interface Service {

    public void process(MapMessage message) throws JMSException;

    public ServiceType getType();

}
