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
public class StatusGetter extends BaseMessageProducer {

    public StatusGetter()  {
        super("Status Getter", new DestinationData(JmsKeys.GW_STATUS_REQUEST_DESTINATION, DestinationType.TOPIC));
    }

    public StatusItem getStatusItem(String statusName) throws TesterException {

        //request the value
        try {
            Message m = _session.createMessage();
            m.setStringProperty(JmsKeys.GW_STATUS_NAME_PROPERTY, statusName);

            //create a consumer to receive the answer
            Destination tempQueue = _session.createTemporaryQueue();
            m.setJMSReplyTo(tempQueue);
            MessageConsumer tempConsumer = _session.createConsumer(tempQueue);

            //send the message
            _producer.send(m);

            Message reply = tempConsumer.receive(1000); //1000 msec to answer.

            tempConsumer.close();

            return MessageBuilder.buildStatusItem(reply);
        } catch (JMSException e) {
            throw new TesterException(e);
        }
    }
}
