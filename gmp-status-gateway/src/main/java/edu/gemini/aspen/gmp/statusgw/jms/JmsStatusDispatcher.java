package edu.gemini.aspen.gmp.statusgw.jms;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;
import edu.gemini.jms.api.BaseMessageProducer;

import javax.jms.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * A producer to sendStatusItem status items back to the client using JMS
 */
public class JmsStatusDispatcher extends BaseMessageProducer {

    public JmsStatusDispatcher(String clientName) {
        super(clientName, null);
    }

    /**
     * Sends a status item via JMS
     *
     * @param item        the StatusItem to send
     * @param destination where to send it
     * @throws JMSException
     */
    public void sendStatusItem(StatusItem item, Destination destination) throws JMSException {
        Message replyMessage = MessageBuilder.buildStatusItemMessage(_session, item);
        _producer.send(destination, replyMessage);
    }

    /**
     * Sends all the status names via JMS
     *
     * @param names       the status names to send
     * @param destination where to send them
     * @throws JMSException
     */
    public void sendStatusNames(Set<String> names, Destination destination) throws JMSException {
        BytesMessage replyMessage = _session.createBytesMessage();

        //just create an empty set so the client doesn't timeout
        if (names == null) {
            names = new TreeSet<String>();
        }

        //create message, fill it and send it
        replyMessage.writeInt(names.size());
        for (String name : names) {
            replyMessage.writeUTF(name);
        }
        _producer.send(destination, replyMessage);
    }

    /**
     * Sends multiple status items via JMS
     *
     * @param items       the status items to send
     * @param destination where to send them
     * @throws JMSException
     */
    public void sendMultipleStatusItems(Collection<StatusItem> items, Destination destination) throws JMSException {
        //just create an empty set so the client doesn't timeout
        if (items == null) {
            items = new ArrayList<StatusItem>();
        }

        //create message, fill it and send it
        Message replyMessage = MessageBuilder.buildMultipleStatusItemsMessage(_session, items);

        _producer.send(destination, replyMessage);
    }
}