package edu.gemini.aspen.giapi.statusservice.jms;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;

import javax.jms.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *  The JMS Consumer to receive Status Items from the instrument
 */
public class JmsStatusListener implements MessageListener {

    private static final Logger LOG = Logger.getLogger(JmsStatusListener.class.getName());

    private final StatusHandler _updater;

    public JmsStatusListener(StatusHandler updater) {
        _updater = updater;
    }

    @Override
    public void onMessage(Message message) {
        try {
            //reconstruct the StatusItem from the JMS Message
            StatusItem item = MessageBuilder.buildStatusItem(message);
            if (item != null) {
                _updater.update(item);
            }

        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem receiving status message", e);
        }
    }

}
