package edu.gemini.aspen.gmp.handlersstate.impl;

import edu.gemini.aspen.gmp.handlersstate.HandlersStateService;
import org.junit.Test;

/**
 * Unit Tests for the HandlersStateService, we need to start an ActiveMQ Broker and
 * add a few handlers as in GPI to detect them via AdvisoryMessages
 */
public class HandlersStateServiceImplTest {
    private HandlersStateService handlerStateService;

    @Test
    public void testBasic() throws Exception {
        handlerStateService = new HandlersStateServiceImpl();
    }
}

