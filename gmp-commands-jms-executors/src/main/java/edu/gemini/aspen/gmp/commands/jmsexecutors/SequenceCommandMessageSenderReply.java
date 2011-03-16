package edu.gemini.aspen.gmp.commands.jmsexecutors;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.jms.api.JmsMapMessageSenderReply;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

public class SequenceCommandMessageSenderReply extends JmsMapMessageSenderReply<HandlerResponse> {
    public SequenceCommandMessageSenderReply(String clientData) {
        super(clientData);
    }

    @Override
    public HandlerResponse buildResponse(Message reply) throws JMSException {
        if (reply instanceof MapMessage) {
            MapMessage replyMap = (MapMessage) reply;
            return MessageBuilder.buildHandlerResponse(replyMap);
        }
        return HandlerResponse.NOANSWER;

    }
}