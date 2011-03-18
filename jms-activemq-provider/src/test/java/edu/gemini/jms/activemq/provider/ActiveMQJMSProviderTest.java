package edu.gemini.jms.activemq.provider;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ActiveMQJMSProviderTest {
    @Test
    public void testConstruction() {
        String brokerUrl = "vm:testBroker?persistent=false";
        ActiveMQJmsProvider provider = new ActiveMQJmsProvider(brokerUrl);

        assertNotNull(provider.getConnectionFactory());
    }
}
