package edu.gemini.aspen.gmp.statusgw.jms;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.gmp.util.jms.GmpJmsUtil;
import edu.gemini.jms.api.BaseMessageProducer;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

/**
 * A producer to send status items back to the client using JMS
 */
public class JmsStatusItemDispatcher extends BaseMessageProducer {

    public JmsStatusItemDispatcher(String clientName) {
        super(clientName, null);
    }

    /**
     * Sends a status item via JMS
     */
    public void send(StatusItem item, Destination destination) throws JMSException {
        Message replyMessage = GmpJmsUtil.buildStatusItemMessage(_session, item);
        _producer.send(destination, replyMessage);
    }

}