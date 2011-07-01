package edu.gemini.aspen.giapi.util.jms.status;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;
import edu.gemini.jms.api.BaseMessageProducer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * This class encapsulates the code to publish a StatusItem update.
 */
public class StatusSetter extends BaseMessageProducer {
    public StatusSetter(String statusName) {
        super("Status Setter", new DestinationData(JmsKeys.GMP_STATUS_DESTINATION_PREFIX + statusName, DestinationType.TOPIC));
    }

    /**
     * Sets a status item name and value
     *
     * @param statusItem item to send to the Status Database
     * @throws JMSException
     */
    public void setStatusItem(StatusItem statusItem) throws JMSException {

        //request the value
        Message m = MessageBuilder.buildStatusItemMessage(_session, statusItem);


        //sendStatusItem the message
        _producer.send(m);

    }

}
