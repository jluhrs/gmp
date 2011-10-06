package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;

import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.util.jms.HandlerResponseMessageParser;
import edu.gemini.jms.api.JmsMapMessageSenderReply;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;

/**
 * Extension of {@link edu.gemini.jms.api.JmsMapMessageSenderReply<T>} that can understand and decode a
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
            HandlerResponseMessageParser messageParser = new HandlerResponseMessageParser(reply);
            return messageParser.readResponse();
        } else {
            return HandlerResponse.NOANSWER;
        }
    }

    /**
     * This SenderReply object will listen based only on the temporary topic created to get responses
     * and set in the getJmsReplyTo method
     */
    @Override
    protected MessageConsumer createReplyConsumer(Message requestMessage) throws JMSException {
        return _session.createConsumer(requestMessage.getJMSReplyTo());
    }

}