package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.jms.api.JmsMapMessageSenderReply;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

/**
 * Extension of {@link edu.gemini.jms.api.JmsMapMessageSenderReply} that can understand and decode a
 * reply of a {@link edu.gemini.aspen.giapi.commands.SequenceCommand} in the form
 * of a {@link edu.gemini.aspen.giapi.commands.HandlerResponse}
 */
class HandlerResponseSenderReply extends JmsMapMessageSenderReply<HandlerResponse> {
    public HandlerResponseSenderReply(String topicName) {
        super(topicName);
    }

    @Override
    public HandlerResponse buildResponse(Message reply) throws JMSException {
        if (reply instanceof MapMessage) {
            return MessageBuilder.buildHandlerResponse(reply);
        } else {
            return HandlerResponse.NOANSWER;
        }
    }
}