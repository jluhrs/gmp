package edu.gemini.giapi.tool.arguments;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeoutArgumentTest {
    @Test
    public void basicPropertiesTests() {
        TimeoutArgument timeoutArgument = new TimeoutArgument();
        timeoutArgument.parseParameter("1000");

        assertTrue(timeoutArgument.requireParameter());
        assertFalse(timeoutArgument.getInvalidArgumentMsg().isEmpty());

        assertEquals(1000, timeoutArgument.getTimeout());
    }
}
