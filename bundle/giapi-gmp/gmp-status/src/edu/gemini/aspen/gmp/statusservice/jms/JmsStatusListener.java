package edu.gemini.aspen.gmp.statusservice.jms;

import edu.gemini.aspen.gmp.status.StatusHandler;
import edu.gemini.aspen.gmp.status.StatusItem;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;
import edu.gemini.aspen.gmp.util.jms.GmpJmsUtil;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *  The JMS Consumer to receive Status Items from the instrument
 */
public class JmsStatusListener implements MessageListener {

    private static final Logger LOG = Logger.getLogger(JmsStatusListener.class.getName());

    public static final String TOPIC_NAME = GmpKeys.GMP_STATUS_DESTINATION_PREFIX + ">"; //defaults to listen for all the status items.

    private final StatusHandler _updater;

    public JmsStatusListener(StatusHandler updater) {
        _updater = updater;
    }

    @Override
    public void onMessage(Message message) {
        try {
            //reconstruct the StatusItem from the JMS Message
            StatusItem item = GmpJmsUtil.buildStatusItem(message);
            if (item != null) {
                _updater.update(item);
            }

        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem receiving status message", e);
        }
    }

}
