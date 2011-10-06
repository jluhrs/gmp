package edu.gemini.aspen.giapi.util.jms;

import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEvent;
import edu.gemini.jms.api.BaseMessageProducer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * This class encapsulates the code to publish a StatusItem update.
 */
public class ObsEventSender extends BaseMessageProducer {
    public ObsEventSender(String clientName) {
        super(clientName, new DestinationData(JmsKeys.GMP_DATA_OBSEVENT_DESTINATION, DestinationType.TOPIC));
    }

    /**
     * Sets a status item name and value
     *
     * @param obsEvent obs event to send via JMS
     * @param label The data label this events refers to
     * @throws javax.jms.JMSException
     */
    public void send(ObservationEvent obsEvent, DataLabel label) throws JMSException {

        //request the value
        Message m = MessageBuilder.buildObsEventMessage(_session, obsEvent, label);


        //sendStatusItem the message
        _producer.send(m);

    }

}
