package edu.gemini.jms.api;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

public class JmsMapMessageSenderTest {
    @Test
    public void testSendMapMessage() {
        JmsMapMessageSender sender = new JmsMapMessageSender("GMP.TOPIC");
        Map<String,Object> message = ImmutableMap.of();
        Map<String,Object> properties = ImmutableMap.of();
        DestinationData destination = new DestinationData("GMP.TOPIC", DestinationType.TOPIC);
        
        sender.sendMapMessage(destination, message, properties);
    }
}
