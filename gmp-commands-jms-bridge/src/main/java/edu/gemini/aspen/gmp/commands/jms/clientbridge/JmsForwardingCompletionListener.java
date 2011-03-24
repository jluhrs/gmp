package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionInformation;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.MessageBuilderFactory;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.JmsMapMessageSender;
import edu.gemini.jms.api.MapMessageBuilder;

import java.util.logging.Logger;

/**
 * This class is an internal listener that gets notified when an Action is completed
 * and then forwards the completion message to a client to the destination in the
 * reply of the command request
 * <p/>
 * TODO This class has two responsibilities - separate them
 */
public class JmsForwardingCompletionListener extends JmsMapMessageSender implements CompletionListener {
    private static final Logger LOG = Logger.getLogger(JmsForwardingCompletionListener.class.getName());
    private final DestinationData _replyDestination;
    private final String _correlationID;

    public JmsForwardingCompletionListener(DestinationData replyDestination, String correlationID) {
        super("ForwardingListener on correlationID: " + correlationID);
        Preconditions.checkArgument(replyDestination != null, "Destination to send responses cannot be null");
        Preconditions.checkArgument(correlationID != null && !correlationID.isEmpty(), "All Command messages require a _correlationID");

        _replyDestination = replyDestination;
        this._correlationID = correlationID;
    }

    /**
     * Sends to the client that sent the command the initial response to the request
     *
     * @param response The initial response returned by CommandSender.sendCommand
     */
    void sendInitialResponse(HandlerResponse response) {
        logSendingReply(response);

        MapMessageBuilder responseMessageBuilder = MessageBuilderFactory.newMessageBuilder(response, _correlationID);
        super.sendMapMessage(_replyDestination, responseMessageBuilder);
    }

    private void logSendingReply(Object objectToSend) {
        LOG.fine("Sent initial response " + objectToSend+ " to " + _replyDestination + " " + _correlationID);
    }

    void stopListeningForResponses() {
        super.stopJms();
    }

    @Override
    public void onHandlerResponse(HandlerResponse response, Command command) {
        sendCompletionResponse(new CompletionInformation(response, command));

        stopListeningForResponses();
    }

    private void sendCompletionResponse(CompletionInformation completionInformation) {
        logSendingReply(completionInformation);

        MapMessageBuilder messageBuilder = MessageBuilderFactory.newMessageBuilder(completionInformation, _correlationID);
        super.sendMapMessage(_replyDestination, messageBuilder);
    }

    @Override
    public String toString() {
        return "ForwardingCompletionListener on " + _replyDestination.getName();
    }

}
