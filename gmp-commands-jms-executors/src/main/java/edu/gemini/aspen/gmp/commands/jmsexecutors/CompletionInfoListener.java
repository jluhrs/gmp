package edu.gemini.aspen.gmp.commands.jmsexecutors;


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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a consumer of Completion Info messages.
 * <p/>
 * Whenever an action in the client code is finished, it has to report back to
 * the GMP with their completion information. Completion information can be
 * either COMPLETED or ERROR.
 */
@Component
@Instantiate
public class CompletionInfoListener implements MessageListener {
    private static final Logger LOG = Logger.getLogger(
            CompletionInfoListener.class.getName());

    public final static String QUEUE_NAME = JmsKeys.GMP_COMPLETION_INFO;

    private final CommandUpdater _commandUpdater;
    private final BaseMessageConsumer _messageConsumer;

    private JmsProvider _provider;

    public CompletionInfoListener(@Requires CommandUpdater updater, @Requires JmsProvider jmsProvider) {
        Preconditions.checkArgument(updater != null, "CommandUpdater cannot be null");
        Preconditions.checkArgument(jmsProvider != null, "JmsProvider cannot be null");
        _commandUpdater = updater;
        _provider = jmsProvider;

        _messageConsumer = new BaseMessageConsumer(
                "JMS Completion Info Consumer",
                new DestinationData(CompletionInfoListener.QUEUE_NAME,
                        DestinationType.QUEUE),
                this
        );
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

    @Validate
    public void startListening() {
        try {
            _messageConsumer.startJms(_provider);
        } catch (JMSException e) {
            LOG.log(Level.SEVERE, "Exception while starting the JMS connection");
        }
    }

    @Invalidate
    public void stopListening() {
        _messageConsumer.stopJms();
    }
}
