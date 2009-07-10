package edu.gemini.aspen.gmp.commands.jms;


import javax.jms.*;

import edu.gemini.aspen.gmp.commands.api.CommandUpdater;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.util.jms.GmpJmsUtil;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;

import java.util.logging.Logger;

/**
 * This is a consumer of Completion Info messages.
 * <p/>
 * Whenever an action in the client code is finished, it has to report back to
 * the GMP with their completion information. Completion information can be
 * either COMPLETED or ERROR.
 */
public class JMSCompletionInfoListener implements MessageListener {

    private static final Logger LOG = Logger.getLogger(
            JMSCompletionInfoListener.class.getName());

    public final static String QUEUE_NAME = GmpKeys.GMP_COMPLETION_INFO;

    private CommandUpdater _commandUpdater;

    public JMSCompletionInfoListener(CommandUpdater updater) {
        _commandUpdater = updater;
    }

    public void onMessage(Message message) {
        try {
            if (message instanceof MapMessage) {
                MapMessage m = (MapMessage) message;

                int actionId = m.getIntProperty(GmpKeys.GMP_ACTIONID_PROP);
                HandlerResponse response = GmpJmsUtil.buildHandlerResponse(m);
                LOG.info(
                        "Received Completion info for action ID " + actionId + " : " + response
                                .toString());
                //Notify the OCS. Based on the action ID, we know who to notify
                _commandUpdater.updateOcs(actionId, response);
            }
        } catch (JMSException e) {
            LOG.warning("onMessage exception : " + e);
        }
    }
}
