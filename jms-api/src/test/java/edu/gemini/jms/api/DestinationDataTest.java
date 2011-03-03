package edu.gemini.jms.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DestinationDataTest {
    @Test
    public void buildTopicDestination() {
        DestinationData destinationData = new DestinationData("queue1", DestinationType.TOPIC);
        assertEquals("queue1", destinationData.getName());
        assertEquals(DestinationType.TOPIC, destinationData.getType());
    }

    @Test
    public void buildQueueDestination() {
        DestinationData destinationData = new DestinationData("queue1", DestinationType.QUEUE);
        assertEquals("queue1", destinationData.getName());
        assertEquals(DestinationType.QUEUE, destinationData.getType());
    }
}
