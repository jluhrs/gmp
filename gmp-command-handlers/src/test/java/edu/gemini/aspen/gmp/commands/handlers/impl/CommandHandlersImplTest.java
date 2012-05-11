package edu.gemini.aspen.gmp.commands.handlers.impl;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommandHandlersImplTest {
    @Test
    public void testConstruction() {
        assertNotNull(new CommandHandlersImpl());
    }

    @Test
    public void testGetApplyHandlers() {
        assertTrue(new CommandHandlersImpl().getApplyHandlers().isEmpty());
    }
}
