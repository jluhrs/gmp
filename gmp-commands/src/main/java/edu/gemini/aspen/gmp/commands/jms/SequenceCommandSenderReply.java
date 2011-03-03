package edu.gemini.aspen.gmp.commands.jms;

import edu.gemini.aspen.giapi.util.jms.MessageBuilder;
import edu.gemini.jms.api.JmsMapMessageSenderReply;
import edu.gemini.jms.api.MessagingException;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;

import javax.jms.Message;
import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * This is the implementation of the JMS producer object that will
 * send sequence commands down to the instrument. This class implements
 * the {@link edu.gemini.aspen.gmp.commands.model.ActionSender} interface
 * to simplify the usage (and consistency) across the different
 * {@link edu.gemini.aspen.gmp.commands.model.SequenceCommandExecutor}
 */
public class SequenceCommandSenderReply extends JmsMapMessageSenderReply<HandlerResponse>
        implements ActionSender {

    public SequenceCommandSenderReply(String clientData) {
        super(clientData);
    }

    @Override
    protected HandlerResponse buildResponse(Message reply) throws JMSException {
        if (reply instanceof MapMessage) {
            MapMessage replyMap = (MapMessage) reply;
            return MessageBuilder.buildHandlerResponse(replyMap);
        } 
        return HandlerResponse.NOANSWER;

    }

    @Override
    public HandlerResponse send(ActionMessage actionMessage)
            throws SequenceCommandException {

        try {
            Object o = sendMapMessageReply(
                    actionMessage.getDestinationData(),
                    actionMessage.getDataElements(),
                    actionMessage.getProperties(),
                    500);
            if (o instanceof HandlerResponse)
                return (HandlerResponse) o;
            else
                throw new SequenceCommandException(
                        "Invalid answer received for sequence command message");

        } catch (MessagingException e) {
            throw new SequenceCommandException("Unable to send action", e);
        }


    }
}
