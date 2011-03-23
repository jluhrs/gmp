package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;


import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Logger;

/**
 * This is a consumer of CompletionInfo messages send over JMS.
 * <p/>
 * Whenever an action in the client code is finished, it has to report back to
 * the GMP with their completion information. Completion information can be
 * either COMPLETED or ERROR.
 */
@Component
@Instantiate
class CompletionInfoListener implements MessageListener {
    private static final Logger LOG = Logger.getLogger(
            CompletionInfoListener.class.getName());

    private static final String QUEUE_NAME = JmsKeys.GMP_COMPLETION_INFO;
    private static final String CONSUMER_NAME = "JMS Completion Info Consumer";

    private final CommandUpdater _commandUpdater;
    private final BaseMessageConsumer _messageConsumer;

    private JmsProvider _provider;

    public CompletionInfoListener(@Requires CommandUpdater updater, @Requires JmsProvider jmsProvider) {
        Preconditions.checkArgument(updater != null, "CommandUpdater cannot be null");
        Preconditions.checkArgument(jmsProvider != null, "JmsProvider cannot be null");
        _commandUpdater = updater;
        _provider = jmsProvider;

        _messageConsumer = new BaseMessageConsumer(
                CONSUMER_NAME,
                new DestinationData(QUEUE_NAME, DestinationType.QUEUE),
                this
        );
    }

    @Override
    public void onMessage(Message message) {
        try {
            processIncomingMessage(message);
        } catch (JMSException e) {
            throw new SequenceCommandException(
                    "Unable to update OCS with completion information", e);
        }
    }

    private void processIncomingMessage(Message message) throws JMSException {
        if (message instanceof MapMessage) {
            decodeMessageAndNotify((MapMessage) message);
        } else {
            LOG.warning("Arrived unknown message to " + QUEUE_NAME + ": " + message);
        }
    }

    private void decodeMessageAndNotify(MapMessage message) throws JMSException {
        if (messageContainsActionId(message)) {
            int actionId = message.getIntProperty(JmsKeys.GMP_ACTIONID_PROP);
            HandlerResponse response = MessageBuilder.buildHandlerResponse(message);

            LOG.info("Received Completion info for action ID " +
                    actionId + " : " + response.toString());

            //Notify the CommandUpdater
            _commandUpdater.updateOcs(actionId, response);
        } else {
            LOG.warning("Cannot process reply message without ActionID");
        }
    }

    private boolean messageContainsActionId(MapMessage message) throws JMSException {
        return message.propertyExists(JmsKeys.GMP_ACTIONID_PROP);
    }

    @Validate
    public void startListening() throws JMSException {
        _messageConsumer.startJms(_provider);
    }

    @Invalidate
    public void stopListening() {
        _messageConsumer.stopJms();
    }
}
