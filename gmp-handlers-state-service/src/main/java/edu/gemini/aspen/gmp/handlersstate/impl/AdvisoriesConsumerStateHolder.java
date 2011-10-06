package edu.gemini.aspen.gmp.handlersstate.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.RemoveInfo;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A ConsumerStateHolder that gets its information out of the advisory messages
 * provided by ActiveMQ
 * <br>
 * See <a href="http://activemq.apache.org/advisory-message.html">Advisory Messages</a>
 */
public class AdvisoriesConsumerStateHolder extends BaseMessageConsumer implements MessageListener, ConsumerStateHolder {
    private static final Logger LOG = Logger.getLogger(AdvisoriesConsumerStateHolder.class.getName());
    private final ConcurrentMap<String, MessageSubscriber> advisoryBasedSubscribers = Maps.newConcurrentMap();


    public AdvisoriesConsumerStateHolder() {
        super("AdvisoriesConsumerStateHolder", new DestinationData("ActiveMQ.Advisory.Consumer.>", DestinationType.TOPIC));
        setMessageListener(this);
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ActiveMQMessage) {
            LOG.fine("Advisory message arrived " + ((ActiveMQMessage) message).getMessageId());
            try {
                processAdvisoryMessage((ActiveMQMessage) message);
            } catch (ClassCastException e) {
                LOG.log(Level.SEVERE, "Expected a ConsumerInfo in the ActiveMQ Message", e);
            } catch (JMSException e) {
                LOG.log(Level.SEVERE, "JMSException while processing message", e);
            }
        } else {
            LOG.warning("Arrived non advisory message " + message);
        }
    }

    private void processAdvisoryMessage(ActiveMQMessage message) throws JMSException {
        ActiveMQMessage amqMsg = (ActiveMQMessage) message;
        if (isConsumerAdded(amqMsg)) {
            addConsumerInfo(amqMsg);
        } else if (isConsumerRemoved(amqMsg)) {
            removeConsumerInfo(amqMsg);
        } else {
            LOG.warning("Unknown type of Advisory: " + message);
        }
    }

    private boolean isConsumerAdded(ActiveMQMessage amqMsg) {
        return amqMsg.getDataStructure() instanceof ConsumerInfo;
    }

    private void addConsumerInfo(ActiveMQMessage amqMsg) {
        ConsumerInfo consumerInfo = (ConsumerInfo) amqMsg.getDataStructure();
        MessageSubscriber messageSubscriber = new MessageSubscriber(consumerInfo.getConsumerId().getConnectionId(), consumerInfo.getDestination().getQualifiedName());
        advisoryBasedSubscribers.putIfAbsent(consumerInfo.getConsumerId().toString(), messageSubscriber);
    }

    private boolean isConsumerRemoved(ActiveMQMessage amqMsg) {
        return amqMsg.getDataStructure() instanceof RemoveInfo;
    }

    private void removeConsumerInfo(ActiveMQMessage amqMsg) {
        RemoveInfo consumerInfo = (RemoveInfo) amqMsg.getDataStructure();
        advisoryBasedSubscribers.remove(consumerInfo.getObjectId().toString());
    }
    
    @Override
    public List<MessageSubscriber> listSubscribers() {
        return ImmutableList.copyOf(advisoryBasedSubscribers.values());
    }
}
