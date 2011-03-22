package edu.gemini.jms.api;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DestinationDataTest {
    @Test
    public void buildTopicDestination() {
        DestinationData destinationData = new DestinationData("topic1", DestinationType.TOPIC);
        assertEquals("topic1", destinationData.getName());
        assertEquals(DestinationType.TOPIC, destinationData.getType());
    }

    @Test
    public void buildQueueDestination() {
        DestinationData destinationData = new DestinationData("queue1", DestinationType.QUEUE);
        assertEquals("queue1", destinationData.getName());
        assertEquals(DestinationType.QUEUE, destinationData.getType());
    }

    @Test
    public void testEqualityOverType() {
        DestinationData a = new DestinationData("topic1", DestinationType.TOPIC);
        DestinationData b = new DestinationData("topic1", DestinationType.TOPIC);
        DestinationData c = new DestinationData("topic1", DestinationType.QUEUE);

        new EqualsTester(a, b, c, null);
    }

    @Test
    public void testEqualityOverName() {
        DestinationData a = new DestinationData("topic1", DestinationType.TOPIC);
        DestinationData b = new DestinationData("topic1", DestinationType.TOPIC);
        DestinationData c = new DestinationData("topic2", DestinationType.TOPIC);

        new EqualsTester(a, b, c, null);
    }

    @Test
    public void testToString() {
        DestinationData a = new DestinationData("topic1", DestinationType.TOPIC);

        assertTrue(a.toString().contains("topic1"));
    }

}
