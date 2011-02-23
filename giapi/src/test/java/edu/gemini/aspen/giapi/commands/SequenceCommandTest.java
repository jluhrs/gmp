package edu.gemini.aspen.giapi.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SequenceCommandTest {
    @Test
    public void trivialTestOfGetName() {
        assertEquals("ABORT", SequenceCommand.ABORT.getName());
    }
}
