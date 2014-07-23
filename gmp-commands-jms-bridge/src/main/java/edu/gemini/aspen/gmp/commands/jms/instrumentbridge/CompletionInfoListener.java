package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.HandlerResponseMessageParser;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import edu.gemini.jms.api.*;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Logger;

/**
 * This is a consumer of CompletionInfo messages send over JMS.
 * <br>
 * Whenever an action in the client code is finished, it has to report back to
 * the GMP with their completion information. Completion information can be
 * either COMPLETED or ERROR.
 */
public class CompletionInfoListener implements MessageListener, JmsArtifact {
    private static final Logger LOG = Logger.getLogger(
            CompletionInfoListener.class.getName());

    private static final String QUEUE_NAME = JmsKeys.GMP_COMPLETION_INFO;
    private static final String CONSUMER_NAME = "JMS Completion Info Consumer";

    private final CommandUpdater _commandUpdater;
    private final BaseMessageConsumer _messageConsumer;


    public CompletionInfoListener(CommandUpdater updater) {
        Preconditions.checkArgument(updater != null, "CommandUpdater cannot be null");
        _commandUpdater = updater;

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
        } catch (FormatException e) {
            throw new SequenceCommandException(
                    "Unable to update OCS with completion information", e);
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
            HandlerResponseMessageParser parser = new HandlerResponseMessageParser(message);
            HandlerResponse response = parser.readResponse();

            LOG.info("Received Completion info for action ID " +
                    actionId + " : " + response.toString());

            //Notify the CommandUpdater
            _commandUpdater.updateOcs(actionId, response);
        } else {
            LOG.warning("Cannot process reply message without Action ID");
        }
    }

    private boolean messageContainsActionId(MapMessage message) throws JMSException {
        return message.propertyExists(JmsKeys.GMP_ACTIONID_PROP);
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        _messageConsumer.startJms(provider);
    }

    @Override
    public void stopJms() {
        _messageConsumer.stopJms();
    }
}