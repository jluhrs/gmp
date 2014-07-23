package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.messagebuilders.ObjectBasedMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import edu.gemini.jms.api.*;

import javax.jms.JMSException;
import java.util.logging.Logger;

/**
 * This is the implementation of an ActionSender that uses JMS to send the
 * sequence commands to the down to the instrument. This class implements
 * the {@link edu.gemini.aspen.gmp.commands.model.ActionSender} interface
 * that is required by the SequenceCommandExecutor
 */
public class ActionMessageActionSender implements ActionSender, JmsArtifact {
    private static final Logger LOG = Logger.getLogger(ActionMessageActionSender.class.getName());
    private final HandlerResponseSenderReply _messageSender = new HandlerResponseSenderReply(JmsKeys.GW_COMMAND_TOPIC);

    @Override
    public HandlerResponse send(ActionMessage actionMessage) throws SequenceCommandException {
        return send(actionMessage, CommandSender.DEFAULT_COMMAND_RESPONSE_TIMEOUT);
    }

    @Override
    public HandlerResponse send(ActionMessage actionMessage, long timeout) {
        Preconditions.checkArgument(actionMessage != null, "Passed action message to send cannot be null");
        Preconditions.checkArgument(timeout > 0, "Timeout must be more than zero");

        try {
            return sendMessageAndWaitForReply(actionMessage, timeout);
        } catch (MessagingException e) {
            throw new SequenceCommandException("Unable to send action", e);
        }
    }

    /**
     * Does the actual sending of the message down to the instrument, and waits for the initial response
     * the specified timeout
     */
    private HandlerResponse sendMessageAndWaitForReply(ActionMessage actionMessage, long timeout) {
        DestinationData destinationData = new DestinationData(actionMessage.getDestinationName(), DestinationType.TOPIC);
        ObjectBasedMessageBuilder instrumentMessageBuilder = new ObjectBasedMessageBuilder(
                actionMessage.getDataElements(),
                actionMessage.getProperties());
        LOG.fine("Attempt to send action message to " + destinationData + " and wait for reply");

        return _messageSender.sendMessageWithReply(destinationData, instrumentMessageBuilder, timeout);
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        _messageSender.startJms(provider);
    }

    @Override
    public void stopJms() {
        _messageSender.stopJms();
    }
}
