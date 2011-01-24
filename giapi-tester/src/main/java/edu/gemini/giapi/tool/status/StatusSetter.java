package edu.gemini.giapi.tool.status;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;
import edu.gemini.giapi.tool.TesterException;
import edu.gemini.jms.api.BaseMessageProducer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;

import javax.jms.*;

/**
 *
 */
public class StatusSetter extends BaseMessageProducer {
    public StatusSetter(String statusName)  {
        super("Status Setter", new DestinationData(JmsKeys.GMP_STATUS_DESTINATION_PREFIX+statusName, DestinationType.TOPIC));
    }

    /**
     * Sets a status item name and value
     * 
     * @param statusItem  item to send to the Status Database
     * @throws TesterException
     */
    public void setStatusItem(StatusItem statusItem) throws TesterException {

        //request the value
        try {
            Message m = MessageBuilder.buildStatusItemMessage(_session,statusItem);
              

            //sendStatusItem the message
            _producer.send(m);

        } catch (JMSException e) {
            throw new TesterException(e);
        }
    }
 
}
