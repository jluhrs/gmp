package edu.gemini.aspen.gmp.services.jms;

import edu.gemini.aspen.gmp.services.core.ServiceRequest;

import javax.jms.MapMessage;

/**
 *  A JMS Service requests contains a map message with the request on it.
 *
 * The Map message is parsed internally by the services, so it is up to
 * them to interpret the content of it. 
 */
public class JmsServiceRequest implements ServiceRequest {

    private final MapMessage _msg;

    public JmsServiceRequest(MapMessage msg) {
        _msg = msg;
    }

    public MapMessage getMessage() {
        return _msg;
    }

}
