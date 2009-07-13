package edu.gemini.aspen.gmp.commands.jms;

import edu.gemini.jms.api.JmsMapMessageSenderReply;
import edu.gemini.jms.api.MessagingException;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.commands.ActionSender;
import edu.gemini.aspen.gmp.commands.ActionMessage;
import edu.gemini.aspen.gmp.commands.SequenceCommandException;
import edu.gemini.aspen.gmp.util.jms.GmpJmsUtil;
import edu.gemini.aspen.gmp.util.commands.HandlerResponseImpl;

import javax.jms.Message;
import javax.jms.JMSException;
import javax.jms.MapMessage;

/**
 * This is the implementation of the JMS producer object that will
 * send sequence commands down to the instrument. This class implements
 * the {@link edu.gemini.aspen.gmp.commands.ActionSender} interface
 * to simplify the usage (and consistency) across the different
 * {@link edu.gemini.aspen.gmp.commands.SequenceCommandExecutor}
 */
public class SequenceCommandSenderReply extends JmsMapMessageSenderReply
        implements ActionSender {

    public SequenceCommandSenderReply(String clientData) {
        super(clientData);
    }

    protected Object buildResponse(Message reply) throws JMSException {

        if (reply instanceof MapMessage) {
            MapMessage replyMap = (MapMessage) reply;
            return GmpJmsUtil.buildHandlerResponse(replyMap);
        } 
        return HandlerResponseImpl.create(HandlerResponse.Response.NOANSWER);

    }

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
