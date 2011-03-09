package edu.gemini.aspen.gmp.commands.jms;


import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Logger;

/**
 * This is a consumer of Completion Info messages.
 * <p/>
 * Whenever an action in the client code is finished, it has to report back to
 * the GMP with their completion information. Completion information can be
 * either COMPLETED or ERROR.
 */
public class CompletionInfoListener implements MessageListener {
    private static final Logger LOG = Logger.getLogger(
            CompletionInfoListener.class.getName());

    public final static String QUEUE_NAME = JmsKeys.GMP_COMPLETION_INFO;

    private final CommandUpdater _commandUpdater;

    public CompletionInfoListener(CommandUpdater updater) {
        Preconditions.checkArgument(updater != null, "CommandUpdater cannot be null");
        _commandUpdater = updater;
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof MapMessage) {
                MapMessage m = (MapMessage) message;

                int actionId = m.getIntProperty(JmsKeys.GMP_ACTIONID_PROP);
                HandlerResponse response = MessageBuilder.buildHandlerResponse(m);
                LOG.info("Received Completion info for action ID " +
                                actionId + " : " + response.toString());
                //Notify the OCS. Based on the action ID, we know who to notify
                _commandUpdater.updateOcs(actionId, response);
            }
        } catch (JMSException e) {
            throw new SequenceCommandException(
                    "Unable to update OCS with completion information", e);
        }
    }
}
