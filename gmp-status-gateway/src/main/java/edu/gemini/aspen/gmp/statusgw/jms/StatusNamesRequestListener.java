package edu.gemini.aspen.gmp.statusgw.jms;


import edu.gemini.aspen.giapi.status.StatusDatabaseService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Status Names Request Listener receives a request via JMS .
 * The Status Request Processor looks for the status names in
 * the database service it contains and will sendStatusNames a reply back to the client
 * with the information.
 */
public class StatusNamesRequestListener implements MessageListener {

    private static final Logger LOG = Logger.getLogger(StatusNamesRequestListener.class.getName());

    /**
     * The database service to obtain the status item information
     */
    private StatusDatabaseService _db;

    /**
     * The status item dispatcher to sendStatusItem information back to clients
     */
    private JmsStatusDispatcher _dispatcher;


    public StatusNamesRequestListener(StatusDatabaseService db, JmsStatusDispatcher dispatcher) {
        _db = db;
        _dispatcher = dispatcher;
    }

    public void onMessage(Message message) {
        try {
            if (message.getJMSReplyTo() == null) {
                return; //nothing to do since we don't know where to reply
            }

            Set<String> names = _db.getStatusNames();
            
            _dispatcher.sendStatusNames(names, message.getJMSReplyTo());

        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem getting status item names: ", e);
        }

    }
}
