package edu.gemini.aspen.gmp.logging;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class LoggingMessageConsumerTest {
    @Test
    public void testConstruction() {
        assertNotNull(new LoggingMessageConsumer());
    }
}
