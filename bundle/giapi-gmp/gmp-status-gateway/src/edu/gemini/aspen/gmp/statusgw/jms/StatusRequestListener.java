package edu.gemini.aspen.gmp.statusgw.jms;


import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The Status Request Listener receives a request via JMS about a Status
 * Item by name. The Status Request Processor looks for the status item in
 * the database service it contains and will send a reply back to the client
 * with the information about it. 
 */
public class StatusRequestListener implements MessageListener {

    private static final Logger LOG = Logger.getLogger(StatusRequestListener.class.getName());

    public static final String TOPIC_NAME = JmsKeys.GW_STATUS_REQUEST_DESTINATION;

    /**
     * The database service to obtain the status item information
     */
    private StatusDatabaseService _db;

    /**
     * The status item dispatcher to send information back to clients
     */
    private JmsStatusItemDispatcher _dispatcher;


    public StatusRequestListener(StatusDatabaseService db, JmsStatusItemDispatcher dispatcher) {
        _db = db;
        _dispatcher = dispatcher;
    }

    public void onMessage(Message message) {

        try {
            if (message.getJMSReplyTo() == null) {
                return; //nothing to do since we don't know where to reply
            }

            String statusName = message.getStringProperty(JmsKeys.GW_STATUS_NAME_PROPERTY);
            StatusItem item = _db.getStatusItem(statusName);

            if (item == null) {
                return; //no item found, can't reply
            }
            _dispatcher.send(item, message.getJMSReplyTo());

        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem processing status item request message: ", e);
        }

    }
}
