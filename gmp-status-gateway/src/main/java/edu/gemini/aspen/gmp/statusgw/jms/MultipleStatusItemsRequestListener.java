package edu.gemini.aspen.gmp.statusgw.jms;


import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import edu.gemini.aspen.giapi.status.StatusItem;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Status Names Request Listener receives a request via JMS .
 * The Status Request Processor looks for the status names in
 * the database service it contains and will sendStatusNames a reply back to the client
 * with the information.
 */
public class MultipleStatusItemsRequestListener implements MessageListener {

    private static final Logger LOG = Logger.getLogger(MultipleStatusItemsRequestListener.class.getName());

    /**
     * The database service to obtain the status item information
     */
    private StatusDatabaseService _db;

    /**
     * The status item dispatcher to sendStatusItem information back to clients
     */
    private JmsStatusDispatcher _dispatcher;


    public MultipleStatusItemsRequestListener(StatusDatabaseService db, JmsStatusDispatcher dispatcher) {
        _db = db;
        _dispatcher = dispatcher;
    }

    public void onMessage(Message message) {
        try {
            if (message.getJMSReplyTo() == null) {
                return; //nothing to do since we don't know where to reply
            }

            Collection<StatusItem> items = _db.getAll();

            _dispatcher.sendMultipleStatusItems(items, message.getJMSReplyTo());

        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem getting status items: ", e);
        }

    }
}
