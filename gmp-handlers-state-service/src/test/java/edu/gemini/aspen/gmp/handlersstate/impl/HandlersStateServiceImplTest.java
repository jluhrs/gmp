package edu.gemini.aspen.gmp.handlersstate.impl;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Unit Tests for the HandlersStateService, we need to start an ActiveMQ Broker and
 * add a few handlers as in GPI to detect them via AdvisoryMessages
 */
public class HandlersStateServiceImplTest {

    @Test
    public void testBasic() {
        assertNotNull(new HandlersStateServiceImpl());
    }
}
