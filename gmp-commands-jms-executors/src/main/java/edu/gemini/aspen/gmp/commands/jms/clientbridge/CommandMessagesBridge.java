package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsMapMessageSender;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Map;
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

    public CommandMessagesBridge(@Requires JmsProvider jmsProvider, @Requires CommandSender commandSender) {
        Preconditions.checkArgument(jmsProvider != null, "JMS Provider cannot be null");
        Preconditions.checkArgument(commandSender != null, "CommandSender cannot be null");
        _jmsProvider = jmsProvider;
        _commandSender = commandSender;

        _messageConsumer = new BaseMessageConsumer(
                CONSUMER_NAME,
                new DestinationData(JmsKeys.GW_COMMAND_TOPIC, DestinationType.TOPIC),
                this
        );
    }

    @Validate
    public void startListeningForMessages() {
        try {
            _messageConsumer.startJms(_jmsProvider);
        } catch (JMSException e) {
            LOG.log(Level.SEVERE, "Exception while starting the provider", e);
        }
    }

    @Invalidate
    public void stopListeningForMessages() {
        this._messageConsumer.stopJms();
    }

    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            try {
                Command command = CommandMessageParser.readCommand((MapMessage) message);
                LOG.info("New command arrived: " + command);

                Destination replyDestination = message.getJMSReplyTo();
                JmsForwardingCompletionListener listener = new JmsForwardingCompletionListener(replyDestination);
                listener.startJms(_jmsProvider);
                HandlerResponse response = _commandSender.sendCommand(command, listener);

                //send response back to the client
                sendResponse(message.getJMSReplyTo(), response);
            } catch (FormatException e) {
                LOG.log(Level.WARNING, "Message did not contain a command message: " + e.getMessage());
            } catch (JMSException e) {
                //this is produced when sending reply back to the client
                LOG.log(Level.WARNING, "Problem sending response back to client ", e);
            }
        }
    }

    private void sendResponse(Destination destination, HandlerResponse response) throws JMSException {
        Map<String, String> content = CommandMessageSerializer.convertHandlerResponseToProperties(response);

        JmsMapMessageSender messageSender = new JmsMapMessageSender("");
        messageSender.startJms(_jmsProvider);
        messageSender.sendStringBasedMapMessage(destination, content, ImmutableMap.<String, String>of());
        messageSender.stopJms();
    }

}
