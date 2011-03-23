package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
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
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This component listens to commands sent over JMS and forwards them to CommandSender
 * <p/>
 * It also creates listeners to track responses to the commands
 */
@Component
@Instantiate
public class CommandMessagesBridge implements MessageListener {
    private static final Logger LOG = Logger.getLogger(CommandMessagesBridge.class.getName());

    private final JmsProvider _jmsProvider;
    private final CommandSender _commandSender;

    private final BaseMessageConsumer _messageConsumer;
    private static final String CONSUMER_NAME = "Gateway Command Consumer";
    private static final DestinationData REPLY_DESTINATION = new DestinationData(JmsKeys.GW_COMMAND_REPLY_QUEUE, DestinationType.QUEUE);

    public CommandMessagesBridge(@Requires JmsProvider jmsProvider, @Requires CommandSender commandSender) {
        Preconditions.checkArgument(jmsProvider != null, "JMS Provider cannot be null");
        Preconditions.checkArgument(commandSender != null, "CommandSender cannot be null");
        _jmsProvider = jmsProvider;
        _commandSender = commandSender;

        // Listen for incoming messages in GW_COMMAND_TOPIC
        _messageConsumer = new BaseMessageConsumer(
                CONSUMER_NAME,
                new DestinationData(JmsKeys.GW_COMMAND_TOPIC, DestinationType.TOPIC),
                this
        );
    }

    @Override
    public void onMessage(Message message) {
        try {
            decodeMessageAndProcess(message);
        } catch (FormatException e) {
            LOG.log(Level.WARNING, "Message did not contain a command message: " + e.getMessage());
        } catch (JMSException e) {
            //this is produced when sending initial reply back to the client
            LOG.log(Level.WARNING, "Problem sending response back to client ", e);
        }
    }

    private void decodeMessageAndProcess(Message message) throws JMSException {
        CommandMessageParser messageParser = new CommandMessageParser(message);
        if (messageParser.doesMessageContainACommand()) {
            Command command = messageParser.readCommand();
            LOG.info("New command arrived: " + command + " with correlationID " + message.getJMSCorrelationID());

            // TODO The listeners should be released at some point. We need to create an entity to keep
            // track of the listeners and release them when the command is effectively completed
            JmsForwardingCompletionListener listener = setupCompletionListener(message.getJMSCorrelationID());

            HandlerResponse response = _commandSender.sendCommand(command, listener);

            listener.sendInitialResponse(response);
        } else {
            LOG.warning("Invalid Command message arrived: " + message);
        }
    }

    private JmsForwardingCompletionListener setupCompletionListener(String correlationID) throws JMSException {
        JmsForwardingCompletionListener listener = new JmsForwardingCompletionListener(REPLY_DESTINATION, correlationID);
        listener.startJms(_jmsProvider);
        return listener;
    }

    @Validate
    public void startListeningForMessages() throws JMSException {
        _messageConsumer.startJms(_jmsProvider);
    }

    @Invalidate
    public void stopListeningForMessages() {
        this._messageConsumer.stopJms();
    }

}
