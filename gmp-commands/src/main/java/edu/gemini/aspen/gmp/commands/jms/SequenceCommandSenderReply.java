package edu.gemini.aspen.gmp.commands.jms;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import edu.gemini.jms.api.JmsMapMessageSenderReply;
import edu.gemini.jms.api.MessagingException;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

/**
 * This is the implementation of the JMS producer object that will
 * send sequence commands down to the instrument. This class implements
 * the {@link edu.gemini.aspen.gmp.commands.model.ActionSender} interface
 * to simplify the usage (and consistency) across the different
 * {@link edu.gemini.aspen.gmp.commands.model.SequenceCommandExecutor}
 */
//@Provides(specifications = ActionSender.class)
public class SequenceCommandSenderReply extends JmsMapMessageSenderReply<HandlerResponse>
        implements ActionSender {

    public static final int RESPONSE_WAIT_TIMEOUT = 500;

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
        return send(actionMessage, RESPONSE_WAIT_TIMEOUT);
    }

    @Override
    public HandlerResponse send(ActionMessage actionMessage, long timeout) {
        try {
            return sendMapMessageReply(
                    actionMessage.getDestinationData(),
                    actionMessage.getDataElements(),
                    actionMessage.getProperties(),
                    timeout);
        } catch (MessagingException e) {
            throw new SequenceCommandException("Unable to send action", e);
        }
    }
}
