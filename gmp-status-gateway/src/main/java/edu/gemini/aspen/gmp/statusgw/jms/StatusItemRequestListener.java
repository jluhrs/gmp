package edu.gemini.aspen.gmp.statusgw.jms;


import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.StatusDatabaseService;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The Status Request Listener receives a request via JMS about a Status
 * Item by name. The Status Request Processor looks for the status item in
 * the database service it contains and will sendStatusItem a reply back to the client
 * with the information about it. 
 */
public class StatusItemRequestListener implements MessageListener {

    private static final Logger LOG = Logger.getLogger(StatusItemRequestListener.class.getName());

    /**
     * The database service to obtain the status item information
     */
    private StatusDatabaseService _db;

    /**
     * The status item dispatcher to sendStatusItem information back to clients
     */
    private JmsStatusDispatcher _dispatcher;


    public StatusItemRequestListener(StatusDatabaseService db, JmsStatusDispatcher dispatcher) {
        _db = db;
        _dispatcher = dispatcher;
    }

    public void onMessage(Message message) {
        try {
            if (message.getJMSReplyTo() == null) {
                return; //nothing to do since we don't know where to reply
            }

            if (!(message instanceof TextMessage)) {
                LOG.log(Level.WARNING, "Message received is not a TextMessage instance.");
                return;
            }
            TextMessage tm = (TextMessage)message;
            String statusName = tm.getText();
            StatusItem item = _db.getStatusItem(statusName);

            //TODO: add a way to report back that the statusName requested does not exist
            if (item == null) {
                LOG.log(Level.INFO, "Requested status item "+statusName+" doesn't exist.");
                return; //no item found, can't reply
            }
            _dispatcher.sendStatusItem(item, message.getJMSReplyTo());

        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem processing status item request message: ", e);
        }

    }
}
