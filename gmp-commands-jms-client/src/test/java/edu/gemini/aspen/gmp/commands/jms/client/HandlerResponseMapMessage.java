package edu.gemini.aspen.gmp.commands.jms.client;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.test.MapMessageMock;

import javax.jms.JMSException;

/**
 * This class is a mock of how a reply for a given handler message would look like
 *
 * It is used to simulate responses from the GMP
 */
public class HandlerResponseMapMessage extends MapMessageMock {
    public HandlerResponseMapMessage(HandlerResponse response) throws JMSException {
        super();
        setString(JmsKeys.GMP_HANDLER_RESPONSE_KEY, response.getResponse().toString());
        if (response.hasErrorMessage()) {
            setString(JmsKeys.GMP_HANDLER_RESPONSE_ERROR_KEY, response.getMessage());
        }
    }
}
