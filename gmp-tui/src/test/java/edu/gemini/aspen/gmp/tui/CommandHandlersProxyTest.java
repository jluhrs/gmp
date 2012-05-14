package edu.gemini.aspen.gmp.tui;

import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import edu.gemini.aspen.gmp.commands.handlers.CommandHandlers;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CommandHandlersProxyTest {
    private CommandHandlersProxy commandHandlersProxy;
    private CommandHandlers commandHandlers;

    @Before
    public void setUp() throws Exception {
        commandHandlers = mock(CommandHandlers.class);
        commandHandlersProxy = new CommandHandlersProxy(commandHandlers);
    }

    @Test
    public void verifyScope() {
        assertEquals("gmp", commandHandlersProxy.SCOPE);
    }

    @Test
    public void verifyFunctions() {
        assertEquals("applyhandlers", commandHandlersProxy.FUNCTIONS[0]);
    }

    @Test
    public void testStatusNamesCall() {
        commandHandlersProxy.applyhandlers();

        verify(commandHandlers).getApplyHandlers();
    }

}
