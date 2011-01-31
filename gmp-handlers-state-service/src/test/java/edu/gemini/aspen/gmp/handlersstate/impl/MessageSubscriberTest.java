package edu.gemini.aspen.gmp.handlersstate.impl;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Trivial Tests for MessageSubscriber
 */
public class MessageSubscriberTest {
    @Test
    public void testMessageSubscriber() {
        assertNotNull(new MessageSubscriber("clientId", "subscriptionName"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadConstructorArgument() {
        new MessageSubscriber(null, "subscriptionName");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadConstructorArgument2() {
        new MessageSubscriber("clientId", null);
    }

    @Test
    public void testEquality() {
        MessageSubscriber a = new MessageSubscriber("clientId", "subscriptionName");
        MessageSubscriber b = new MessageSubscriber("clientId", "subscriptionName");
        MessageSubscriber c = new MessageSubscriber("anotherClientId", "subscriptionName");
        MessageSubscriber d = new MessageSubscriber("clientId", "subscriptionName") {
        };

        new EqualsTester(a, b, c, d);
    }
}
