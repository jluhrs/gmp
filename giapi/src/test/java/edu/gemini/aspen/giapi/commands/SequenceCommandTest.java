package edu.gemini.aspen.giapi.commands;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class SequenceCommandTest {
    @Test
    public void trivialTestOfGetName() {
        assertEquals("ABORT", SequenceCommand.ABORT.toString());
    }

    @Test
    public void testCommandsWithNoConfig() {
        assertFalse(SequenceCommand.commandWithNoConfig().contains(SequenceCommand.APPLY));
    }
}
