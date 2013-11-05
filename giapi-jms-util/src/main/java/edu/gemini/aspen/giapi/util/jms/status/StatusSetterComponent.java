package edu.gemini.aspen.giapi.util.jms.status;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.MessageBuilder;
import edu.gemini.jms.api.*;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * This class encapsulates the code to publish a StatusItem update.
 */
@Component
@Provides
@Instantiate
public class StatusSetterComponent extends MultiDestinationMessageProducer implements JmsArtifact, IStatusSetter {
    private Cache<String, StatusItem> itemsCache = CacheBuilder.newBuilder().weakValues().maximumSize(500).build();

    public StatusSetterComponent() {
        super("StatusSetterComponent");
    }

    /**
     * Sets a status item name and value
     *
     * @param statusItem item to send to the Status Database
     * @throws javax.jms.JMSException
     */
    @Override
    public boolean setStatusItem(StatusItem statusItem) throws JMSException {
        if (isConnected()) {
            Message m = MessageBuilder.buildStatusItemMessage(_session, statusItem);
            send(m, new DestinationData(JmsKeys.GMP_STATUS_DESTINATION_PREFIX + statusItem.getName(), DestinationType.TOPIC));
            return true;
        } else {
            LOG.warning("Trying to send a StatusItem update before starting Jms. This item will be lost: " + statusItem);
            itemsCache.put(statusItem.getName(), statusItem);
            return false;
        }
    }

    @Override
    public void startJms(JmsProvider provider) throws JMSException {
        super.startJms(provider);
        LOG.info("Starting JMS on status setter with " + itemsCache.size() + " items in the cache");
        for (StatusItem item: itemsCache.asMap().values()) {
            setStatusItem(item);
        }
        itemsCache.cleanUp();
    }

}
