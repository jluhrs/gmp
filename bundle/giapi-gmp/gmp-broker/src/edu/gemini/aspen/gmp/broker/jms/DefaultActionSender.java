package edu.gemini.aspen.gmp.broker.jms;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.broker.commands.Action;
import edu.gemini.aspen.gmp.broker.commands.HandlerResponseImpl;
import edu.gemini.aspen.gmp.broker.commands.ActionSender;

import javax.jms.Destination;
import javax.jms.JMSException;
import java.util.logging.Logger;

/**
 * The default mechanism to send actions.
 */
public class DefaultActionSender implements ActionSender {
    private static final Logger LOG = Logger.getLogger(DefaultActionSender.class.getName());

    JMSActionMessageProducer _producer;

    public DefaultActionSender() {
        LOG.info("Initializing Default Action Sender");
        _producer = new JMSActionMessageProducer();
    }


    public HandlerResponse send(Action action) {
        try {
            ActionMessage m = _producer.createActionMessage(action);

            m.setConfiguration(action.getConfiguration());
          
            //the topic for this
            Destination topic = _producer.createTopic(action.getSequenceCommand());
            
            //send the message to the topic
            return _producer.send(topic, m);

        } catch (JMSException e) {
            LOG.warning("Exception while sending action:" + action + ": " + e);
        }
        return HandlerResponseImpl.createError(
                "No answer received to action " + action);
    }
}
