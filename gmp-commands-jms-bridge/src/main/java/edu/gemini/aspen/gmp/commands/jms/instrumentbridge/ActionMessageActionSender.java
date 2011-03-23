package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.jms.api.MessagingException;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import javax.jms.JMSException;

/**
 * This is the implementation of an ActionSender that uses JMS to send the
 * sequence commands to the down to the instrument. This class implements
 * the {@link edu.gemini.aspen.gmp.commands.model.ActionSender} interface
 */
@Component
@Instantiate
@Provides(specifications = {ActionSender.class})
class ActionMessageActionSender implements ActionSender {
    private final HandlerResponseSenderReply _messageSender = new HandlerResponseSenderReply(JmsKeys.GW_COMMAND_TOPIC);
    private final JmsProvider _provider;

    public ActionMessageActionSender(@Requires JmsProvider jmsProvider) {
        Preconditions.checkArgument(jmsProvider != null, "JmsProvider cannot be null");
        this._provider = jmsProvider;
    }

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

    private HandlerResponse sendMessageAndWaitForReply(ActionMessage actionMessage, long timeout) {
        DestinationData destinationData = new DestinationData(actionMessage.getDestinationName(), DestinationType.TOPIC);
        InstrumentCommandMessageBuilder instrumentMessageBuilder = new InstrumentCommandMessageBuilder(
                actionMessage.getDataElements(),
                actionMessage.getProperties());

        return _messageSender.sendMessageWithReply(destinationData, instrumentMessageBuilder, timeout);
    }

    @Validate
    public void startJmsClient() throws JMSException {
        _messageSender.startJms(_provider);
    }

    @Invalidate
    public void stopJmsClient() {
        _messageSender.stopJms();
    }
}
