package edu.gemini.giapi.tool.status;

import edu.gemini.aspen.gmp.status.StatusHandler;
import edu.gemini.aspen.gmp.status.StatusItem;
import edu.gemini.aspen.gmp.util.jms.GmpJmsUtil;
import edu.gemini.giapi.tool.jms.BrokerConnection;
import edu.gemini.giapi.tool.TesterException;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Gets status items from JMS and notifies the given handler when updates are
 * received.
 */

public class StatusReader implements ExceptionListener {

    private static final Logger LOG = Logger.getLogger(StatusReader.class.getName());
    private StatusHandler _handler;

    private MessageConsumer _consumer;

    public StatusReader(BrokerConnection connection,
                        StatusHandler handler, String statusName)
            throws TesterException {

        _handler = handler;

        Session session = connection.getSession();

        try {
            //read the status from the topic with the same name as the item
            Destination destination = session.createTopic(
                    statusName);
            _consumer = session.createConsumer(destination);

        } catch (JMSException ex) {
            throw new TesterException(ex);
        }


    }

    public void onException(JMSException e) {
        LOG.log(Level.WARNING, "Exception on Status Reader", e);
    }

    /**
     * Monitor loop. Wait forever for messages to come in and update
     * the handler as they arrive.
     *
     * @throws TesterException if there is a problem receiving messages
     */
    public void start() throws TesterException {


        while (true) {
            try {
                Message m = _consumer.receive(); //blocking method
                StatusItem item = GmpJmsUtil.buildStatusItem(m);

                if (item != null) {
                    _handler.update(item);
                }
            } catch (JMSException e) {
                throw new TesterException(e);
            }


        }
    }
}
