package edu.gemini.jms.activemq.provider;

import edu.gemini.jms.api.JmsProviderStatusListener;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ActiveMQJMSProviderTest {
    @Test
    public void testConstruction() {
        String brokerUrl = "vm:testBroker?broker.persistent=false";
        ActiveMQJmsProvider provider = new ActiveMQJmsProvider(brokerUrl, "1000");
        provider.startConnection();

        assertNotNull(provider.getConnectionFactory());
    }

    @Test
    public void testConstructionWithPropertySubstitution() {
        String brokerUrl = "${address}?broker.persistent=false";
        System.setProperty("address", "vm:testBroker");
        ActiveMQJmsProvider provider = new ActiveMQJmsProvider(brokerUrl, "1000");
        provider.startConnection();

        assertNotNull(provider.getConnectionFactory());
    }

    @Test
    public void addStatusListener() throws InterruptedException, JMSException {
        String brokerUrl = "failover:(vm:testBroker?broker.persistent=false)";
        ActiveMQJmsProvider provider = new ActiveMQJmsProvider(brokerUrl, "1000");

        final AtomicBoolean resumed = new AtomicBoolean(false);

        provider.bindJmsStatusListener(new JmsProviderStatusListener() {
            @Override
            public void transportResumed() {
                resumed.set(true);
            }

            @Override
            public void transportInterrupted() {
            }
        });

        assertFalse(resumed.get());

        provider.getConnectionFactory().createConnection();

        TimeUnit.SECONDS.sleep(2);

        assertTrue(resumed.get());
    }
}
